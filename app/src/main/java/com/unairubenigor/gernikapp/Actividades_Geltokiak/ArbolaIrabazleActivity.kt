package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.unairubenigor.gernikapp.R
import com.unairubenigor.gernikapp.ikasle.IkasleMenuActivity

class ArbolaIrabazleActivity : AppCompatActivity() {

    private lateinit var tvIrabazleIzena: TextView
    private lateinit var tvZurePuntuazioa: TextView
    private lateinit var btnItzuliMenura: Button
    private lateinit var progressBar: ProgressBar

    private val db = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null

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

        // Botón visible por defecto para que no desaparezca nunca
        btnItzuliMenura.visibility = View.VISIBLE
        tvZurePuntuazioa.text = "Zuzentzen..."
        tvIrabazleIzena.text = "Emaitzen zain..."

        val gelaCode = intent.getStringExtra("CLASS_CODE")
        val gelaCodeAlternativo = intent.getStringExtra("GELA_CODE")
        gela = gelaCode ?: gelaCodeAlternativo ?: ""

        ikasleIzena = intent.getStringExtra("USER_NAME") ?: ""

        // Botón SALIR
        btnItzuliMenura.setOnClickListener {
            val intent = Intent(this, IkasleMenuActivity::class.java)
            intent.putExtra("USER_NAME", ikasleIzena)
            intent.putExtra("CLASS_CODE", gela)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        if (gela.isNotEmpty()) {
            escucharEmaitzak(gela, ikasleIzena)
        } else {
            tvIrabazleIzena.text = "Errorea: Kodea falta da"
        }
    }

    private fun escucharEmaitzak(gela: String, ikasleIzena: String) {
        progressBar.visibility = View.VISIBLE

        listener = db.collection("arbola_marrazkiak")
            .whereEqualTo("gela", gela)
            .addSnapshotListener { documents, e ->

                progressBar.visibility = View.GONE

                if (e != null) return@addSnapshotListener
                if (documents == null || documents.isEmpty) {
                    tvIrabazleIzena.text = "Marrazkiak bidaltzen..."
                    return@addSnapshotListener
                }

                // 1. Procesar datos (igual que en el Admin)
                val entradasRaw = documents.mapNotNull { document ->
                    val izena = document.getString("izena")
                    val puntuazioa = document.getLong("puntuazioa")?.toInt()
                    if (izena != null && puntuazioa != null && puntuazioa > 0) Pair(izena, puntuazioa) else null
                }

                if (entradasRaw.isEmpty()) {
                    tvIrabazleIzena.text = "Irakaslea zuzentzen..."
                    tvZurePuntuazioa.text = "Zuzentzen..."
                    return@addSnapshotListener
                }

                // 2. Agrupar
                val puntuazioakUnicas = entradasRaw
                    .groupBy { it.first }
                    .map { entry -> Pair(entry.key, entry.value.maxOf { it.second }) }

                // 3. Ganadores
                val topScores = puntuazioakUnicas.map { it.second }.distinct().sortedDescending().take(3)
                val scoreToPoints = mapOf(
                    (topScores.getOrNull(0) ?: -1) to 3,
                    (topScores.getOrNull(1) ?: -1) to 2,
                    (topScores.getOrNull(2) ?: -1) to 1
                )

                val winners = mutableListOf<String>()
                puntuazioakUnicas.forEach { (izena, notaDibujo) ->
                    val pointsToAdd = scoreToPoints[notaDibujo]
                    if (pointsToAdd != null && pointsToAdd > 0) {
                        winners.add("$izena ($notaDibujo/10)")
                    }
                }

                // 4. Mostrar Ganadores
                if (winners.isNotEmpty()) {
                    // Ordenamos para mostrar primero al que más nota tiene
                    val winnersSorted = winners.sortedByDescending {
                        it.substringAfter("(").substringBefore("/").toIntOrNull() ?: 0
                    }
                    tvIrabazleIzena.text = "${winnersSorted.joinToString("\n")}"
                } else {
                    tvIrabazleIzena.text = "Emaitzak prozesatzen..."
                }

                // 5. Mostrar Tu Nota
                val zurePuntuazioa = puntuazioakUnicas.find { it.first.equals(ikasleIzena, ignoreCase = true) }?.second

                if (zurePuntuazioa != null) {
                    tvZurePuntuazioa.text = "$zurePuntuazioa / 10"
                } else {
                    tvZurePuntuazioa.text = "Zuzentzen..."
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        listener?.remove()
    }
}