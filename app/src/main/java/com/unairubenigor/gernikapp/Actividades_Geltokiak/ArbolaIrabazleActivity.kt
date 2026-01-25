package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unairubenigor.gernikapp.R
import com.unairubenigor.gernikapp.ikasle.IkasleMenuActivity

class ArbolaIrabazleActivity : AppCompatActivity() {

    private lateinit var tvIrabazleIzena: TextView
    private lateinit var tvZurePuntuazioa: TextView
    private lateinit var btnItzuliMenura: Button
    private lateinit var progressBar: ProgressBar

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arbola_irabazle)

        // 1. Configurar vistas
        tvIrabazleIzena = findViewById(R.id.tvIrabazleIzena)
        tvZurePuntuazioa = findViewById(R.id.tvZurePuntuazioa)
        btnItzuliMenura = findViewById(R.id.btnItzuliMenura)
        progressBar = findViewById(R.id.progressBarIrabazle)

        // 2. Configurar Header
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "EMAITZAK"

        // 3. Configurar botón de volver
        btnItzuliMenura.setOnClickListener {
            val intent = Intent(this, IkasleMenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        // 4. Obtener datos y calcular resultados
        val gela = intent.getStringExtra("GELA_CODE") ?: ""
        val ikasleIzena = intent.getStringExtra("USER_NAME") ?: ""

        if (gela.isNotEmpty() && ikasleIzena.isNotEmpty()) {
            kalkulatuEmaitzak(gela, ikasleIzena)
        } else {
            Toast.makeText(this, "Ezin izan dira emaitzak lortu (kodea edo izena falta da)", Toast.LENGTH_LONG).show()
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

                var irabazleIzena = ""
                var puntuazioMaximoa = -1
                var zurePuntuazioa = -1

                for (document in documents) {
                    val izena = document.getString("izena") ?: ""
                    val puntuazioa = document.getLong("puntuazioa")?.toInt() ?: 0

                    // Kalkulatu irabazlea
                    if (puntuazioa > puntuazioMaximoa) {
                        puntuazioMaximoa = puntuazioa
                        irabazleIzena = izena
                    }

                    // Lortu zure puntuazioa
                    if (izena.equals(ikasleIzena, ignoreCase = true)) {
                        zurePuntuazioa = puntuazioa
                    }
                }

                // Erakutsi emaitzak
                tvIrabazleIzena.text = irabazleIzena
                if (zurePuntuazioa != -1) {
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