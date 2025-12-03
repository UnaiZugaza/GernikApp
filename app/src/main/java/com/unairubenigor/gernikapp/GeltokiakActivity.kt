package com.unairubenigor.gernikapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GeltokiakActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geltokiak)

        // --- Header ---
        val headerView = findViewById<android.view.View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "GELTOKIAK"

        // --- Footer ---
        val footerView = findViewById<android.view.View>(R.id.footer_layout)
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnContinue = footerView.findViewById<ImageButton>(R.id.buttonContinue)

        // Configurar botón ATRÁS (visible)
        btnBack.visibility = android.view.View.VISIBLE
        btnBack.setOnClickListener {
            finish() // Vuelve al menú
        }

        // Configurar botón SIGUIENTE (invisible)
        btnContinue.visibility = android.view.View.INVISIBLE

        // --- Lógica de Botones de Opciones ---

        // 1. Astra
        findViewById<Button>(R.id.btnAstra).setOnClickListener {
            Toast.makeText(this, "Astra (Laster)", Toast.LENGTH_SHORT).show()
        }

        // 2. Gernikako Arbola
        findViewById<Button>(R.id.btnArbola).setOnClickListener {
            Toast.makeText(this, "Gernikako Arbola (Laster)", Toast.LENGTH_SHORT).show()
        }
        
        // 3. Pasileku
        findViewById<Button>(R.id.btnPasileku).setOnClickListener {
            Toast.makeText(this, "Pasileku (Laster)", Toast.LENGTH_SHORT).show()
        }
        
        // 4. Andra Mari
        findViewById<Button>(R.id.btnAndraMari).setOnClickListener {
            Toast.makeText(this, "Andra Mari (Laster)", Toast.LENGTH_SHORT).show()
        }

        // 5. GERNIKAKO MURALA - ¡Conectado!
        findViewById<Button>(R.id.btnMurala).setOnClickListener {
            val intent = Intent(this, GernikakoMuralaOngiEtorriActivity::class.java)
            startActivity(intent)
        }
    }
}