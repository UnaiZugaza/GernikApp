package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.unairubenigor.gernikapp.R

class AstraAdminActivity : AppCompatActivity() {

    private lateinit var tvCodigo: TextView
    private lateinit var tvInfo: TextView
    private lateinit var btnRepartir: Button

    // El botón reiniciar ya no hace falta aquí porque se hace en la pantalla anterior
    // pero si quieres mantenerlo para "resetear partida" sin cambiar código, lo dejamos oculto o lo cambiamos.
    // Para simplificar, lo he quitado del diseño lógico.

    private var codigoClase: String = ""
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_astra_admin)

        // 1. RECUPERAR EL CÓDIGO DE LA SALA
        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        if (codigoClase.isEmpty()) {
            Toast.makeText(this, getString(R.string.errorea_gelaren_kodea_falta_da), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        configurarDiseno()

        tvCodigo = findViewById(R.id.tvCodigoGenerado)
        tvInfo = findViewById(R.id.tvInfoJugadores)
        btnRepartir = findViewById(R.id.btnRepartirRoles)

        // El botón reiniciar del XML lo ocultamos o usamos para otra cosa
        val btnReiniciar = findViewById<Button>(R.id.btnReiniciar)
        btnReiniciar.visibility = View.GONE

        // Mostramos el código
        tvCodigo.text = codigoClase

        // Empezamos a escuchar jugadores de ESA sala
        escucharJugadores()

        btnRepartir.setOnClickListener { repartirRoles() }
    }

    private fun configurarDiseno() {
        val headerView = findViewById<View>(R.id.include_header)
        val tvHeaderTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        tvHeaderTitle.text = "ASTRA ADMIN"

        val footerView = findViewById<View>(R.id.include_footer)
        val btnAtras = footerView.findViewById<ImageView>(R.id.buttonBack)
        val btnContinuar = footerView.findViewById<ImageView>(R.id.buttonContinue)

        btnAtras.visibility = View.VISIBLE
        btnContinuar.visibility = View.GONE

        btnAtras.setOnClickListener { finish() }
    }

    private fun escucharJugadores() {
        db.collection("partidas").document(codigoClase)
            .collection("jugadores")
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                val numero = snapshots?.size() ?: 0
                tvInfo.text = "Jokalariak konektatuta: $numero"
            }
    }

    private fun repartirRoles() {
        val jugadoresRef = db.collection("partidas").document(codigoClase).collection("jugadores")

        jugadoresRef.get().addOnSuccessListener { result ->
            val listaJugadores = result.documents
            val total = listaJugadores.size

            if (total < 3) {
                Toast.makeText(this,
                    getString(R.string.gutxienez_3_jokalari_behar_dira), Toast.LENGTH_SHORT).show()
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
                batch.update(doc.reference, "ultimoAtaque", 0L)
            }

            val partidaRef = db.collection("partidas").document(codigoClase)
            batch.update(partidaRef, "estado", "JUGANDO")
            batch.update(partidaRef, "asesino", nombresAsesinos.trim())

            batch.commit().addOnSuccessListener {
                Toast.makeText(this, getString(R.string.partida_hasita), Toast.LENGTH_SHORT).show()
            }
        }
    }
}