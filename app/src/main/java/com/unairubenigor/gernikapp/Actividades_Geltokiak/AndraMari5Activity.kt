package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.R

class AndraMari5Activity : AppCompatActivity() {

    // Mapa para asociar cada EditText con su respuesta correcta (ignorando mayúsculas)
    private val correctAnswers = mapOf(
        R.id.kopla_answer_1 to "Santu",
        R.id.kopla_answer_2 to "Kristoren",
        R.id.kopla_answer_3 to "Maria",
        R.id.kopla_answer_4 to "Jose",
        R.id.kopla_answer_5 to "Jesus",
        R.id.kopla_answer_6 to "Maria",
        R.id.kopla_answer_7 to "Etxe",
        R.id.kopla_answer_8 to "Noretan",
        R.id.kopla_answer_9 to "zeuke"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari5)

        // 1. Configuración del Header
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI - 2. GELTOKIA"

        // 2. Configuración del Video
        setupVideo()

        // 3. Configuración del Footer y la validación
        setupFooter()
    }

    private fun setupVideo() {
        val videoView = findViewById<VideoView>(R.id.video_view_kopla)
        val videoPath = "android.resource://" + packageName + "/" + R.raw.video_andramari2
        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        val mediaController = MediaController(this)
        videoView.setMediaController(mediaController)
        mediaController.setAnchorView(videoView)

        videoView.setOnPreparedListener {
            it.start()
        }
    }

    private fun setupFooter() {
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE

        btnBack.setOnClickListener {
            finish() // Vuelve a AndraMari4Activity
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

            // Comparamos ignorando mayúsculas/minúsculas
            if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
                // Respuesta correcta: fondo verde
                editText.setBackgroundColor(Color.parseColor("#A5D6A7")) // Verde claro
            } else {
                // Respuesta incorrecta: fondo rojo
                editText.setBackgroundColor(Color.parseColor("#EF9A9A")) // Rojo claro
                allCorrect = false
            }
        }

        if (allCorrect) {
            Toast.makeText(this, "Zorionak! Dena ondo dago!", Toast.LENGTH_LONG).show()
            // TODO: Aquí puedes navegar a la siguiente actividad si lo deseas
            // val intent = Intent(this, SiguienteActividad::class.java)
            // startActivity(intent)
        } else {
            Toast.makeText(this, "Erantzun batzuk ez dira zuzenak. Saiatu berriro!", Toast.LENGTH_SHORT).show()
        }
    }
}
