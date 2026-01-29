package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.unairubenigor.gernikapp.R
import com.unairubenigor.gernikapp.ikasle.IkasleMenuActivity

class PasilekuEndActivity : AppCompatActivity() {

    private var nombreUsuario: String? = ""
    private var codigoClase: String? = ""
    private var mediaPlayer: MediaPlayer? = null // Variable para el reproductor de sonido

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasileku_end)

        // --- CÓDIGO AÑADIDO PARA EL SONIDO ---
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.jardueraburututa)
            mediaPlayer?.start() // Iniciar el sonido
            // Liberar recursos cuando el sonido termine
            mediaPlayer?.setOnCompletionListener {
                it.release()
            }
        } catch (e: Exception) {
            // Manejo de error por si el archivo no existe o está corrupto
            e.printStackTrace()
        }

        // Recoger datos
        nombreUsuario = intent.getStringExtra("USER_NAME")
        codigoClase = intent.getStringExtra("CLASS_CODE")

        // Configurar Header
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "PASILEKU PLAZA"

        // --- Cargar GIF del árbol ---
        val ivArbolGif = findViewById<ImageView>(R.id.iv_arbol_gif_pasileku_end)
        Glide.with(this).asGif().load(R.raw.arbolgif).into(ivArbolGif)

        // Configurar Footer
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.INVISIBLE // No hay siguiente pantalla

        btnBack.setOnClickListener {
            finish()
        }

        // Botón Menura Bueltatu
        val btnVolverMenu = findViewById<Button>(R.id.btn_volver_menu)
        btnVolverMenu.setOnClickListener {
            val intent = Intent(this, IkasleMenuActivity::class.java)
            intent.putExtra("USER_NAME", nombreUsuario)
            intent.putExtra("CLASS_CODE", codigoClase)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Nos aseguramos de liberar el reproductor si la actividad se destruye
        mediaPlayer?.release()
        mediaPlayer = null
    }
}