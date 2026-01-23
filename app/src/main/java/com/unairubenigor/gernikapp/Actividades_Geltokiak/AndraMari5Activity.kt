package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.R

class AndraMari5Activity : AppCompatActivity() {

    private val correctAnswers = mapOf(
        R.id.kopla_answer_1 to "Santu",
        R.id.kopla_answer_2 to "Kristoren",
        R.id.kopla_answer_3 to "Maria",
        R.id.kopla_answer_4 to "Jose",
        R.id.kopla_answer_5 to "Jesus",
        R.id.kopla_answer_6 to "Maria",
        R.id.kopla_answer_7 to "Etxetxu",
        R.id.kopla_answer_8 to "Nor",
        R.id.kopla_answer_9 to "zeunke"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari5)

        // 1. Configuración del Header
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI - 2. GELTOKIA"

        // 2. Configuración del Botón de Video (como en MuralaVideoActivity)
        val buttonPlayVideo = findViewById<ImageButton>(R.id.button_play_youtube)
        buttonPlayVideo.setOnClickListener {
            // Este Intent abre la app de YouTube o el navegador
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/W36FCEdqc04"))
            startActivity(intent)
        }

        // 3. Configuración del Footer y la validación
        setupFooter()
    }

    private fun setupFooter() {
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE

        btnBack.setOnClickListener {
            finish()
        }

        btnNext.setOnClickListener {
            validateAnswers()
        }
    }

    private fun validateAnswers() {
        var allCorrect = true
        for ((editTextId, correctAnswer) in correctAnswers) {
            val editText = findViewById<EditText>(editTextId)
            val userAnswer = editText.text.toString().trim()

            if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
                editText.setBackgroundColor(Color.parseColor("#A5D6A7")) // Verde
            } else {
                editText.setBackgroundColor(Color.parseColor("#EF9A9A")) // Rojo
                // allCorrect = false // <-- RESTRICCIÓN COMENTADA
            }
        }

        if (allCorrect) {
            Toast.makeText(this, "Zorionak! Dena ondo dago!", Toast.LENGTH_LONG).show()
            val intent = Intent(this, AndraMari6Activity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Erantzun batzuk ez dira zuzenak. Saiatu berriro!", Toast.LENGTH_SHORT).show()
        }
    }

}
