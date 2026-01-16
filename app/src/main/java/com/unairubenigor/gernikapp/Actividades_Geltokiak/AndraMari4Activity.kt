package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.R

class AndraMari4Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari4)

        // 1. CONFIGURACIÓN DEL HEADER
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI - 1. GELTOKIA" // O el título que prefieras

        // 2. CONFIGURACIÓN DEL VIDEO
        val videoView = findViewById<VideoView>(R.id.video_view)
        // Construye la ruta al video en la carpeta res/raw
        val videoPath = "android.resource://" + packageName + "/" + R.raw.video_andramari // Asegúrate que el nombre del archivo es correcto
        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        // Añade los controles de reproducción (play, pausa, barra de progreso)
        val mediaController = MediaController(this)
        videoView.setMediaController(mediaController)
        mediaController.setAnchorView(videoView)

        // Inicia el video automáticamente al entrar en la pantalla
        videoView.setOnPreparedListener {
            it.start()
        }

        // 3. CONFIGURACIÓN DEL FOOTER
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE

        btnBack.setOnClickListener {
            // Vuelve a AndraMari3Activity
            finish()
        }

        btnNext.setOnClickListener {
            val intent = Intent(this, AndraMari5Activity::class.java)
            startActivity(intent)
        }
    }
}
