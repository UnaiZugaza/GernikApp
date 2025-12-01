package com.unairubenigor.gernikapp

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

        // Configurar botón SIGUIENTE (invisible, como pediste)
        btnContinue.visibility = android.view.View.INVISIBLE

        // --- Lógica de Botones de Opciones ---
        // Aquí puedes añadir qué pasa al pulsar cada botón (ir a detalle, marcar mapa, etc.)
        findViewById<Button>(R.id.btnAstra).setOnClickListener {
            Toast.makeText(this, "Astra aukeratuta", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<Button>(R.id.btnArbola).setOnClickListener {
            Toast.makeText(this, "Gernikako Arbola aukeratuta", Toast.LENGTH_SHORT).show()
        }
        
        // (Puedes añadir listeners para los demás botones aquí)
    }
}