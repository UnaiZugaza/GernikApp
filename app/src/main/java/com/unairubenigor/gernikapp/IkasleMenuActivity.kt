package com.unairubenigor.gernikapp

import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.content.Intent
import android.os.Bundle
import com.unairubenigor.gernikapp.R

class IkasleMenuActivity : AppCompatActivity() {

    // Define las clases de destino en un Companion Object (el equivalente a static en Java)
    companion object {
        // IMPORTANTE: Debes crear estas Activities de destino (OptionAActivity, etc.)
        // y registrarlas en tu AndroidManifest.xml
        private val ACTIVITY_A = OptionAActivity::class.java // ej., Jarduerak
        private val ACTIVITY_B = OptionBActivity::class.java // ej., Argazkiak
        private val ACTIVITY_C = OptionCActivity::class.java // ej., Bideoak
        private val ACTIVITY_D = OptionDActivity::class.java // ej., Informazioa
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Asume que el layout sigue llamándose activity_student_menu
        setContentView(R.layout.activity_student_menu)

        // Encuentra los cuatro elementos CardView
        val cardOption1: CardView = findViewById(R.id.cardOption1)
        val cardOption2: CardView = findViewById(R.id.cardOption2)
        val cardOption3: CardView = findViewById(R.id.cardOption3)
        val cardOption4: CardView = findViewById(R.id.cardOption4)

        // Establece listeners para cada opción usando lambdas de Kotlin
        cardOption1.setOnClickListener { navigateTo(ACTIVITY_A) }
        cardOption2.setOnClickListener { navigateTo(ACTIVITY_B) }
        cardOption3.setOnClickListener { navigateTo(ACTIVITY_C) }
        cardOption4.setOnClickListener { navigateTo(ACTIVITY_D) }
    }

    /**
     * Método de ayuda para iniciar una nueva Activity.
     * @param destinationClass La clase de Activity de destino.
     */
    private fun navigateTo(destinationClass: Class<*>) {
        // 'this' hace referencia al contexto de la actividad
        val intent = Intent(this, destinationClass)
        startActivity(intent)
    }
}