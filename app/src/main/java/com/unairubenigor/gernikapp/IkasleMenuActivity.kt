package com.unairubenigor.gernikapp

import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.content.Intent
import android.os.Bundle
import android.widget.TextView

class IkasleMenuActivity : AppCompatActivity() {

    // Define las clases de destino
    companion object {
        private val ACTIVITY_A = OptionMapsActivity::class.java
        
        // CAMBIO: Ahora la opción B (Geltokiak) va a GeltokiakActivity
        private val ACTIVITY_B = GeltokiakActivity::class.java
        
        private val ACTIVITY_C = OptionRankingActivity::class.java
        private val ACTIVITY_D = OptionPuzzleActivity::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ikasle_menu)

        // --- Configuración del Menú Común (Header) ---
        val headerView = findViewById<android.view.View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "MENUA"

        // --- Recibir y mostrar el nombre del usuario ---
        val userName = intent.getStringExtra("USER_NAME")
        val textViewUserName = findViewById<TextView>(R.id.textViewUserName)
        
        if (!userName.isNullOrEmpty()) {
            textViewUserName.text = "Kaixo, $userName!"
        }

        // --- Configuración de las tarjetas ---
        val cardOption1: CardView = findViewById(R.id.cardOption1)
        val cardOption2: CardView = findViewById(R.id.cardOption2)
        val cardOption3: CardView = findViewById(R.id.cardOption3)
        val cardOption4: CardView = findViewById(R.id.cardOption4)

        cardOption1.setOnClickListener { navigateTo(ACTIVITY_A) }
        cardOption2.setOnClickListener { navigateTo(ACTIVITY_B) } // Abre GeltokiakActivity
        cardOption3.setOnClickListener { navigateTo(ACTIVITY_C) }
        cardOption4.setOnClickListener { navigateTo(ACTIVITY_D) }
    }

    private fun navigateTo(destinationClass: Class<*>) {
        val intent = Intent(this, destinationClass)
        startActivity(intent)
    }
}