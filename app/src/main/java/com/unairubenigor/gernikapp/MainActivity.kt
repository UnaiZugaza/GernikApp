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
        setContentView(R.layout.inicio)

        // --- Configuración del Header ---
        val headerView = findViewById<android.view.View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "HASIERA"

        // --- Configuración del Footer ---
        val footerView = findViewById<android.view.View>(R.id.footer_layout)
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnContinue = footerView.findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = android.view.View.INVISIBLE
        btnContinue.visibility = android.view.View.INVISIBLE

        // --- Lógica Principal ---
        val editTextNombre = findViewById<EditText>(R.id.editTextNombre)
        val buttonIrakasle = findViewById<Button>(R.id.buttonIrakasle)
        val buttonIkasle = findViewById<Button>(R.id.buttonIkasle)

        buttonIrakasle.setOnClickListener {
            startActivity(Intent(this, IrakasleActivity::class.java))
        }

        buttonIkasle.setOnClickListener {
            val nombre = editTextNombre.text.toString()

            // CORRECCIÓN: Apuntar a IkasleActivity (la del código)
            val intent = Intent(this, IkasleActivity::class.java)

            // Le pasamos el nombre a la siguiente pantalla
            intent.putExtra("USER_NAME", nombre)

            startActivity(intent)
        }
    }
}