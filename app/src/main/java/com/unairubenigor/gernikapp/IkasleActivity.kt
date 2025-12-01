package com.unairubenigor.gernikapp

import android.content.Intent
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

        // --- Configuración del Header ---
        val headerView = findViewById<android.view.View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "KODEA"

        // --- Configuración del Footer ---
        val footerView = findViewById<android.view.View>(R.id.footer_layout)
        
        // Referencias a los botones del footer
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnContinue = footerView.findViewById<ImageButton>(R.id.buttonContinue)

        // Botón ATRÁS visible
        btnBack.visibility = android.view.View.VISIBLE
        btnBack.setOnClickListener {
            finish()
        }

        // Botón CONTINUAR visible
        btnContinue.visibility = android.view.View.VISIBLE

        // --- Lógica Principal ---
        val editTextCode = findViewById<EditText>(R.id.editTextClassCode)

        btnContinue.setOnClickListener {
            val code = editTextCode.text.toString()

            if (code.length == 6) {
                // Validación simple (aquí podrías comprobar si el código existe realmente)
                Toast.makeText(this, "Código $code enviado. ¡Entrando a la clase!", Toast.LENGTH_SHORT).show()
                
                // NAVEGACIÓN A IKASLE MENU ACTIVITY
                val intent = Intent(this, IkasleMenuActivity::class.java)
                // (Opcional) Pasamos el código a la siguiente actividad si hace falta
                intent.putExtra("CLASS_CODE", code)
                startActivity(intent)
                
            } else {
                Toast.makeText(this, "Por favor, introduce un código de 6 dígitos.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}