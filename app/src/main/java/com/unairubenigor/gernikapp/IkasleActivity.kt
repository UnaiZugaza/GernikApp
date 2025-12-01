package com.unairubenigor.gernikapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class IkasleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ikasle_codigo)

        // 1. Recoger el nombre de usuario de la actividad anterior
        val userName = intent.getStringExtra("USER_NAME")

        // --- Configuración del Header ---
        val headerView = findViewById<android.view.View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "KODEA"

        // --- Configuración del Footer ---
        val footerView = findViewById<android.view.View>(R.id.footer_layout)
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnContinue = footerView.findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = android.view.View.VISIBLE
        btnBack.setOnClickListener { finish() }

        btnContinue.visibility = android.view.View.VISIBLE

        // --- Lógica Principal ---
        val editTextCode = findViewById<EditText>(R.id.editTextClassCode)

        btnContinue.setOnClickListener {
            val code = editTextCode.text.toString()

            if (code.length == 6) {
                // NAVEGACIÓN A IKASLE MENU ACTIVITY
                val intent = Intent(this, IkasleMenuActivity::class.java)
                
                // 2. Pasar tanto el código como el nombre de usuario a la siguiente pantalla
                intent.putExtra("CLASS_CODE", code)
                intent.putExtra("USER_NAME", userName)
                
                startActivity(intent)
                
            } else {
                Toast.makeText(this, "Por favor, introduce un código de 6 dígitos.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}