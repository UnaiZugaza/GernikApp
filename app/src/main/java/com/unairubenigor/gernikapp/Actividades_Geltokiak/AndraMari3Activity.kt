package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.R

class AndraMari3Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari3)

        // 1. CONFIGURACIÓN DEL HEADER
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI ELIZA"

        // 2. REFERENCIAS A LOS BOTONES DEL FOOTER
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        // 3. CONFIGURACIÓN DE VISIBILIDAD DEL FOOTER
        btnBack.visibility = View.VISIBLE
        // Decide si la flecha de continuar debe ser visible.
        btnNext.visibility = View.VISIBLE

        // 4. CONFIGURACIÓN DE LOS CLICKS
        btnBack.setOnClickListener {
            // Vuelve a AndraMari2Activity
            finish()
        }

        btnNext.setOnClickListener {
            // TODO: Navegar a la siguiente pantalla (Geltoki1Activity, por ejemplo)
            // val intent = Intent(this, Geltoki1Activity::class.java)
            // startActivity(intent)
        }
    }
}
