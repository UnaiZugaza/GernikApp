package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.R

class AndraMariActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari)

        // --- INICIO DEL CÓDIGO A INSERTAR ---

        // 1. CONFIGURACIÓN DEL HEADER
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI ELIZA"

        // 2. REFERENCIAS A LOS BOTONES
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)
        val buttonPrest = findViewById<Button>(R.id.button_prest)

        // 3. CONFIGURACIÓN DE VISIBILIDAD DEL FOOTER
        // Hacemos que AMBOS botones del footer sean visibles
        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE

        // 4. CONFIGURACIÓN DE LOS CLICKS

        // Botón para ir hacia atrás
        btnBack.setOnClickListener {
            // Cierra esta actividad y vuelve a la anterior
            finish()
        }

        // Botón para seguir (JARRAITU)
        btnNext.setOnClickListener {
            // TODO: Añadir aquí la navegación a la siguiente pantalla
            Toast.makeText(this, "Siguiente pantalla (aún por programar)", Toast.LENGTH_SHORT).show()
        }

        // Botón central "Prest Zaudete"
        buttonPrest.setOnClickListener {
            // TODO: Este botón debería hacer lo mismo que el de seguir
            Toast.makeText(this, "Botón 'Prest Zaudete' pulsado", Toast.LENGTH_SHORT).show()
        }

        // --- FIN DEL CÓDIGO A INSERTAR ---
    }
}
