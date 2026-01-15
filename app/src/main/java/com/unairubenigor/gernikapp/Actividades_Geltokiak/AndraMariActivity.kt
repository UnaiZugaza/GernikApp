package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.R

class AndraMariActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Conectamos el layout que hemos diseñado
        setContentView(R.layout.activity_andramari)

        // 2. Referenciamos los botones del layout
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)
        val buttonPrest = findViewById<Button>(R.id.button_prest)

        // 3. Añadimos la funcionalidad a los botones

        // Botón para ir hacia atrás (Error corregido: usa 'btnBack')
        btnBack.setOnClickListener {
            // Cierra esta actividad y vuelve a la anterior
            finish()
        }

        // Botón para seguir (JARRAITU - Error corregido: usa 'btnNext')
        btnNext.setOnClickListener {
            // TODO: Aquí debes poner el código para ir a la siguiente pantalla
            // Ejemplo:
            // val intent = Intent(this, SiguienteActividad::class.java)
            // startActivity(intent)

            // De momento, mostramos un mensaje
            Toast.makeText(this, "Siguiente pantalla (aún por programar)", Toast.LENGTH_SHORT).show()
        }

        // Botón central "Prest Zaudete"
        buttonPrest.setOnClickListener {
            // TODO: Define qué debe hacer este botón. Quizás lo mismo que "JARRAITU".
            // Ejemplo:
            // val intent = Intent(this, SiguienteActividad::class.java)
            // startActivity(intent)

            Toast.makeText(this, "Botón 'Prest Zaudete' pulsado", Toast.LENGTH_SHORT).show()
        }
    }
}
