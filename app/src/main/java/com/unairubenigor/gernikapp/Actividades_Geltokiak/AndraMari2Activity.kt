package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.R

class AndraMari2Activity : AppCompatActivity() {

    private var nombreUsuario: String? = null
    private var codigoClase: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari2)

        // --- CORRECCIÓN CLAVE 1: Recoger la "mochila" de datos ---
        nombreUsuario = intent.getStringExtra("USER_NAME")
        codigoClase = intent.getStringExtra("CLASS_CODE")

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI ELIZA"

        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)
        val buttonGoazen = findViewById<Button>(R.id.button_goazen)

        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE

        btnBack.setOnClickListener {
            finish()
        }

        // --- CORRECCIÓN CLAVE 2: Pasar la "mochila" a la siguiente actividad ---
        val navigateToNext = {
            val intent = Intent(this, AndraMari3Activity::class.java)
            intent.putExtra("USER_NAME", nombreUsuario)
            intent.putExtra("CLASS_CODE", codigoClase)
            startActivity(intent)
        }

        btnNext.setOnClickListener { navigateToNext() }
        buttonGoazen.setOnClickListener { navigateToNext() }
    }
}