package com.unairubenigor.gernikapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.unairubenigor.gernikapp.ikasle.IkasleActivity
import com.unairubenigor.gernikapp.irakasle.IrakasleActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var btnIrakasle: Button
    private lateinit var btnIkasle: Button
    private lateinit var btnIdioma: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio)

        // --- CONFIGURACIÓN DEL HEADER (CABECERA) ---
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "LOGIN" // O usa getString(R.string.tu_titulo)

        // LÓGICA DEL IDIOMA
        btnIdioma = findViewById(R.id.btnLanguage)

        // 1. AQUÍ lo hacemos visible (en el XML está oculto para los demás)
        btnIdioma.visibility = View.VISIBLE

        // 2. Al pulsar, cambiamos el idioma
        btnIdioma.setOnClickListener {
            val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
            // Si tiene "es", cambiamos a euskera. Si no, a español.
            val nuevoIdioma = if (currentLocale.contains("es")) "eu" else "es"

            val appLocale = LocaleListCompat.forLanguageTags(nuevoIdioma)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }

        // --- CONFIGURACIÓN DEL FOOTER (PIE) ---
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        // Ocultamos las flechas porque estamos en el Login
        btnBack.visibility = View.INVISIBLE
        btnNext.visibility = View.INVISIBLE

        // --- RESTO DE LA APP ---
        etNombre = findViewById(R.id.editTextNombre)
        btnIrakasle = findViewById(R.id.buttonIrakasle)
        btnIkasle = findViewById(R.id.buttonIkasle)

        // Botón Profesor
        btnIrakasle.setOnClickListener {
            val intent = Intent(this, IrakasleActivity::class.java)
            startActivity(intent)
        }

        // Botón Alumno
        btnIkasle.setOnClickListener {
            val nombre = etNombre.text.toString().trim()

            if (nombre.isEmpty()) {
                etNombre.error = getString(R.string.izena_beharrezkoa_da_nombre_obligatorio)
                Toast.makeText(this,
                    getString(R.string.mesedez_idatzi_zure_izena_ikaslea_bazara), Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, IkasleActivity::class.java)
                intent.putExtra("USER_NAME", nombre)
                startActivity(intent)
            }
        }
    }
}