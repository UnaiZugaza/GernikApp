package com.unairubenigor.gernikapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Usa el layout que tiene el campo para el nombre
        setContentView(R.layout.activity_main)

        // --- Configuración del Menú Común (Header) ---
        val headerView = findViewById<android.view.View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "HASIERA"

        // --- Configuración del Menú Común (Footer) ---
        val footerView = findViewById<android.view.View>(R.id.footer_layout)
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnContinue = footerView.findViewById<Button>(R.id.buttonContinue)

        btnBack.visibility = android.view.View.INVISIBLE
        btnContinue.visibility = android.view.View.INVISIBLE

        // --- Lógica Principal de la Pantalla ---
        val editTextNombre = findViewById<EditText>(R.id.editTextNombre)
        val buttonIrakasle = findViewById<Button>(R.id.buttonIrakasle)
        val buttonIkasle = findViewById<Button>(R.id.buttonIkasle)

        // Acción al pulsar IRAKASLE
        buttonIrakasle.setOnClickListener {
            val intent = Intent(this, IrakasleActivity::class.java)
            startActivity(intent)
        }

        // Acción al pulsar IKASLE
        buttonIkasle.setOnClickListener {
            val nombre = editTextNombre.text.toString()
            val intent = Intent(this, IkasleMenuActivity::class.java)
            // Pasamos el nombre a la siguiente actividad
            intent.putExtra("USER_NAME", nombre) 
            startActivity(intent)
        }
    }
}