package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
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
    private lateinit var cardFinJuego: CardView

    // UI Espera y Selección de Avatar
    private lateinit var tvContadorJugadores: TextView
    private lateinit var ivSelectAvatar1: ImageView
    private lateinit var ivSelectAvatar2: ImageView
    private lateinit var ivSelectAvatar3: ImageView

    // UI Grids
    private lateinit var rvPueblo: RecyclerView
    private lateinit var rvPolicia: RecyclerView
    private lateinit var rvAsesino: RecyclerView

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

    // --- DATOS ---
    private var nombreUsuario: String = ""
    private var codigoClase: String = ""
    private var miRol: String = ""
    private var estoyVivo: Boolean = true
    private var estadoPartida: String = "ESPERANDO"

    private var listaVictimas: MutableList<String> = mutableListOf()
    private var listaSospechosos: MutableList<String> = mutableListOf()
    private var listaEstadoJugadores: MutableList<JugadorEstadoGrid> = mutableListOf()
    private var mapUltimoAtaquePrevio: MutableMap<String, Long> = mutableMapOf()
    private var isFirstLoad = true

    // --- FIREBASE ---
    private val db = FirebaseFirestore.getInstance()
    private var listenerPartida: ListenerRegistration? = null
    private var listenerJugador: ListenerRegistration? = null
    private var listenerOtrosJugadores: ListenerRegistration? = null
    private var listenerTodosJugadores: ListenerRegistration? = null
    private var listenerGrid: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_astra)

        nombreUsuario = intent.getStringExtra("USER_NAME") ?: ""
        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        configurarDiseno()
        inicializarVistas()

        if (nombreUsuario.isEmpty() || codigoClase.isEmpty()) {
            Toast.makeText(this, getString(R.string.errorea_datuekin), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        unirseAPartida()
    }

    private fun configurarDiseno() {
        val headerView = findViewById<View>(R.id.include_header)
        val tvHeaderTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        tvHeaderTitle.text = "ASTRA"

        val footerView = findViewById<View>(R.id.include_footer)
        val btnAtras = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnContinuar = footerView.findViewById<ImageButton>(R.id.buttonContinue)
        btnAtras.visibility = View.VISIBLE
        btnContinuar.visibility = View.GONE
        btnAtras.setOnClickListener { finish() }
    }

    private fun inicializarVistas() {
        layoutCarga = findViewById(R.id.layoutCarga)
        layoutEspera = findViewById(R.id.layoutEspera)
        layoutPueblo = findViewById(R.id.layoutPueblo)
        layoutPolicia = findViewById(R.id.layoutPolicia)
        layoutAsesino = findViewById(R.id.layoutAsesino)
        layoutMuerto = findViewById(R.id.layoutMuerto)
        layoutFinJuego = findViewById(R.id.layoutFinJuego)
        cardFinJuego = findViewById(R.id.cardFinJuego)

        // SELECCIÓN DE AVATAR
        tvContadorJugadores = findViewById(R.id.tvContadorJugadores)
        ivSelectAvatar1 = findViewById(R.id.ivSelectAvatar1)
        ivSelectAvatar2 = findViewById(R.id.ivSelectAvatar2)
        ivSelectAvatar3 = findViewById(R.id.ivSelectAvatar3)

        // Logic de clicks para elegir avatar
        ivSelectAvatar1.setOnClickListener { cambiarAvatar("1") }
        ivSelectAvatar2.setOnClickListener { cambiarAvatar("2") }
        ivSelectAvatar3.setOnClickListener { cambiarAvatar("3") }

        rvPueblo = findViewById(R.id.rvPueblo)
        rvPolicia = findViewById(R.id.rvPolicia)
        rvAsesino = findViewById(R.id.rvAsesino)

        adapterPueblo = JugadoresGridAdapter(listaEstadoJugadores)
        adapterPolicia = JugadoresGridAdapter(listaEstadoJugadores)
        adapterAsesino = JugadoresGridAdapter(listaEstadoJugadores)

        rvPueblo.layoutManager = GridLayoutManager(this, 3)
        rvPueblo.adapter = adapterPueblo
        rvPolicia.layoutManager = GridLayoutManager(this, 3)
        rvPolicia.adapter = adapterPolicia
        rvAsesino.layoutManager = GridLayoutManager(this, 3)
        rvAsesino.adapter = adapterAsesino

        spinnerVictimas = findViewById(R.id.spinnerVictimas)
        btnMatar = findViewById(R.id.btnMatar)
        tvCooldown = findViewById(R.id.tvCooldown)
        spinnerSospechosos = findViewById(R.id.spinnerSospechosos)
        btnAcusar = findViewById(R.id.btnAcusar)
        tvGanador = findViewById(R.id.tvGanador)

        btnAcusar.setOnClickListener { intentarAcusar() }
        btnMatar.setOnClickListener { matarJugadorSeleccionado() }
    }

    private fun cambiarAvatar(idAvatar: String) {
        // 1. Actualizar UI visualmente (Opacidad)
        ivSelectAvatar1.alpha = if (idAvatar == "1") 1.0f else 0.5f
        ivSelectAvatar2.alpha = if (idAvatar == "2") 1.0f else 0.5f
        ivSelectAvatar3.alpha = if (idAvatar == "3") 1.0f else 0.5f

        // 2. Guardar en Firebase
        db.collection("partidas").document(codigoClase)
            .collection("jugadores").document(nombreUsuario)
            .update("avatar", idAvatar)
    }

    private fun unirseAPartida() {
        val salaRef = db.collection("partidas").document(codigoClase)
        salaRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                gestionarJugador(salaRef)
            } else {
                Toast.makeText(this, getString(R.string.gela_ez_da_existitzen), Toast.LENGTH_SHORT).show()
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
                // Por defecto entramos con avatar "1" y 0 PUNTOS
                val datos = hashMapOf(
                    "rol" to "PUEBLO",
                    "vivo" to true,
                    "ultimoAtaque" to 0L,
                    "avatar" to "1",
                    "puntuacion" to 0  // <--- ¡NUEVO! Inicializamos a cero
                )
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
                        gestionarLogicaPorEstado()
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

                    // Recuperar mi avatar actual para marcarlo en la UI si vuelvo a entrar
                    val miAvatar = snapshot.getString("avatar") ?: "1"
                    if (estadoPartida == "ESPERANDO") {
                        ivSelectAvatar1.alpha = if (miAvatar == "1") 1.0f else 0.5f
                        ivSelectAvatar2.alpha = if (miAvatar == "2") 1.0f else 0.5f
                        ivSelectAvatar3.alpha = if (miAvatar == "3") 1.0f else 0.5f
                    }

                    if (!estadoPartida.startsWith("TERMINADO")) {
                        actualizarPantalla()
                    }
                    gestionarLogicaPorEstado()
                }
            }
    }

    private fun gestionarLogicaPorEstado() {
        escucharTodosLosJugadoresEnJuego()
        if (estadoPartida == "JUGANDO" && estoyVivo) {
            if (miRol == "ASESINO") escucharJugadoresParaAsesino()
            if (miRol == "POLICIA") escucharJugadoresParaPolicia()
        }
    }

    private fun escucharTodosLosJugadoresEnJuego() {
        if (listenerGrid != null) return
        listenerGrid = db.collection("partidas").document(codigoClase)
            .collection("jugadores")
            .addSnapshotListener { snapshots, _ ->
                if (snapshots == null) return@addSnapshotListener
                val nuevaListaEstados = mutableListOf<JugadorEstadoGrid>()
                val nuevosTiemposAtaque = mutableMapOf<String, Long>()

                snapshots.forEach { doc ->
                    val nombre = doc.id
                    val vivo = doc.getBoolean("vivo") ?: true
                    val ultimoAtaque = doc.getLong("ultimoAtaque") ?: 0L

                    // LEEMOS EL AVATAR DE CADA JUGADOR
                    val avatarId = doc.getString("avatar") ?: "1"

                    nuevosTiemposAtaque[nombre] = ultimoAtaque
                    var parpadea = false
                    if (!isFirstLoad) {
                        val timestampPrevio = mapUltimoAtaquePrevio[nombre] ?: 0L
                        if (ultimoAtaque > timestampPrevio) {
                            parpadea = true
                        }
                    }
                    // Pasamos el avatarId al adaptador
                    nuevaListaEstados.add(JugadorEstadoGrid(nombre, vivo, parpadea, avatarId))
                }
                mapUltimoAtaquePrevio = nuevosTiemposAtaque
                listaEstadoJugadores = nuevaListaEstados
                isFirstLoad = false
                tvContadorJugadores.text =
                    getString(R.string.konektatuta, listaEstadoJugadores.size)
                adapterPueblo.actualizarLista(listaEstadoJugadores)
                adapterPolicia.actualizarLista(listaEstadoJugadores)
                adapterAsesino.actualizarLista(listaEstadoJugadores)
            }
    }

    private fun actualizarPantalla() {
        if (estadoPartida.startsWith("TERMINADO")) return
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
        val batch = db.batch()
        val partidaRef = db.collection("partidas").document(codigoClase)
        val victimaRef = partidaRef.collection("jugadores").document(victima)
        batch.update(victimaRef, "vivo", false)
        val yoRef = partidaRef.collection("jugadores").document(nombreUsuario)
        batch.update(yoRef, "ultimoAtaque", System.currentTimeMillis())
        batch.commit().addOnSuccessListener {
            iniciarCooldown()
            verificarVictoriaAsesinos()
        }
    }

    private fun intentarAcusar() {
        val sospechoso = spinnerSospechosos.selectedItem?.toString() ?: return
        db.collection("partidas").document(codigoClase)
            .collection("jugadores").document(nombreUsuario)
            .update("ultimoAtaque", System.currentTimeMillis())
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
                Toast.makeText(this, getString(R.string.asmatu_duzu), Toast.LENGTH_SHORT).show()
                db.collection("partidas").document(codigoClase)
                    .collection("jugadores").document(nombreAsesino).update("vivo", false)
                    .addOnSuccessListener { verificarSiQuedanAsesinos() }
            } else {
                Toast.makeText(this,
                    getString(R.string.huts_egin_duzu_hil_egin_zara), Toast.LENGTH_SHORT).show()
                eliminarme { verificarVictoriaAsesinos() }
            }
        }
    }

    private fun iniciarCooldown() {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(m: Long) { tvCooldown.text = "${m / 1000}s" }
            override fun onFinish() {
                tvCooldown.visibility = View.INVISIBLE
                btnMatar.isEnabled = true
                btnMatar.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D32F2F"))
                btnMatar.text = getString(R.string.hil)
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
        // Ocultar paneles anteriores
        layoutCarga.visibility = View.GONE
        layoutEspera.visibility = View.GONE
        layoutPueblo.visibility = View.GONE
        layoutPolicia.visibility = View.GONE
        layoutAsesino.visibility = View.GONE
        layoutMuerto.visibility = View.GONE

        // Mostrar panel final
        layoutFinJuego.visibility = View.VISIBLE

        val ganadorTraducido = when (ganador) {
            "ASESINOS" -> "HILTZAILEAK"
            "PUEBLO y POLICIA" -> "HERRITARRAK eta POLIZIA"
            else -> ganador
        }

        val ganadorEsPueblo = ganador.contains("PUEBLO", ignoreCase = true) || ganador.contains(
            "POLICIA",
            ignoreCase = true
        )
        val ganadorEsAsesino = ganador.contains("ASESINO", ignoreCase = true)
        val soyPueblo = miRol == "PUEBLO" || miRol == "POLICIA"
        val soyAsesino = miRol == "ASESINO"

        var heGanado = false
        var puntosASumar = 0L

        if (ganadorEsPueblo && soyPueblo) {
            heGanado = true
            puntosASumar = 1
        } else if (ganadorEsAsesino && soyAsesino) {
            heGanado = true
            puntosASumar = 2
        }

        // --- GUARDADO ONLINE (Firebase) ---
        if (puntosASumar > 0) {
            db.collection("partidas").document(codigoClase)
                .collection("jugadores").document(nombreUsuario)
                .update("puntuacion", FieldValue.increment(puntosASumar))
                .addOnSuccessListener {
                    Toast.makeText(this, "+$puntosASumar puntu ranking-era!", Toast.LENGTH_SHORT)
                        .show()
                }
        }

        if (heGanado) {
            // ---------------------------------------------------------
            // NUEVO: DESBLOQUEAR PIEZAS DEL PUZLE (OFFLINE)
            // ---------------------------------------------------------
            val prefs = getSharedPreferences("GernikaProgress", Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putBoolean("astraCompletado", true) // Desbloquea la Fila 1
            editor.apply()

            Toast.makeText(this,
                getString(R.string.puzzlearen_pieza_berriak_lortu_dituzu), Toast.LENGTH_LONG).show()
            // ---------------------------------------------------------

            cardFinJuego.setCardBackgroundColor(Color.parseColor("#4CAF50"))
            tvGanador.text = "ZORIONAK!\n\nIRABAZLEAK:\n$ganadorTraducido\n(+$puntosASumar puntu)"
            tvGanador.setTextColor(Color.WHITE)
        } else {
            cardFinJuego.setCardBackgroundColor(Color.parseColor("#D32F2F"))
            tvGanador.text = "GALDU DUZU\n\nIRABAZLEAK:\n$ganadorTraducido"
            tvGanador.setTextColor(Color.WHITE)
        }
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