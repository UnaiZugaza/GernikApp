package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.R

class AndraMariActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari)

        // 1. CONFIGURACIÓN DEL HEADER
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI ELIZA"

        // 2. REFERENCIAS A LOS BOTONES
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)
        val buttonPrest = findViewById<Button>(R.id.button_prest)

        // 3. CONFIGURACIÓN DE VISIBILIDAD DEL FOOTER
        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE

        // 4. CONFIGURACIÓN DE LOS CLICKS

        btnBack.setOnClickListener {
            finish()
        }

        // Función para navegar a la siguiente pantalla
        val navigateToNext = {
            // Navega a AndraMari2Activity
            val intent = Intent(this, AndraMari2Activity::class.java)
            startActivity(intent)
        }

        // Los dos botones hacen lo mismo
        btnNext.setOnClickListener {
            navigateToNext()
        }
        buttonPrest.setOnClickListener {
            navigateToNext()
        }
    }
}
