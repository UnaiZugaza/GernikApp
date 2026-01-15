package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.unairubenigor.gernikapp.R

class AstraActivity : AppCompatActivity() {

    // --- UI ---
    private lateinit var layoutCarga: LinearLayout
    private lateinit var layoutEspera: LinearLayout
    private lateinit var layoutPueblo: LinearLayout
    private lateinit var layoutPolicia: LinearLayout
    private lateinit var layoutAsesino: LinearLayout
    private lateinit var layoutMuerto: LinearLayout
    private lateinit var layoutFinJuego: LinearLayout

    // UI Asesino
    private lateinit var spinnerVictimas: Spinner
    private lateinit var btnMatar: Button
    private lateinit var tvCooldown: TextView

    // UI Policía (CAMBIADO: Ahora usa Spinner)
    private lateinit var spinnerSospechosos: Spinner
    private lateinit var btnAcusar: Button

    // UI General
    private lateinit var tvGanador: TextView
    private lateinit var btnSalir: Button

    // --- DATOS ---
    private var nombreUsuario: String = ""
    private var codigoClase: String = ""
    private var miRol: String = ""
    private var estoyVivo: Boolean = true
    private var estadoPartida: String = "ESPERANDO"

    // Listas para los desplegables
    private var listaVictimas: MutableList<String> = mutableListOf()
    private var listaSospechosos: MutableList<String> = mutableListOf()

