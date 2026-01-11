package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.unairubenigor.gernikapp.R

class AstraActivity : AppCompatActivity() {

    // Referencias UI
    private lateinit var layoutCarga: LinearLayout
    private lateinit var layoutPueblo: LinearLayout
    private lateinit var layoutPolicia: LinearLayout
    private lateinit var layoutAsesino: LinearLayout
    private lateinit var layoutMuerto: LinearLayout
    private lateinit var layoutFinJuego: LinearLayout

    private lateinit var etBlocNotas: EditText
    private lateinit var etSospechoso: EditText
    private lateinit var btnAcusar: Button
    private lateinit var tvGanador: TextView
    private lateinit var btnSalir: Button

    // Datos
    private var nombreUsuario: String = ""
    private var codigoClase: String = ""
    private var miRol: String = ""
    private var estoyVivo: Boolean = true

    // Firestore
    private val db = FirebaseFirestore.getInstance()
    // Guardamos los listeners para poder detenerlos si salimos de la pantalla
    private var listenerPartida: ListenerRegistration? = null
    private var listenerJugador: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_astra)

        // Recogemos datos del Intent
        nombreUsuario = intent.getStringExtra("USER_NAME") ?: ""
        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        if (nombreUsuario.isEmpty() || codigoClase.isEmpty()) {
            Toast.makeText(this, "Error: Datos de partida incompletos", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        inicializarVistas()
        escucharFirestore()
    }

    private fun inicializarVistas() {
        // Vinculamos con el XML (activity_astra.xml que te pasé antes)
        layoutCarga = findViewById(R.id.layoutCarga)
        layoutPueblo = findViewById(R.id.layoutPueblo)
        layoutPolicia = findViewById(R.id.layoutPolicia)
        layoutAsesino = findViewById(R.id.layoutAsesino)
        layoutMuerto = findViewById(R.id.layoutMuerto)
        layoutFinJuego = findViewById(R.id.layoutFinJuego)

        etBlocNotas = findViewById(R.id.etBlocNotas)
        etSospechoso = findViewById(R.id.etSospechoso)
        btnAcusar = findViewById(R.id.btnAcusar)
        tvGanador = findViewById(R.id.tvGanador)
        btnSalir = findViewById(R.id.btnSalir)

        btnAcusar.setOnClickListener {
            val sospechoso = etSospechoso.text.toString().trim()
            if (sospechoso.isNotEmpty()) {
                intentarAcusar(sospechoso)
            } else {
                Toast.makeText(this, "Escribe un nombre", Toast.LENGTH_SHORT).show()
            }
        }

        btnSalir.setOnClickListener { finish() }
    }

    private fun escucharFirestore() {
        // 1. Escuchar el Estado General de la Partida (si alguien ganó)
        // Estructura: Colección "partidas" -> Documento "1234"
        val partidaDoc = db.collection("partidas").document(codigoClase)

        listenerPartida = partidaDoc.addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null || !snapshot.exists()) {
                // Error o partida no existe
                return@addSnapshotListener
            }

            val estado = snapshot.getString("estado") ?: "JUGANDO"
            if (estado.startsWith("TERMINADO")) {
                val ganador = estado.substringAfter("_")
                mostrarPantallaFin(ganador)
            }
        }

        // 2. Escuchar MI ESTADO (Rol y Vida)
        // Estructura: partidas -> 1234 -> jugadores (Subcolección) -> Ruben (Documento)
        val miJugadorDoc = partidaDoc.collection("jugadores").document(nombreUsuario)

        listenerJugador = miJugadorDoc.addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null || !snapshot.exists()) {
                // Aún no existo en la base de datos (o error)
                layoutCarga.visibility = View.VISIBLE
                return@addSnapshotListener
            }

            // Leemos los campos
            miRol = snapshot.getString("rol") ?: "PUEBLO"
            estoyVivo = snapshot.getBoolean("vivo") ?: true

            actualizarPantalla()
        }
    }

    private fun actualizarPantalla() {
        // Ocultamos todo
        layoutCarga.visibility = View.GONE
        layoutPueblo.visibility = View.GONE
        layoutPolicia.visibility = View.GONE
        layoutAsesino.visibility = View.GONE
        layoutMuerto.visibility = View.GONE
        layoutFinJuego.visibility = View.GONE

        // Lógica de pantallas
        if (!estoyVivo) {
            layoutMuerto.visibility = View.VISIBLE
            return
        }

        when (miRol) {
            "POLICIA" -> layoutPolicia.visibility = View.VISIBLE
            "ASESINO" -> layoutAsesino.visibility = View.VISIBLE
            else -> layoutPueblo.visibility = View.VISIBLE
        }
    }

    private fun intentarAcusar(sospechoso: String) {
        // Leer el nombre del asesino del documento de la partida
        db.collection("partidas").document(codigoClase).get()
            .addOnSuccessListener { document ->
                val asesinoReal = document.getString("asesino") ?: ""

                if (asesinoReal.equals(sospechoso, ignoreCase = true)) {
                    // ACIERTO -> Terminar juego
                    terminarPartida("PUEBLO y POLICIA")
                } else {
                    // FALLO -> Matar al policía
                    eliminarme()
                    Toast.makeText(this, "¡Fallaste! Has sido eliminado.", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun eliminarme() {
        db.collection("partidas").document(codigoClase)
            .collection("jugadores").document(nombreUsuario)
            .update("vivo", false)
    }

    private fun terminarPartida(ganador: String) {
        db.collection("partidas").document(codigoClase)
            .update("estado", "TERMINADO_$ganador")
    }

    override fun onDestroy() {
        super.onDestroy()
        // Importante: Dejar de escuchar cuando cerramos la app para ahorrar datos
        listenerPartida?.remove()
        listenerJugador?.remove()
    }
    private fun mostrarPantallaFin(ganador: String) {
        // Ocultamos todas las pantallas de juego
        layoutCarga.visibility = View.GONE
        layoutPueblo.visibility = View.GONE
        layoutPolicia.visibility = View.GONE
        layoutAsesino.visibility = View.GONE
        layoutMuerto.visibility = View.GONE

        // Mostramos la pantalla de fin
        layoutFinJuego.visibility = View.VISIBLE
        tvGanador.text = "GANADOR: $ganador"

        // Cambiamos el color del texto según quién gane
        if (ganador == "ASESINO") {
            tvGanador.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
        } else {
            tvGanador.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
        }
    }
}