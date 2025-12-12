package com.unairubenigor.gernikapp.irakasle

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.R

class IrakasleActivity : AppCompatActivity() {

    // Genera un código aleatorio de 6 dígitos
    private fun generateRandomCode(): String {
        val charset = "0123456789"
        return (1..6)
            .map { charset.random() }
            .joinToString("")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.irakasle_codigo)

        // --- Configuración del Menú Común (Header) ---
        val headerView = findViewById<View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "KODEA" // Título para esta pantalla

        // --- Configuración del Menú Común (Footer) ---
        val footerView = findViewById<View>(R.id.footer_layout)

        // CORRECCIÓN: buttonContinue es ImageButton
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnContinue = footerView.findViewById<ImageButton>(R.id.buttonContinue)

        // Habilitamos el botón de ir atrás para volver a MainActivity
        btnBack.visibility = View.VISIBLE
        btnBack.setOnClickListener {
            finish()
        }

        // El botón JARRAITU (Continue) se mantendrá visible para avanzar una vez generado el código
        btnContinue.visibility = View.INVISIBLE // Inicialmente oculto hasta tener código

        // --- Lógica Principal de la Pantalla ---
        val buttonGenerate = findViewById<Button>(R.id.buttonGenerateCode)
        val textViewCode = findViewById<TextView>(R.id.textViewGeneratedCode)

        buttonGenerate.setOnClickListener {
            // 1. Genera el código
            val code = generateRandomCode()

            // 2. Muestra el código y lo hace visible
            textViewCode.text = code
            textViewCode.visibility = View.VISIBLE

            // 3. Oculta el botón de generación
            buttonGenerate.visibility = View.INVISIBLE

            // 4. Muestra el botón de continuar para avanzar a la siguiente pantalla lógica
            btnContinue.visibility = View.VISIBLE

            // Agrega aquí la lógica para guardar el código si es necesario

            // Ahora el botón de continuar tiene funcionalidad (ejemplo de avance)
            btnContinue.setOnClickListener {
                // Aquí va la navegación a la siguiente pantalla (e.g., Tablero de la clase)
                // val intent = Intent(this, NextScreenActivity::class.java)
                // startActivity(intent)
            }
        }
    }
}