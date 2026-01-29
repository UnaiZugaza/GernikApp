package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.unairubenigor.gernikapp.R

class AndraMari7Activity : AppCompatActivity() {

    private lateinit var opinionEditText: EditText
    private lateinit var sendButton: Button

    private var nombreUsuario: String = "Ezezaguna"
    private var codigoClase: String = ""
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari7)

        // Recuperar datos con seguridad
        nombreUsuario = intent.getStringExtra("USER_NAME") ?: "Ezezaguna"
        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI - 4. GELTOKIA"

        opinionEditText = findViewById(R.id.et_opinion)
        sendButton = findViewById(R.id.button_send_opinion)

        sendButton.setOnClickListener {
            val textoOpinion = opinionEditText.text.toString().trim()

            if (textoOpinion.isNotEmpty()) {
                if (codigoClase.isEmpty()) {
                    Toast.makeText(this, "Errorea: Gela kodea falta da. Sartu berriro gelan.", Toast.LENGTH_SHORT).show()
                } else {
                    enviarOpinionAFirestore(textoOpinion)
                }
            } else {
                Toast.makeText(this, "Mesedez, idatzi zure iritzia bidali aurretik", Toast.LENGTH_SHORT).show()
            }
        }

        setupFooter()
    }

    private fun setupFooter() {
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE

        // Botón continuar (solo si ya han enviado o quieren saltar)
        btnNext.setOnClickListener {
            irASiguiente()
        }

        btnBack.setOnClickListener { finish() }
    }

    private fun enviarOpinionAFirestore(opinion: String) {
        setLoading(true)

        // Creamos el mapa de datos exactamente como en el resto de la app
        val opinionData = hashMapOf(
            "izena" to nombreUsuario,
            "gela" to codigoClase,
            "iritzia" to opinion, // Asegúrate que en AdminActivity busques "iritzia"
            "data" to System.currentTimeMillis()
        )

        // 1. REVISA EN FIREBASE: La colección debe llamarse exactamente "andra_mari_iritziak"
        db.collection("andra_mari_iritziak")
            .add(opinionData)
            .addOnSuccessListener { docRef ->
                Log.d("Firestore_OK", "Iritzia gordeta ID: ${docRef.id}")
                setLoading(false)
                Toast.makeText(this, "Iritzia ondo bidali da!", Toast.LENGTH_SHORT).show()
                irASiguiente()
            }
            .addOnFailureListener { e ->
                // ESTO TE DIRÁ EL ERROR REAL EN EL LOGCAT (Permisos, red, etc.)
                Log.e("Firestore_ERROR", "Errorea bidaltzerakoan", e)
                setLoading(false)
                Toast.makeText(this, "Errorea: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun irASiguiente() {
        val intent = Intent(this, AndraMari8Activity::class.java).apply {
            putExtra("USER_NAME", nombreUsuario)
            putExtra("CLASS_CODE", codigoClase)
        }
        startActivity(intent)
        finish()
    }

    private fun setLoading(isLoading: Boolean) {
        sendButton.isEnabled = !isLoading
        opinionEditText.isEnabled = !isLoading
        sendButton.text = if (isLoading) "BIDALTZEN..." else "BIDALI IRITZIA"
    }
}