package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.unairubenigor.gernikapp.R

class GernikakoArbolaExplica3Activity : AppCompatActivity() {

    private var nombreUsuario: String? = ""
    private var codigoClase: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arbola_explica3)

        nombreUsuario = intent.getStringExtra("USER_NAME")
        codigoClase = intent.getStringExtra("CLASS_CODE")

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "GERNIKAKO ARBOLA"

        // --- Cargar GIF del árbol ---
        val ivArbolGif = findViewById<ImageView>(R.id.iv_arbol_gif_explica3)
        Glide.with(this).asGif().load(R.raw.arbolgif).into(ivArbolGif)

        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE

        btnBack.setOnClickListener { finish() }
        btnNext.setOnClickListener {
            val intent = Intent(this, GernikakoArbolaBehatuActivity::class.java)
            intent.putExtra("USER_NAME", nombreUsuario)
            intent.putExtra("CLASS_CODE", codigoClase)
            startActivity(intent)
        }
    }
}