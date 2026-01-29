package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.graphics.Color
import android.net.Uri
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

class AndraMari4Activity : AppCompatActivity() {

    private lateinit var etAnswer1: EditText
    private lateinit var etAnswer2: EditText
    private lateinit var btnSend: Button

    private var nombreUsuario: String = "Ezezaguna"
    private var codigoClase: String = ""
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari4)

        nombreUsuario = intent.getStringExtra("USER_NAME") ?: "Ezezaguna"
        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI - 1. GELTOKIA"

        val buttonPlayVideo = findViewById<ImageButton>(R.id.button_play_youtube_4)
        buttonPlayVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/SuXIYJuH7z0"))
            startActivity(intent)
        }

        etAnswer1 = findViewById(R.id.edittext_answer1)
        etAnswer2 = findViewById(R.id.edittext_answer2)
        btnSend = findViewById(R.id.button_send_answers)

        btnSend.setOnClickListener {
            val r1 = etAnswer1.text.toString().trim()
            val r2 = etAnswer2.text.toString().trim()

            if (r1.isNotEmpty() && r2.isNotEmpty()) {
                if (codigoClase.isEmpty()){
                    Toast.makeText(this, "Errorea: Gelaren kodea falta da.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                validateAndSendAnswers(r1, r2)
            } else {
                Toast.makeText(this, "Erantzun bi galderak mesedez", Toast.LENGTH_SHORT).show()
            }
        }

        setupFooter()
    }

    private fun validateAndSendAnswers(r1: String, r2: String) {
        // Validación visual (No bloquea el envío)
        val isR1Correct = r1.contains("koplak", ignoreCase = true) || r1.contains("abestu", ignoreCase = true) || r1.contains("jaiotza", ignoreCase = true)
        val isR2Correct = r2.contains("goizean", ignoreCase = true) || r2.contains("goizaldean", ignoreCase = true) || r2.contains("4", ignoreCase = true)

        etAnswer1.setBackgroundColor(if (isR1Correct) Color.parseColor("#A5D6A7") else Color.parseColor("#EF9A9A"))
        etAnswer2.setBackgroundColor(if (isR2Correct) Color.parseColor("#A5D6A7") else Color.parseColor("#EF9A9A"))

        if (isR1Correct && isR2Correct) {
            Toast.makeText(this, "Zuzen! Bidaltzen...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Erantzun batzuk ez dira zuzenak, baina bidali egingo ditugu.", Toast.LENGTH_SHORT).show()
        }

        enviarRespuestas(r1, r2)
    }

    private fun enviarRespuestas(r1: String, r2: String) {
        val data = hashMapOf(
            "izena" to nombreUsuario,
            "gela" to codigoClase,
            "erantzuna1" to r1,
            "erantzuna2" to r2,
            "data" to System.currentTimeMillis()
        )

        btnSend.isEnabled = false
        btnSend.text = "BIDALTZEN..."

        // Intentamos guardar en la colección
        db.collection("andra_mari_4_erantzunak")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Documento añadido con ID: ${documentReference.id}")
                Toast.makeText(this, "Ondo bidali da!", Toast.LENGTH_SHORT).show()
                btnSend.text = "BIDALITA ✅"
            }
            .addOnFailureListener { e ->
                // ESTO ES CLAVE: Ver el error real en el Logcat
                Log.e("Firestore Error", "Error al añadir respuestas", e)
                Toast.makeText(this, "Errorea: ${e.message}", Toast.LENGTH_LONG).show()
                btnSend.isEnabled = true
                btnSend.text = "BIDALI"
            }
    }

    private fun setupFooter() {
        findViewById<ImageButton>(R.id.buttonBack).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.buttonContinue).setOnClickListener {
            val intent = Intent(this, AndraMari5Activity::class.java).apply {
                putExtra("USER_NAME", nombreUsuario)
                putExtra("CLASS_CODE", codigoClase)
            }
            startActivity(intent)
        }
    }
}