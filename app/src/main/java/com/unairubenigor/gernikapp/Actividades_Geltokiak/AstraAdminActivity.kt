package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.unairubenigor.gernikapp.R

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

        // 1. GENERAR CÓDIGO DE 6 DÍGITOS AL INICIAR
        generarNuevaSala()

        btnRepartir.setOnClickListener {
            repartirRoles()
        }

        btnReiniciar.setOnClickListener {
            // Genera uno nuevo si quieren empezar de cero total
            generarNuevaSala()
        }
    }

    private fun generarNuevaSala() {
        // Genera un número aleatorio entre 100000 y 999999
        val numeroRandom = (100000..999999).random()
        codigoClase = numeroRandom.toString()

        // Lo mostramos en pantalla grande
        tvCodigo.text = codigoClase

        // Creamos la sala en Firebase para que exista
        val datosSala = hashMapOf(
            "estado" to "ESPERANDO", // Estado inicial
            "asesino" to ""
        )

        db.collection("partidas").document(codigoClase)
            .set(datosSala)
            .addOnSuccessListener {
                Toast.makeText(this, "Sala $codigoClase abierta", Toast.LENGTH_SHORT).show()
                // Empezamos a escuchar quién entra
                escucharJugadores()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al crear sala: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun escucharJugadores() {
        // Escuchamos la colección 'jugadores' DENTRO del documento de la sala generada
        db.collection("partidas").document(codigoClase)
            .collection("jugadores")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    tvInfo.text = "Error de conexión"
                    return@addSnapshotListener
                }

                val numero = snapshots?.size() ?: 0
                tvInfo.text = "Jugadores conectados: $numero"
            }
    }

    private fun repartirRoles() {
        val jugadoresRef = db.collection("partidas").document(codigoClase).collection("jugadores")

        jugadoresRef.get().addOnSuccessListener { result ->
            val listaJugadores = result.documents
            val total = listaJugadores.size

            if (total < 4) {
                Toast.makeText(this, "Faltan jugadores (Mínimo 4). Hay $total", Toast.LENGTH_SHORT).show()
                // Descomenta la siguiente línea si quieres probar solo con 1 o 2 personas:
                // if (total < 1) return@addOnSuccessListener
                return@addOnSuccessListener
            }

            // 1. Barajamos
            val listaBarajada = listaJugadores.shuffled()
            val batch = db.batch()
            var nombresAsesinos = ""

            // 2. Asignamos roles (2 Polis, 2 Asesinos, resto Pueblo)
            listaBarajada.forEachIndexed { index, doc ->
                var rol = "PUEBLO"

                if (index == 0 || index == 1) {
                    rol = "POLICIA"
                } else if (index == 2 || index == 3) {
                    rol = "ASESINO"
                    // Guardamos el nombre para que el sistema sepa quién es el malo
                    val nombre = doc.id
                    nombresAsesinos += "$nombre "
                }

                batch.update(doc.reference, "rol", rol)
                batch.update(doc.reference, "vivo", true)
            }

            // 3. Actualizamos la sala
            val partidaRef = db.collection("partidas").document(codigoClase)
            batch.update(partidaRef, "estado", "JUGANDO")
            batch.update(partidaRef, "asesino", nombresAsesinos.trim()) // Guardamos quién es el asesino real

            batch.commit().addOnSuccessListener {
                Toast.makeText(this, "¡ROLES ASIGNADOS!", Toast.LENGTH_LONG).show()
            }
        }
    }
}