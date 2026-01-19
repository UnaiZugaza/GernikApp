package com.unairubenigor.gernikapp.ikasle

import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.unairubenigor.gernikapp.Options.GeltokiakActivity
import com.unairubenigor.gernikapp.Options.OptionMapsActivity
import com.unairubenigor.gernikapp.Options.OptionPuzzleActivity
import com.unairubenigor.gernikapp.Options.OptionRankingActivity
import com.unairubenigor.gernikapp.R

class IkasleMenuActivity : AppCompatActivity() {

    private var nombreUsuario: String? = null
    private var codigoClase: String? = null // NUEVO: Variable para el código

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ikasle_menu)

        // RECUPERAMOS AMBOS DATOS
        nombreUsuario = intent.getStringExtra("USER_NAME")
        codigoClase = intent.getStringExtra("CLASS_CODE") // NUEVO

        // Configurar Header - Cambiar título a "MENÚ"
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "MENÚ"

        // Configurar Footer
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.INVISIBLE // Ocultar el botón de hacia adelante

        btnBack.setOnClickListener {
            finish() // Volver atrás (a la pantalla anterior, probablemente identificación)
        }

        val textViewUserName = findViewById<TextView>(R.id.textViewUserName)
        if (!nombreUsuario.isNullOrEmpty()) {
            textViewUserName.text = "Kaixo, $nombreUsuario!"
        }

        val cardOption1: CardView = findViewById(R.id.cardOption1) // Mapa
        val cardOption2: CardView = findViewById(R.id.cardOption2) // Geltokiak (Estaciones)
        val cardOption3: CardView = findViewById(R.id.cardOption3) // Ranking
        val cardOption4: CardView = findViewById(R.id.cardOption4) // Puzzle

        cardOption1.setOnClickListener {
            navegarA(OptionMapsActivity::class.java)
        }

        cardOption2.setOnClickListener {
            navegarA(GeltokiakActivity::class.java)
        }

        cardOption3.setOnClickListener {
            navegarA(OptionRankingActivity::class.java)
        }

        cardOption4.setOnClickListener {
            navegarA(OptionPuzzleActivity::class.java)
        }
    }

    private fun navegarA(claseDestino: Class<*>) {
        val intent = Intent(this, claseDestino)
        // PASAMOS LA "MOCHILA" DE DATOS A LA SIGUIENTE PANTALLA
        if (nombreUsuario != null) {
            intent.putExtra("USER_NAME", nombreUsuario)
        }
        if (codigoClase != null) {
            intent.putExtra("CLASS_CODE", codigoClase) // NUEVO: Enviamos el código
        }
        startActivity(intent)
    }
}