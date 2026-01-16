package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    // UI Espera
    private lateinit var tvContadorJugadores: TextView

    // UI Grids (Uno por cada pantalla de juego)
    private lateinit var rvPueblo: RecyclerView
    private lateinit var rvPolicia: RecyclerView
    private lateinit var rvAsesino: RecyclerView

    // Adaptadores para los grids
    private lateinit var adapterPueblo: JugadoresGridAdapter
    private lateinit var adapterPolicia: JugadoresGridAdapter
    private lateinit var adapterAsesino: JugadoresGridAdapter

    // UI Roles
    private lateinit var spinnerVictimas: Spinner
    private lateinit var btnMatar: Button
    private lateinit var tvCooldown: TextView
    private lateinit var spinnerSospechosos: Spinner
    private lateinit var btnAcusar: Button
    private lateinit var tvGanador: TextView
    private lateinit var btnSalir: Button

    // --- DATOS ---
    private var nombreUsuario: String = ""
    private var codigoClase: String = ""
    private var miRol: String = ""
    private var estoyVivo: Boolean = true
    private var estadoPartida: String = "ESPERANDO"

    // Listas
    private var listaVictimas: MutableList<String> = mutableListOf()
    private var listaSospechosos: MutableList<String> = mutableListOf()
    private var listaTodosJugadores: MutableList<String> = mutableListOf() // Para el grid

    // --- FIREBASE ---
    private val db = FirebaseFirestore.getInstance()
    private var listenerPartida: ListenerRegistration? = null
    private var listenerJugador: ListenerRegistration? = null
    private var listenerOtrosJugadores: ListenerRegistration? = null
    private var listenerTodosJugadores: ListenerRegistration? = null
    private var listenerGrid: ListenerRegistration? = null // Listener para actualizar la matriz

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

        tvContadorJugadores = findViewById(R.id.tvContadorJugadores)

        // --- CONFIGURACIÓN DE LOS GRIDS (MATRICES) EN CADA PANTALLA ---
        rvPueblo = findViewById(R.id.rvPueblo)
        rvPolicia = findViewById(R.id.rvPolicia)
        rvAsesino = findViewById(R.id.rvAsesino)

        // Creamos 3 adaptadores independientes pero que mostrarán la misma lista
        adapterPueblo = JugadoresGridAdapter(listaTodosJugadores)
        adapterPolicia = JugadoresGridAdapter(listaTodosJugadores)
        adapterAsesino = JugadoresGridAdapter(listaTodosJugadores)

        // Asignamos LayoutManager (3 columnas) y Adapter a cada uno
        rvPueblo.layoutManager = GridLayoutManager(this, 3)
        rvPueblo.adapter = adapterPueblo

        rvPolicia.layoutManager = GridLayoutManager(this, 3)
        rvPolicia.adapter = adapterPolicia

        rvAsesino.layoutManager = GridLayoutManager(this, 3)
        rvAsesino.adapter = adapterAsesino

        // UI Controles
        spinnerVictimas = findViewById(R.id.spinnerVictimas)
        btnMatar = findViewById(R.id.btnMatar)
        tvCooldown = findViewById(R.id.tvCooldown)
        spinnerSospechosos = findViewById(R.id.spinnerSospechosos)
        btnAcusar = findViewById(R.id.btnAcusar)
        tvGanador = findViewById(R.id.tvGanador)
        btnSalir = findViewById(R.id.btnSalir)

        btnAcusar.setOnClickListener { intentarAcusar() }
        btnMatar.setOnClickListener { matarJugadorSeleccionado() }
        btnSalir.setOnClickListener { finish() }
    }

    private fun unirseAPartida() {
        val salaRef = db.collection("partidas").document(codigoClase)
        salaRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                gestionarJugador(salaRef)
            } else {
                Toast.makeText(this, "Sala no existe", Toast.LENGTH_SHORT).show()
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
        // Escuchar cambios de estado (Esperando -> Jugando)
        listenerPartida = db.collection("partidas").document(codigoClase)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null && snapshot.exists()) {
                    estadoPartida = snapshot.getString("estado") ?: "ESPERANDO"

                    if (estadoPartida.startsWith("TERMINADO")) {
                        mostrarPantallaFin(estadoPartida.substringAfter("_"))
                    } else {
                        actualizarPantalla()
                        gestionarLogicaPorEstado()
                    }
                }
            }

        // Escuchar mi rol
        listenerJugador = db.collection("partidas").document(codigoClase)
            .collection("jugadores").document(nombreUsuario)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null && snapshot.exists()) {
                    miRol = snapshot.getString("rol") ?: "PUEBLO"
                    estoyVivo = snapshot.getBoolean("vivo") ?: true

                    actualizarPantalla()
                    gestionarLogicaPorEstado()
                }
            }
    }

    private fun gestionarLogicaPorEstado() {
        // LÓGICA DE LA MATRIZ DE JUGADORES
        // Queremos ver la matriz tanto en ESPERA como en JUGANDO
        escucharTodosLosJugadoresEnJuego()

        // LÓGICA DE ROLES (SOLO SI JUGAMOS Y ESTOY VIVO)
        if (estadoPartida == "JUGANDO" && estoyVivo) {
            if (miRol == "ASESINO") escucharJugadoresParaAsesino()
            if (miRol == "POLICIA") escucharJugadoresParaPolicia()
        }
    }

    // --- NUEVO: ESCUCHAR TODOS PARA LA MATRIZ (GRID) ---
    private fun escucharTodosLosJugadoresEnJuego() {
        if (listenerGrid != null) return // Ya estamos escuchando

        listenerGrid = db.collection("partidas").document(codigoClase)
            .collection("jugadores")
            .addSnapshotListener { snapshots, _ ->
                listaTodosJugadores.clear()
                snapshots?.forEach { doc ->
                    listaTodosJugadores.add(doc.id)
                }

                // Actualizamos el contador de la sala de espera
                tvContadorJugadores.text = "Conectados: ${listaTodosJugadores.size}"

                // Actualizamos LOS 3 ADAPTADORES a la vez
                adapterPueblo.actualizarLista(listaTodosJugadores)
                adapterPolicia.actualizarLista(listaTodosJugadores)
                adapterAsesino.actualizarLista(listaTodosJugadores)
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

    // --- LÓGICA ESPECÍFICA DE ROLES (SPINNERS) ---
    private fun escucharJugadoresParaAsesino() {
        if (listenerOtrosJugadores != null) return
        listenerOtrosJugadores = db.collection("partidas").document(codigoClase)
            .collection("jugadores").whereEqualTo("vivo", true)
            .addSnapshotListener { snapshots, _ ->
                listaVictimas.clear()
                snapshots?.forEach { doc ->
                    if (doc.id != nombreUsuario && doc.getString("rol") != "ASESINO") {
                        listaVictimas.add(doc.id)
                    }
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaVictimas)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerVictimas.adapter = adapter
            }
    }

    private fun escucharJugadoresParaPolicia() {
        if (listenerTodosJugadores != null) return
        listenerTodosJugadores = db.collection("partidas").document(codigoClase)
            .collection("jugadores").whereEqualTo("vivo", true)
            .addSnapshotListener { snapshots, _ ->
                listaSospechosos.clear()
                snapshots?.forEach { doc ->
                    if (doc.id != nombreUsuario) listaSospechosos.add(doc.id)
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaSospechosos)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerSospechosos.adapter = adapter
            }
    }

    private fun matarJugadorSeleccionado() {
        val victima = spinnerVictimas.selectedItem?.toString() ?: return
        btnMatar.isEnabled = false
        btnMatar.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
        tvCooldown.visibility = View.VISIBLE

        db.collection("partidas").document(codigoClase)
            .collection("jugadores").document(victima).update("vivo", false)
            .addOnSuccessListener {
                iniciarCooldown()
                verificarVictoriaAsesinos()
            }
    }

    private fun intentarAcusar() {
        val sospechoso = spinnerSospechosos.selectedItem?.toString() ?: return
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
                Toast.makeText(this, "¡Correcto!", Toast.LENGTH_SHORT).show()
                db.collection("partidas").document(codigoClase)
                    .collection("jugadores").document(nombreAsesino).update("vivo", false)
                    .addOnSuccessListener { verificarSiQuedanAsesinos() }
            } else {
                Toast.makeText(this, "Fallaste. Has muerto.", Toast.LENGTH_SHORT).show()
                eliminarme { verificarVictoriaAsesinos() }
            }
        }
    }

    private fun iniciarCooldown() {
        object : CountDownTimer(15000, 1000) {
            override fun onTick(m: Long) { tvCooldown.text = "${m/1000}s" }
            override fun onFinish() {
                tvCooldown.visibility = View.INVISIBLE
                btnMatar.isEnabled = true
                btnMatar.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#212121"))
            }
        }.start()
    }

    private fun eliminarme(onComplete: () -> Unit) {
        db.collection("partidas").document(codigoClase)
            .collection("jugadores").document(nombreUsuario).update("vivo", false)
            .addOnSuccessListener { onComplete() }
    }

    private fun verificarSiQuedanAsesinos() {
        db.collection("partidas").document(codigoClase).collection("jugadores")
            .whereEqualTo("rol", "ASESINO").whereEqualTo("vivo", true).get()
            .addOnSuccessListener { if (it.isEmpty) terminarPartida("PUEBLO y POLICIA") }
    }

    private fun verificarVictoriaAsesinos() {
        db.collection("partidas").document(codigoClase).collection("jugadores")
            .whereEqualTo("rol", "POLICIA").whereEqualTo("vivo", true).get()
            .addOnSuccessListener { if (it.isEmpty) terminarPartida("ASESINOS") }
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
        listenerGrid?.remove()
    }
}