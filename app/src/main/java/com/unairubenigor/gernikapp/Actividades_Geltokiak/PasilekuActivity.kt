package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.R

class PasilekuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasileku)

        // Configurar Header
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "PASILEKU PLAZA"

        // Configurar Footer
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        // Hacer visible el botón de atrás si es necesario (en footer.xml está como invisible por defecto)
        btnBack.visibility = View.VISIBLE

        btnBack.setOnClickListener {
            finish()
        }

        btnNext.setOnClickListener {
            // Lógica para ir a la siguiente actividad
        }
    }
}