package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.os.Bundle
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

        // Recuperar datos del intent
        nombreUsuario = intent.getStringExtra("USER_NAME") ?: "Ezezaguna"
        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        // Configurar Título del Header
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI - 4. GELTOKIA"

        // Inicializar vistas
        opinionEditText = findViewById(R.id.et_opinion) // ID Corregido
        sendButton = findViewById(R.id.button_send_opinion)

        sendButton.setOnClickListener {
            val textoOpinion = opinionEditText.text.toString().trim()

            if (textoOpinion.isNotEmpty()) {
                if (codigoClase.isEmpty()) {
                    Toast.makeText(this, "Errorea: Gela kodea falta da", Toast.LENGTH_SHORT).show()
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

        btnNext.setOnClickListener {
            val intent = Intent(this, AndraMari8Activity::class.java)
            startActivity(intent)
        }

        btnBack.setOnClickListener { finish() }
    }

    private fun enviarOpinionAFirestore(opinion: String) {
        setLoading(true)

        val opinionData = hashMapOf(
            "izena" to nombreUsuario,
            "gela" to codigoClase,
            "iritzia" to opinion,
            "data" to System.currentTimeMillis()
        )

        // Lo guardamos en una colección llamada "andra_mari_iritziak"
        db.collection("andra_mari_iritziak")
            .add(opinionData)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(this, "Iritzia ondo bidali da!", Toast.LENGTH_LONG).show()

                // Ir a la siguiente actividad tras enviar
                val intent = Intent(this, AndraMari8Activity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Errorea bidaltzerakoan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            sendButton.isEnabled = false
            opinionEditText.isEnabled = false
            sendButton.text = "BIDALTZEN..."
        } else {
            sendButton.isEnabled = true
            opinionEditText.isEnabled = true
            sendButton.text = "BIDALI IRITZIA"
        }
    }
}