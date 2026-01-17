package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.unairubenigor.gernikapp.R
import java.util.Calendar
import java.util.Date

class AstraAdminActivity : AppCompatActivity() {

    private lateinit var tvCodigo: TextView
    private lateinit var tvInfo: TextView
    private lateinit var btnRepartir: Button
    private lateinit var btnReiniciar: Button

    private var codigoClase: String = ""
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_astra_admin)

        tvCodigo = findViewById(R.id.tvCodigoGenerado)
        tvInfo = findViewById(R.id.tvInfoJugadores)
        btnRepartir = findViewById(R.id.btnRepartirRoles)
        btnReiniciar = findViewById(R.id.btnReiniciar)

        // Ya no necesitamos limpiar manualmente, Google lo hará solo.
        generarNuevaSala()

        btnRepartir.setOnClickListener { repartirRoles() }
        btnReiniciar.setOnClickListener { generarNuevaSala() }
    }

    private fun generarNuevaSala() {
        val numeroRandom = (100000..999999).random()
        codigoClase = numeroRandom.toString()
        tvCodigo.text = codigoClase

        // --- CALCULAR FECHA DE CADUCIDAD (2 DÍAS) ---
        val calendario = Calendar.getInstance()
        calendario.add(Calendar.DAY_OF_YEAR, 2) // Sumamos 2 días a hoy
        val fechaMuerte = calendario.time // Objeto Date

        val datosSala = hashMapOf(
            "estado" to "ESPERANDO",
            "asesino" to "",
            "fechaExpiracion" to fechaMuerte // Guardamos la fecha futura
        )

        db.collection("partidas").document(codigoClase)
            .set(datosSala)
            .addOnSuccessListener {
                Toast.makeText(this, "Sala creada (Caduca en 48h)", Toast.LENGTH_SHORT).show()
                escucharJugadores()
            }
    }

    private fun escucharJugadores() {
        db.collection("partidas").document(codigoClase)
            .collection("jugadores")
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                val numero = snapshots?.size() ?: 0
                tvInfo.text = "Jugadores conectados: $numero"
            }
    }

    private fun repartirRoles() {
        val jugadoresRef = db.collection("partidas").document(codigoClase).collection("jugadores")

        jugadoresRef.get().addOnSuccessListener { result ->
            val listaJugadores = result.documents
            val total = listaJugadores.size

            if (total < 3) {
                Toast.makeText(this, "Mínimo 3 jugadores", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            val numPolis = if (total <= 5) 1 else 2
            val numAsesinos = if (total <= 5) 1 else 2

            val listaBarajada = listaJugadores.shuffled()
            val batch = db.batch()
            var nombresAsesinos = ""

            listaBarajada.forEachIndexed { index, doc ->
                var rol = "PUEBLO"
                if (index < numPolis) {
                    rol = "POLICIA"
                } else if (index < numPolis + numAsesinos) {
                    rol = "ASESINO"
                    nombresAsesinos += "${doc.id} "
                }

                batch.update(doc.reference, "rol", rol)
                batch.update(doc.reference, "vivo", true)
                // Opcional: Reiniciar estado visual
                batch.update(doc.reference, "ultimoAtaque", 0L)
            }

            val partidaRef = db.collection("partidas").document(codigoClase)
            batch.update(partidaRef, "estado", "JUGANDO")
            batch.update(partidaRef, "asesino", nombresAsesinos.trim())

            batch.commit().addOnSuccessListener {
                Toast.makeText(this, "Juego iniciado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}