    // --- FIREBASE ---
    private val db = FirebaseFirestore.getInstance()
    private var listenerPartida: ListenerRegistration? = null
    private var listenerJugador: ListenerRegistration? = null
    private var listenerOtrosJugadores: ListenerRegistration? = null // Para Asesino
    private var listenerTodosJugadores: ListenerRegistration? = null // Para Policía

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_astra)

        nombreUsuario = intent.getStringExtra("USER_NAME") ?: ""
        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        inicializarVistas()

        if (nombreUsuario.isEmpty() || codigoClase.isEmpty()) {
            Toast.makeText(this, "Error de datos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        unirseAPartida()
    }

    private fun inicializarVistas() {
        layoutCarga = findViewById(R.id.layoutCarga)
        layoutEspera = findViewById(R.id.layoutEspera)
        layoutPueblo = findViewById(R.id.layoutPueblo)
        layoutPolicia = findViewById(R.id.layoutPolicia)
        layoutAsesino = findViewById(R.id.layoutAsesino)
        layoutMuerto = findViewById(R.id.layoutMuerto)
        layoutFinJuego = findViewById(R.id.layoutFinJuego)

        // Asesino
        spinnerVictimas = findViewById(R.id.spinnerVictimas)
        btnMatar = findViewById(R.id.btnMatar)
        tvCooldown = findViewById(R.id.tvCooldown)

        // Policía
        spinnerSospechosos = findViewById(R.id.spinnerSospechosos) // Nuevo Spinner
        btnAcusar = findViewById(R.id.btnAcusar)

        // General
        tvGanador = findViewById(R.id.tvGanador)
        btnSalir = findViewById(R.id.btnSalir)

        btnAcusar.setOnClickListener { intentarAcusar() }
        btnMatar.setOnClickListener { matarJugadorSeleccionado() }
        btnSalir.setOnClickListener { finish() }
    }

    // --- CONEXIÓN ---
    private fun unirseAPartida() {
        val salaRef = db.collection("partidas").document(codigoClase)
        salaRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                gestionarJugador(salaRef)
            } else {
                Toast.makeText(this, "La sala no existe", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun gestionarJugador(salaRef: DocumentReference) {
        val jugadorRef = salaRef.collection("jugadores").document(nombreUsuario)
        jugadorRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                escucharJuego()
            } else {
                val datos = hashMapOf("rol" to "PUEBLO", "vivo" to true)
                jugadorRef.set(datos).addOnSuccessListener { escucharJuego() }
            }
        }
    }

    private fun escucharJuego() {
        listenerPartida = db.collection("partidas").document(codigoClase)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null && snapshot.exists()) {
                    estadoPartida = snapshot.getString("estado") ?: "ESPERANDO"

                    if (estadoPartida.startsWith("TERMINADO")) {
                        mostrarPantallaFin(estadoPartida.substringAfter("_"))
                    } else {
                        actualizarPantalla()
                        gestionarListasPorRol()
                    }
                }
            }

        listenerJugador = db.collection("partidas").document(codigoClase)
            .collection("jugadores").document(nombreUsuario)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null && snapshot.exists()) {
                    miRol = snapshot.getString("rol") ?: "PUEBLO"
                    estoyVivo = snapshot.getBoolean("vivo") ?: true

                    actualizarPantalla()
                    gestionarListasPorRol()
                }
            }
    }

    // Función auxiliar para cargar los Spinners según quién seas
    private fun gestionarListasPorRol() {
        if (estadoPartida == "JUGANDO" && estoyVivo) {
            if (miRol == "ASESINO") escucharJugadoresParaAsesino()
            if (miRol == "POLICIA") escucharJugadoresParaPolicia()
        }
    }

    private fun actualizarPantalla() {
        layoutCarga.visibility = View.GONE
        layoutEspera.visibility = View.GONE
        layoutPueblo.visibility = View.GONE
        layoutPolicia.visibility = View.GONE
        layoutAsesino.visibility = View.GONE
        layoutMuerto.visibility = View.GONE
        layoutFinJuego.visibility = View.GONE

        if (estadoPartida == "ESPERANDO") {
            layoutEspera.visibility = View.VISIBLE
            return
        }

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

    // --- LÓGICA ASESINO ---
    private fun escucharJugadoresParaAsesino() {
        if (listenerOtrosJugadores != null) return
        listenerOtrosJugadores = db.collection("partidas").document(codigoClase)
            .collection("jugadores").whereEqualTo("vivo", true)
            .addSnapshotListener { snapshots, _ ->
                listaVictimas.clear()
                snapshots?.forEach { doc ->
                    val nombre = doc.id
                    val rol = doc.getString("rol")
                    // Asesino ve a todos MENOS a sí mismo y a otros asesinos
                    if (nombre != nombreUsuario && rol != "ASESINO") {
                        listaVictimas.add(nombre)
                    }
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaVictimas)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerVictimas.adapter = adapter
            }
    }

    private fun matarJugadorSeleccionado() {
        val victima = spinnerVictimas.selectedItem?.toString() ?: return
        btnMatar.isEnabled = false
        btnMatar.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
        tvCooldown.visibility = View.VISIBLE

        db.collection("partidas").document(codigoClase)
            .collection("jugadores").document(victima)
            .update("vivo", false)
            .addOnSuccessListener {
                Toast.makeText(this, "💀 Has eliminado a $victima", Toast.LENGTH_SHORT).show()
                iniciarCooldown()
                verificarVictoriaAsesinos()
            }
    }

    private fun iniciarCooldown() {
        object : CountDownTimer(15000, 1000) {
            override fun onTick(m: Long) { tvCooldown.text = "Recargando: ${m/1000}s" }
            override fun onFinish() {
                tvCooldown.visibility = View.INVISIBLE
                btnMatar.isEnabled = true
                btnMatar.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D32F2F"))
                btnMatar.text = "☠️ MATAR ☠️"
            }
        }.start()
    }

    // --- LÓGICA POLICÍA (NUEVA) ---
    private fun escucharJugadoresParaPolicia() {
        if (listenerTodosJugadores != null) return // Ya estamos escuchando

        // El policía necesita ver a TODOS los vivos para sospechar
        listenerTodosJugadores = db.collection("partidas").document(codigoClase)
            .collection("jugadores").whereEqualTo("vivo", true)
            .addSnapshotListener { snapshots, _ ->
                listaSospechosos.clear()
                snapshots?.forEach { doc ->
                    val nombre = doc.id
                    // El policía ve a todos MENOS a sí mismo
                    if (nombre != nombreUsuario) {
                        listaSospechosos.add(nombre)
                    }
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaSospechosos)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerSospechosos.adapter = adapter
            }
    }

    private fun intentarAcusar() {
        // Ahora cogemos el nombre del Spinner, no del EditText
        val sospechoso = spinnerSospechosos.selectedItem?.toString()

        if (sospechoso == null) {
            Toast.makeText(this, "Elige un sospechoso primero", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("partidas").document(codigoClase).get().addOnSuccessListener { doc ->
            val asesinos = (doc.getString("asesino") ?: "").split(" ")
            var acierto = false
            var nombreAsesino = ""

            for (a in asesinos) {
                if (a.equals(sospechoso, true)) {
                    acierto = true
                    nombreAsesino = a
                    break
                }
            }

            if (acierto) {
                Toast.makeText(this, "¡Correcto! $nombreAsesino era un asesino.", Toast.LENGTH_LONG).show()
                db.collection("partidas").document(codigoClase)
                    .collection("jugadores").document(nombreAsesino)
                    .update("vivo", false)
                    .addOnSuccessListener { verificarSiQuedanAsesinos() }
            } else {
                Toast.makeText(this, "Fallaste. $sospechoso es inocente. Has muerto.", Toast.LENGTH_LONG).show()
                eliminarme { verificarVictoriaAsesinos() }
            }
        }
    }

    // --- FUNCIONES COMUNES ---
    private fun eliminarme(onComplete: () -> Unit = {}) {
        db.collection("partidas").document(codigoClase)
            .collection("jugadores").document(nombreUsuario)
            .update("vivo", false)
            .addOnSuccessListener { onComplete() }
    }

    private fun verificarSiQuedanAsesinos() {
        db.collection("partidas").document(codigoClase).collection("jugadores")
            .whereEqualTo("rol", "ASESINO").whereEqualTo("vivo", true).get()
            .addOnSuccessListener {
                if (it.isEmpty) terminarPartida("PUEBLO y POLICIA")
            }
    }

    private fun verificarVictoriaAsesinos() {
        db.collection("partidas").document(codigoClase).collection("jugadores")
            .whereEqualTo("rol", "POLICIA").whereEqualTo("vivo", true).get()
            .addOnSuccessListener {
                if (it.isEmpty) terminarPartida("ASESINOS")
            }
    }

    private fun terminarPartida(ganador: String) {
        db.collection("partidas").document(codigoClase).update("estado", "TERMINADO_$ganador")
    }

    private fun mostrarPantallaFin(ganador: String) {
        layoutCarga.visibility = View.GONE
        layoutEspera.visibility = View.GONE
        layoutPueblo.visibility = View.GONE
        layoutPolicia.visibility = View.GONE
        layoutAsesino.visibility = View.GONE
        layoutMuerto.visibility = View.GONE
        layoutFinJuego.visibility = View.VISIBLE
        tvGanador.text = "GANADOR:\n$ganador"
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerPartida?.remove()
        listenerJugador?.remove()
        listenerOtrosJugadores?.remove()
        listenerTodosJugadores?.remove()
    }
}