package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView // Importante: Usamos TextView, no Button
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.unairubenigor.gernikapp.R

class AndraMari1Activity : AppCompatActivity() {

    private var nombreUsuario: String? = null
    private var codigoClase: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Asegúrate de que este es el nombre correcto de tu XML (activity_andramari1 o activity_andramari)
        setContentView(R.layout.activity_andramari)

        nombreUsuario = intent.getStringExtra("USER_NAME")
        codigoClase = intent.getStringExtra("CLASS_CODE")

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI ELIZA"

        // --- Cargar GIF del árbol ---
        val ivArbolGif = findViewById<ImageView>(R.id.iv_arbol_gif_1)
        Glide.with(this).asGif().load(R.raw.arbolgif).into(ivArbolGif)

        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        // CORRECCIÓN AQUÍ:
        // 1. Cambiamos el nombre de la variable para que tenga sentido (textPrest)
        // 2. Buscamos un TextView (no un Button)
        // 3. Usamos el ID nuevo (text_prest)
        val textPrest = findViewById<TextView>(R.id.text_prest)

        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE

        btnBack.setOnClickListener {
            finish()
        }

        val navigateToNext = {
            val intent = Intent(this, AndraMari2Activity::class.java)
            intent.putExtra("USER_NAME", nombreUsuario)
            intent.putExtra("CLASS_CODE", codigoClase)
            startActivity(intent)
        }

        btnNext.setOnClickListener { navigateToNext() }

        // Asignamos el clic al TextView
        textPrest.setOnClickListener { navigateToNext() }
    }
}