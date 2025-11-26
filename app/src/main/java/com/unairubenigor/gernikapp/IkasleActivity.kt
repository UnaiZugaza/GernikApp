package com.unairubenigor.gernikapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class IkasleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ikasle_codigo)

        // --- Configuración del Menú Común (Header) ---
        val headerView = findViewById<android.view.View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "KODEA" // Título para esta pantalla

        // --- Configuración del Menú Común (Footer) ---
        val footerView = findViewById<android.view.View>(R.id.footer_layout)
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnContinue = footerView.findViewById<Button>(R.id.buttonContinue)

        // Habilitamos el botón de ir atrás para volver a MainActivity
        btnBack.visibility = android.view.View.VISIBLE
        btnBack.setOnClickListener {
            // Vuelve a la actividad anterior
            finish()
        }

        // El botón JARRAITU (Continue) es visible por defecto para enviar el código
        btnContinue.visibility = android.view.View.VISIBLE

        // --- Lógica Principal de la Pantalla ---
        val editTextCode = findViewById<EditText>(R.id.editTextClassCode)

        btnContinue.setOnClickListener {
            val code = editTextCode.text.toString()

            if (code.length == 6) {
                // Aquí iría la lógica para validar el código con un servidor o base de datos
                Toast.makeText(this, "Código $code enviado. ¡Entrando a la clase!", Toast.LENGTH_SHORT).show()
                // Navegación a la siguiente pantalla (e.g., Tablero del estudiante)
                // val intent = Intent(this, NextScreenActivity::class.java)
                // startActivity(intent)

            } else {
                Toast.makeText(this, "Por favor, introduce un código de 6 dígitos.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}