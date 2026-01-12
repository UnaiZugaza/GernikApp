package com.unairubenigor.gernikapp.irakasle

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.R

class IrakasleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Usamos el diseño del Login que creamos (activity_irakasle.xml)
        setContentView(R.layout.activity_irakasle)

        val etUsuario = findViewById<EditText>(R.id.etUsuarioProfesor)
        val etPassword = findViewById<EditText>(R.id.etPasswordProfesor)
        val btnEntrar = findViewById<Button>(R.id.btnLoginProfesor)
        val btnVolver = findViewById<Button>(R.id.btnVolverLogin)

        // Botón Entrar: Verifica usuario y contraseña
        btnEntrar.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // 1. Comprobamos las credenciales
            if (usuario == "profesores" && password == "gernikapp") {
                Toast.makeText(this, "Acceso concedido", Toast.LENGTH_SHORT).show()

                // 2. CAMBIO IMPORTANTE: Abrimos el MENÚ DE SELECCIÓN (donde están los 3 botones)
                val intent = Intent(this, IrakasleMenuActivity::class.java)
                startActivity(intent)

                // 3. Cerramos la pantalla de login para que no puedan volver atrás
                finish()
            } else {
                Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_SHORT).show()
                etPassword.text.clear() // Limpiamos la contraseña para reintentar
            }
        }

        // Botón Volver: Por si entraste aquí por error
        btnVolver.setOnClickListener {
            finish()
        }
    }
}