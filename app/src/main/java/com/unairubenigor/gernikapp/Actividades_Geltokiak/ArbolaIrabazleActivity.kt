package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.unairubenigor.gernikapp.R
import com.unairubenigor.gernikapp.ikasle.IkasleMenuActivity

class ArbolaIrabazleActivity : AppCompatActivity() {

    private lateinit var tvIrabazleIzena: TextView
    private lateinit var tvZurePuntuazioa: TextView
    private lateinit var btnItzuliMenura: Button
    private lateinit var progressBar: ProgressBar

    private val db = FirebaseFirestore.getInstance()

    private var gela: String = ""
    private var ikasleIzena: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arbola_irabazle)

        tvIrabazleIzena = findViewById(R.id.tvIrabazleIzena)
        tvZurePuntuazioa = findViewById(R.id.tvZurePuntuazioa)
        btnItzuliMenura = findViewById(R.id.btnItzuliMenura)
        progressBar = findViewById(R.id.progressBarIrabazle)

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "EMAITZAK"

        // --- ¡CORRECCIÓN DEFINITIVA! Aceptamos cualquiera de las dos claves ---
        val gelaCode = intent.getStringExtra("CLASS_CODE")
        val gelaCodeAlternativo = intent.getStringExtra("GELA_CODE")
        gela = gelaCode ?: gelaCodeAlternativo ?: ""
        ikasleIzena = intent.getStringExtra("USER_NAME") ?: ""

        btnItzuliMenura.setOnClickListener {
            val intent = Intent(this, IkasleMenuActivity::class.java)
            intent.putExtra("USER_NAME", ikasleIzena)
            intent.putExtra("CLASS_CODE", gela) // Usamos la clave estándar para devolverlo
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        if (gela.isNotEmpty() && ikasleIzena.isNotEmpty()) {
            kalkulatuEmaitzak(gela, ikasleIzena)
        } else {
            Toast.makeText(this, "Ezin izan dira emaitzak lortu (kodea edo izena falta da)", Toast.LENGTH_LONG).show()
            tvIrabazleIzena.text = "Errorea"
            tvZurePuntuazioa.text = "Ez da gelarik aurkitu"
        }
    }

    private fun kalkulatuEmaitzak(gela: String, ikasleIzena: String) {
        progressBar.visibility = View.VISIBLE

        db.collection("arbola_marrazkiak")
            .whereEqualTo("gela", gela)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Oraindik ez daude notak jarrita.", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    return@addOnSuccessListener
                }

                val puntuazioak = documents.mapNotNull { document ->
                    val izena = document.getString("izena")
                    val puntuazioa = document.getLong("puntuazioa")?.toInt()
                    if (izena != null && puntuazioa != null) Pair(izena, puntuazioa) else null
                }

                val topScores = puntuazioak.map { it.second }.distinct().sortedDescending().take(3)
                val scoreToPoints = mapOf(
                    (topScores.getOrNull(0) ?: -1) to 3,
                    (topScores.getOrNull(1) ?: -1) to 2,
                    (topScores.getOrNull(2) ?: -1) to 1
                )

                val winners = mutableListOf<String>()
                puntuazioak.forEach { (izena, puntuazioa) ->
                    val pointsToAdd = scoreToPoints[puntuazioa]
                    if (pointsToAdd != null && pointsToAdd > 0) {
                        if (!winners.any { it.startsWith(izena) }) {
                            winners.add("$izena ($puntuazioa p.)")
                        }
                        
                        val rankingRef = db.collection("ranking").document(gela).collection("ikasleak").document(izena)
                        rankingRef.update("puntuazioa", FieldValue.increment(pointsToAdd.toLong()))
                            .addOnFailureListener {
                                rankingRef.set(mapOf("puntuazioa" to pointsToAdd, "izena" to izena))
                            }
                    }
                }

                if (winners.isNotEmpty()) {
                    tvIrabazleIzena.text = "Irabazleak: ${winners.joinToString(", ")}"
                } else {
                    tvIrabazleIzena.text = "Ez dago irabazlerik"
                }

                val zurePuntuazioa = puntuazioak.find { it.first.equals(ikasleIzena, ignoreCase = true) }?.second
                if (zurePuntuazioa != null) {
                    tvZurePuntuazioa.text = "$zurePuntuazioa / 10"
                } else {
                    tvZurePuntuazioa.text = "Ez da zure nota aurkitu"
                }

                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Errorea emaitzak lortzean: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}