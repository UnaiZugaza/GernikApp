package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.unairubenigor.gernikapp.R

class ArbolaAdminActivity : AppCompatActivity() {

    private lateinit var rvLista: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnBidaliEmaitzak: Button

    private var codigoClase: String = ""
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistro: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arbola_admin)

        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "MARRAZKIAK ($codigoClase)"

        rvLista = findViewById(R.id.rvMarrazkiak)
        progressBar = findViewById(R.id.progressBarAdmin)
        btnBidaliEmaitzak = findViewById(R.id.btnBidaliEmaitzak)
        rvLista.layoutManager = LinearLayoutManager(this)

        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        btnBack.visibility = View.VISIBLE
        btnBack.setOnClickListener { finish() }

        // AL PULSAR: Calculamos puntos y LUEGO avisamos a los alumnos
        btnBidaliEmaitzak.setOnClickListener { comprobarYRepartir() }

        if (codigoClase.isNotEmpty()) {
            escucharDibujosEnTiempoReal()
            checkSiYaSeRepartio()
        }
    }

    private fun checkSiYaSeRepartio() {
        db.collection("gelak").document(codigoClase).get()
            .addOnSuccessListener { doc ->
                if (doc.exists() && doc.getBoolean("emaitzak_prest") == true) {
                    btnBidaliEmaitzak.text = "EMAITZAK JADA BIDALITA"
                    // No lo deshabilitamos del todo por si quieres reenviar, pero avisamos visualmente
                }
            }
    }

    private fun comprobarYRepartir() {
        if (codigoClase.isEmpty()) return

        btnBidaliEmaitzak.isEnabled = false
        progressBar.visibility = View.VISIBLE

        // 1. Verificamos si ya se dieron los puntos para no duplicar (Opcional, pero recomendado)
        db.collection("gelak").document(codigoClase).get()
            .addOnSuccessListener { doc ->
                if (doc.exists() && doc.getBoolean("emaitzak_prest") == true) {
                    // Ya se dieron, pero volvemos a enviar la señal por si algún alumno se quedó colgado
                    finalizarReparto()
                    Toast.makeText(this, "Emaitzak berriz bidaltzen...", Toast.LENGTH_SHORT).show()
                } else {
                    calcularYSumarPuntos()
                }
            }
            .addOnFailureListener {
                // Si falla la comprobación, intentamos calcular igualmente
                calcularYSumarPuntos()
            }
    }

    private fun calcularYSumarPuntos() {
        db.collection("arbola_marrazkiak")
            .whereEqualTo("gela", codigoClase)
            .get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty) {
                    // Si no hay dibujos, no sumamos puntos, pero AVISAMOS para que pasen de pantalla
                    finalizarReparto()
                    return@addOnSuccessListener
                }

                // 2. Agrupar por alumno (quedarse con la mejor nota para evitar duplicados)
                val entradasRaw = documents.mapNotNull { document ->
                    val izena = document.getString("izena")
                    val puntuazioa = document.getLong("puntuazioa")?.toInt()
                    if (izena != null && puntuazioa != null) Pair(izena, puntuazioa) else null
                }

                val puntuazioakUnicas = entradasRaw
                    .groupBy { it.first }
                    .map { entry -> Pair(entry.key, entry.value.maxOf { it.second }) }

                // 3. Calcular TOP 3
                val topScores = puntuazioakUnicas.map { it.second }.distinct().sortedDescending().take(3)

                val scoreToPoints = mapOf(
                    (topScores.getOrNull(0) ?: -1) to 3,
                    (topScores.getOrNull(1) ?: -1) to 2,
                    (topScores.getOrNull(2) ?: -1) to 1
                )

                val batch = db.batch()
                var hayPuntos = false

                puntuazioakUnicas.forEach { (izena, notaDibujo) ->
                    val pointsToAdd = scoreToPoints[notaDibujo]
                    if (pointsToAdd != null && pointsToAdd > 0) {
                        hayPuntos = true

                        // --- CORRECCIÓN VITAL: Escribimos en 'partidas' (donde lee el Ranking) ---
                        val jugadorRef = db.collection("partidas").document(codigoClase)
                            .collection("jugadores").document(izena)

                        // Usamos set(..., merge()) para que si el jugador no existe, lo cree y no falle el batch
                        val data = hashMapOf(
                            "puntuacion" to FieldValue.increment(pointsToAdd.toLong())
                        )
                        batch.set(jugadorRef, data, SetOptions.merge())
                    }
                }

                if (hayPuntos) {
                    batch.commit().addOnCompleteListener {
                        // Tanto si sale bien como si falla, enviamos la señal de "FIN" a los alumnos
                        finalizarReparto()
                    }
                } else {
                    finalizarReparto()
                }
            }
            .addOnFailureListener {
                // Si falla leer los dibujos, desbloqueamos
                progressBar.visibility = View.GONE
                btnBidaliEmaitzak.isEnabled = true
                Toast.makeText(this, "Errorea marrazkiak irakurtzean", Toast.LENGTH_SHORT).show()
            }
    }

    private fun finalizarReparto() {
        // --- ESTO ES LO QUE HACE SALTAR DE PANTALLA A LOS ALUMNOS ---
        // Usamos SetOptions.merge() para asegurar que no falle si el documento no existe
        db.collection("gelak").document(codigoClase)
            .set(hashMapOf("emaitzak_prest" to true), SetOptions.merge())
            .addOnCompleteListener {
                progressBar.visibility = View.GONE
                btnBidaliEmaitzak.text = "EMAITZAK BIDALITA"
                Toast.makeText(this, "Puntuak banatu eta ikasleei abisatu zaie!", Toast.LENGTH_LONG).show()
                finish() // Cerramos la pantalla del profe
            }
    }

    private fun escucharDibujosEnTiempoReal() {
        listenerRegistro = db.collection("arbola_marrazkiak")
            .whereEqualTo("gela", codigoClase)
            .orderBy("data", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    val lista = snapshots.map { doc ->
                        val m = doc.toObject(Marrazkia::class.java)
                        m.id = doc.id
                        m
                    }
                    rvLista.adapter = MarrazkiakAdapter(lista)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistro?.remove()
    }
}