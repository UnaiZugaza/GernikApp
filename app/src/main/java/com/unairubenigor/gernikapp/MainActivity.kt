package com.unairubenigor.gernikapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.ikasle.IkasleActivity
import com.unairubenigor.gernikapp.irakasle.IrakasleActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var btnIrakasle: Button
    private lateinit var btnIkasle: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio) // Usamos TU diseño original

        etNombre = findViewById(R.id.editTextNombre)
        btnIrakasle = findViewById(R.id.buttonIrakasle)
        btnIkasle = findViewById(R.id.buttonIkasle)

        // --- LÓGICA PROFESOR (Nombre NO obligatorio) ---
        btnIrakasle.setOnClickListener {
            // El profe pasa directo, sin comprobar nada
            val intent = Intent(this, IrakasleActivity::class.java)
            startActivity(intent)
        }

        // --- LÓGICA ALUMNO (Nombre SÍ obligatorio) ---
        btnIkasle.setOnClickListener {
            val nombre = etNombre.text.toString().trim()

            if (nombre.isEmpty()) {
                // Si está vacío, NO pasa y muestra error
                etNombre.error = "Izena beharrezkoa da (Nombre obligatorio)"
                Toast.makeText(this, "Mesedez, idatzi zure izena ikaslea bazara", Toast.LENGTH_SHORT).show()
            } else {
                // Si tiene nombre, PASA a la pantalla de poner el código
                val intent = Intent(this, IkasleActivity::class.java)
                intent.putExtra("USER_NAME", nombre) // Nos llevamos el nombre a la siguiente pantalla
                startActivity(intent)
            }
        }
    }
}