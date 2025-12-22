package com.unairubenigor.gernikapp.ikasle

import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.unairubenigor.gernikapp.Options.GeltokiakActivity
import com.unairubenigor.gernikapp.Options.OptionMapsActivity
import com.unairubenigor.gernikapp.Options.OptionPuzzleActivity
import com.unairubenigor.gernikapp.Options.OptionRankingActivity
import com.unairubenigor.gernikapp.R

class IkasleMenuActivity : AppCompatActivity() {

    private var nombreUsuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ikasle_menu)

        nombreUsuario = intent.getStringExtra("USER_NAME")

        val textViewUserName = findViewById<TextView>(R.id.textViewUserName)
        if (!nombreUsuario.isNullOrEmpty()) {
            textViewUserName.text = "Kaixo, $nombreUsuario!"
        }

        val cardOption1: CardView = findViewById(R.id.cardOption1)
        val cardOption2: CardView = findViewById(R.id.cardOption2)
        val cardOption3: CardView = findViewById(R.id.cardOption3)
        val cardOption4: CardView = findViewById(R.id.cardOption4)

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
        if (nombreUsuario != null) {
            intent.putExtra("USER_NAME", nombreUsuario)
        }
        startActivity(intent)
    }
}