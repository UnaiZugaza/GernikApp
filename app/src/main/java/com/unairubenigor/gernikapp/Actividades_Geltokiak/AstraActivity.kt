package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.os.Bundle
import android.util.Log
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

    // UI
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
    private var listenerPartida: ListenerRegistration? = null
    private var listenerJugador: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_astra)

        nombreUsuario = intent.getStringExtra("USER_NAME") ?: ""
        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        inicializarVistas()

        if (nombreUsuario.isEmpty() || codigoClase.isEmpty()) {
            mensajeError("Error: Faltan datos de usuario o clase.")
            return
        }

        // PASO 1: Intentar unirse a la partida (crear el usuario si no existe)
        unirseAPartida()
    }

    private fun unirseAPartida() {
        val jugadorRef = db.collection("partidas").document(codigoClase)
            .collection("jugadores").document(nombreUsuario)

        // Comprobamos si ya existo antes de escribir nada
        jugadorRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                mensajeEstado("Reconectando a la partida...")
                escucharJuego() // Ya existo, solo escucho
            } else {
                // NO EXISTO -> ME CREO AUTOMÁTICAMENTE
                val nuevosDatos = hashMapOf(
                    "rol" to "PUEBLO", // Por defecto todos entran como Pueblo
                    "vivo" to true
                )

                jugadorRef.set(nuevosDatos)
                    .addOnSuccessListener {
                        mensajeEstado("¡Te has unido a la partida con éxito!")
                        escucharJuego() // Ahora que existo, empiezo a escuchar
                    }
                    .addOnFailureListener { e ->
                        mensajeError("No se pudo entrar a la partida: ${e.message}")
                        // Si falla aquí, suele ser por las REGLAS de seguridad de Firebase
                    }
            }
        }.addOnFailureListener { e ->
            mensajeError("Error al comprobar usuario: ${e.message}")
        }
    }

    private fun inicializarVistas() {
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
            if (sospechoso.isNotEmpty()) intentarAcusar(sospechoso)
        }
        btnSalir.setOnClickListener { finish() }
    }

    private fun escucharJuego() {
        // 1. Escuchar estado global de la partida
        val partidaRef = db.collection("partidas").document(codigoClase)
        listenerPartida = partidaRef.addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener

            if (snapshot != null && snapshot.exists()) {
                val estado = snapshot.getString("estado") ?: "JUGANDO"
                if (estado.startsWith("TERMINADO")) {
                    mostrarPantallaFin(estado.substringAfter("_"))
                }
            }
        }

        // 2. Escuchar mi propio estado (Rol y Vida)
        val jugadorRef = partidaRef.collection("jugadores").document(nombreUsuario)
        listenerJugador = jugadorRef.addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener

            if (snapshot != null && snapshot.exists()) {
                miRol = snapshot.getString("rol") ?: "PUEBLO"
                estoyVivo = snapshot.getBoolean("vivo") ?: true
                actualizarPantalla()
            }
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

        if (!estoyVivo) {
            layoutMuerto.visibility = View.VISIBLE
            return
        }

        // Mostramos según el rol que venga de Firebase
        when (miRol) {
            "POLICIA" -> layoutPolicia.visibility = View.VISIBLE
            "ASESINO" -> layoutAsesino.visibility = View.VISIBLE
            else -> layoutPueblo.visibility = View.VISIBLE
        }
    }

    private fun intentarAcusar(sospechoso: String) {
        // 1. Obtenemos la lista de asesinos de la partida
        db.collection("partidas").document(codigoClase).get()
            .addOnSuccessListener { document ->
                val textoAsesinos = document.getString("asesino") ?: ""
                val listaAsesinos = textoAsesinos.split(" ") // ["Juan", "Carlos"]

                var esAsesino = false
                var nombreRealAsesino = ""

                // Buscamos si el sospechoso coincide con alguno de la lista
                for (nombre in listaAsesinos) {
                    if (nombre.equals(sospechoso, ignoreCase = true)) {
                        esAsesino = true
                        nombreRealAsesino = nombre // Guardamos el nombre exacto (por mayúsculas/minúsculas)
                        break
                    }
                }

                if (esAsesino) {
                    // ¡ACIERTO!
                    mensajeEstado("¡Has atrapado a $nombreRealAsesino! Eliminándolo...")

                    // PASO A: Matamos al asesino en la base de datos
                    eliminarJugador(nombreRealAsesino) {
                        // PASO B: Una vez muerto, comprobamos si quedan más
                        verificarSiQuedanAsesinos()
                    }

                } else {
                    // ¡FALLO! - El policía muere
                    eliminarme()
                    mensajeError("¡Fallaste! $sospechoso es inocente. Has sido eliminado.")
                }
            }
    }

    // Función auxiliar para matar a OTRO jugador (al asesino)
    private fun eliminarJugador(nombreJugador: String, alTerminar: () -> Unit) {
        db.collection("partidas").document(codigoClase)
            .collection("jugadores").document(nombreJugador)
            .update("vivo", false)
            .addOnSuccessListener {
                alTerminar() // Avisamos de que ya está "muerto"
            }
    }

    // Función crucial: Cuenta cuántos asesinos siguen vivos
    private fun verificarSiQuedanAsesinos() {
        db.collection("partidas").document(codigoClase)
            .collection("jugadores")
            .whereEqualTo("rol", "ASESINO") // Filtramos solo asesinos...
            .whereEqualTo("vivo", true)     // ...que sigan vivos
            .get()
            .addOnSuccessListener { documentos ->
                val asesinosVivos = documentos.size()

                if (asesinosVivos == 0) {
                    // ¡YA NO QUEDAN! Ahora sí ganamos
                    terminarPartida("PUEBLO y POLICIA")
                } else {
                    // Aún quedan
                    mensajeError("¡Bien hecho! Pero aún queda $asesinosVivos asesino(s) suelto(s).")
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

    private fun mostrarPantallaFin(ganador: String) {
        layoutCarga.visibility = View.GONE
        layoutPueblo.visibility = View.GONE
        layoutPolicia.visibility = View.GONE
        layoutAsesino.visibility = View.GONE
        layoutMuerto.visibility = View.GONE
        layoutFinJuego.visibility = View.VISIBLE
        tvGanador.text = "GANADOR: $ganador"
    }

    private fun mensajeError(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        Log.e("ASTRA_DEBUG", msg)
    }

    private fun mensajeEstado(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerPartida?.remove()
        listenerJugador?.remove()
    }
}