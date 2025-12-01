package com.unairubenigor.gernikapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class IkasleActivity : AppCompatActivity() {

    // Declarar las vistas
    private lateinit var classCodeEditText: EditText
    private lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ikasle_codigo) // Asumo que el layout se llama activity_ikasle.xml

        // 1. Inicializar las vistas
        classCodeEditText = findViewById(R.id.editTextClassCode)

        // El botón "Jarraitu" está en el layout incluido (footer_layout).
        // Primero encontramos el layout padre (footer_layout) y luego el botón dentro de él.
        val footerLayout = findViewById<android.view.View>(R.id.footer_layout)
        continueButton = footerLayout.findViewById(R.id.buttonContinue) // **¡IMPORTANTE! Usa el ID real del botón "Jarraitu"**

        // 2. Configurar el listener del botón
        continueButton.setOnClickListener {
            // Llamar a la función de validación y navegación
            validateAndNavigate()
        }
    }

    private fun validateAndNavigate() {
        val classCode = classCodeEditText.text.toString().trim()

        // 1. Validar que el código tiene exactamente 6 dígitos
        if (classCode.length != 6) {
            Toast.makeText(this, "Kodeak 6 digitu izan behar ditu.", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Validación de la base de datos (SIMULACIÓN)
        // En una app real, aquí enviarías 'classCode' a un servidor o base de datos
        // para verificar si es válido.
        val isCodeValid = true // SIMULAMOS que el código es válido para la navegación

        if (isCodeValid) {
            // 3. Crear el Intent para navegar a IkasleMenuActivity
            val intent = Intent(this, IkasleMenuActivity::class.java)
            // Opcional: Puedes pasar el código de clase a la siguiente actividad si es necesario
            intent.putExtra("CLASS_CODE", classCode)
            startActivity(intent)
            finish() // Opcional: Cierra IkasleActivity para que el usuario no pueda volver atrás con el botón físico 'Atrás'

        } else {
            // Mensaje de error si la base de datos dice que el código no existe
            Toast.makeText(this, "Kodea ez da zuzena. Saiatu berriro.", Toast.LENGTH_SHORT).show()
        }
    }
}