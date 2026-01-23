package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.R

class AndraMari4Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari4)

        // 1. CONFIGURACIÓN DEL HEADER
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI - 1. GELTOKIA"

        // 2. CONFIGURACIÓN DEL BOTÓN DE VIDEO
        val buttonPlayVideo = findViewById<ImageButton>(R.id.button_play_youtube_4)
        buttonPlayVideo.setOnClickListener {
            // Este Intent abre la app de YouTube o el navegador con el nuevo video
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/SuXIYJuH7z0"))
            startActivity(intent)
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
            // Navega a la siguiente pantalla
            val intent = Intent(this, AndraMari5Activity::class.java)
            startActivity(intent)
        }
    }
}
