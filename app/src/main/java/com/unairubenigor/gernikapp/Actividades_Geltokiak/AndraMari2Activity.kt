package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.R

// Asumimos que la siguiente pantalla es Geltoki1Activity, cámbiala si es otra.
// import com.unairubenigor.gernikapp.Geltoki1Activity

class AndraMari2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari2)

        // 1. CONFIGURACIÓN DEL HEADER
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI ELIZA"

        // 2. REFERENCIAS A LOS BOTONES
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)
        val buttonGoazen = findViewById<Button>(R.id.button_goazen)

        // 3. CONFIGURACIÓN DE VISIBILIDAD DEL FOOTER
        btnBack.visibility = View.VISIBLE
        // Decide si la flecha de continuar debe ser visible. Si es la última pantalla,
        // podrías poner View.GONE
        btnNext.visibility = View.VISIBLE

        // 4. CONFIGURACIÓN DE LOS CLICKS

        // Botón para ir hacia atrás (vuelve a AndraMariActivity)
        btnBack.setOnClickListener {
            finish()
        }

        // Función para navegar a la siguiente pantalla (por ejemplo, el primer Geltoki)
        val navigateToNext = {
            val intent = Intent(this, AndraMari3Activity::class.java)
            startActivity(intent)
        }

        // Asignamos la función a ambos botones de "continuar"
        btnNext.setOnClickListener {
            navigateToNext()
        }
        buttonGoazen.setOnClickListener {
            navigateToNext()
        }
    }
}
