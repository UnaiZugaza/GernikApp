package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.Options.GeltokiakActivity
import com.unairubenigor.gernikapp.R

class AndraMari8Activity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari8)

        // 1. Configuración del Header
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ZORIONAK!"

        // 2. Configuración del Footer (ocultamos las flechas)
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnContinue = findViewById<ImageButton>(R.id.buttonContinue)
        btnBack.visibility = View.GONE
        btnContinue.visibility = View.GONE

        // 3. Botón para volver al menú de Geltokiak
        val buttonToMenu = findViewById<Button>(R.id.button_to_menu)
        buttonToMenu.setOnClickListener {
            // Creamos un Intent para volver a GeltokiakActivity
            val intent = Intent(this, GeltokiakActivity::class.java)
            // Añadimos flags para limpiar la pila de actividades y que no se pueda volver atrás
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish() // Cierra esta actividad
        }

        // 4. Reproducir el sonido de felicitación
        playSound()
    }

    private fun playSound() {
        // Detiene cualquier reproducción anterior
        mediaPlayer?.release()
        // Crea una nueva instancia y la inicia
        mediaPlayer = MediaPlayer.create(this, R.raw.jardueraburututa)
        mediaPlayer?.start()
    }

    override fun onStop() {
        super.onStop()
        // Liberamos los recursos del MediaPlayer cuando la actividad se detiene
        // para evitar fugas de memoria
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

