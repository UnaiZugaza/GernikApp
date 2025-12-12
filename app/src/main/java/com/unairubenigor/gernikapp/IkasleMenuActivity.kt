package com.unairubenigor.gernikapp

import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.unairubenigor.gernikapp.Options.GeltokiakActivity
import com.unairubenigor.gernikapp.Options.OptionMapsActivity
import com.unairubenigor.gernikapp.Options.OptionPuzzleActivity
import com.unairubenigor.gernikapp.Options.OptionRankingActivity

class IkasleMenuActivity : AppCompatActivity() {

    // Variable para guardar el nombre temporalmente mientras estamos en el menú
    private var nombreUsuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ikasle_menu)

        // 1. RECUPERAR EL NOMBRE QUE VIENE DE MAINACTIVITY
        nombreUsuario = intent.getStringExtra("USER_NAME")

        // (Opcional) Mostrar "Kaixo Unai!" en el header
        val textViewUserName = findViewById<TextView>(R.id.textViewUserName)
        if (!nombreUsuario.isNullOrEmpty()) {
            textViewUserName.text = "Kaixo, $nombreUsuario!"
        }

        // --- Configuración de las tarjetas ---
        val cardOption1: CardView = findViewById(R.id.cardOption1) // Mapas
        val cardOption2: CardView = findViewById(R.id.cardOption2) // Geltokiak
        val cardOption3: CardView = findViewById(R.id.cardOption3) // Ranking
        val cardOption4: CardView = findViewById(R.id.cardOption4) // Puzzle

        // 2. AL PULSAR LOS BOTONES, PASAMOS EL NOMBRE A LA SIGUIENTE PANTALLA
        cardOption1.setOnClickListener {
            navegarA(OptionMapsActivity::class.java)
        }

        cardOption2.setOnClickListener {
            navegarA(GeltokiakActivity::class.java)
        }

        cardOption3.setOnClickListener {
            // ¡ESTE ES EL IMPORTANTE! Al ir al Ranking, llevamos el nombre
            navegarA(OptionRankingActivity::class.java)
        }

        cardOption4.setOnClickListener {
            navegarA(OptionPuzzleActivity::class.java)
        }
    }

    // He mejorado tu función navigateTo para que meta el nombre en el Intent
    private fun navegarA(claseDestino: Class<*>) {
        val intent = Intent(this, claseDestino)

        // Si tenemos un nombre guardado, lo metemos en la maleta para el siguiente viaje
        if (nombreUsuario != null) {
            intent.putExtra("USER_NAME", nombreUsuario)
        }

        startActivity(intent)
    }
}