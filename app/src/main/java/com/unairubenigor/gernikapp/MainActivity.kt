package com.unairubenigor.gernikapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- Configuración del Menú Común (Header) ---
        // 🚨 CORREGIDO: Usar R.id.header_layout (ID de la etiqueta <include>) en lugar de R.id.menu_arriba
        val headerView = findViewById<android.view.View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        // Establecemos el título para esta pantalla
        headerTitle.text = "HASIERA"

        // --- Configuración del Menú Común (Footer) ---
        // 🚨 CORREGIDO: Usar R.id.footer_layout (ID de la etiqueta <include>) en lugar de R.id.footer
        val footerView = findViewById<android.view.View>(R.id.footer_layout)
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnContinue = footerView.findViewById<Button>(R.id.buttonContinue)

        // En la pantalla principal no mostramos el botón de ir atrás.
        btnBack.visibility = android.view.View.INVISIBLE
        // El botón JARRAITU (Continue) se usa para ir adelante después de la selección.
        // Lo ocultamos por ahora, ya que el diseño usa los botones de rol para la navegación.
        btnContinue.visibility = android.view.View.INVISIBLE

        // --- Lógica Principal de la Pantalla ---
        val buttonIrakasle = findViewById<Button>(R.id.buttonIrakasle)
        val buttonIkasle = findViewById<Button>(R.id.buttonIkasle)

        // Acción al pulsar IRAKASLE
        buttonIrakasle.setOnClickListener {
            // Navega a la pantalla del profesor (generar código)
            val intent = Intent(this, IrakasleActivity::class.java)
            startActivity(intent)
        }

        // Acción al pulsar IKASLE
        buttonIkasle.setOnClickListener {
            // Navega a la pantalla del estudiante (introducir código)
            val intent = Intent(this, IkasleActivity::class.java)
            startActivity(intent)
        }
    }
}