package com.unairubenigor.gernikapp.irakasle

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.AstraAdminActivity
import com.unairubenigor.gernikapp.R

class IrakasleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Carga el XML del Login que acabamos de crear
        setContentView(R.layout.activity_irakasle)

        val etUsuario = findViewById<EditText>(R.id.etUsuarioProfesor)
        val etPassword = findViewById<EditText>(R.id.etPasswordProfesor)
        val btnEntrar = findViewById<Button>(R.id.btnLoginProfesor)
        val btnVolver = findViewById<Button>(R.id.btnVolverLogin)

        // Botón Entrar: Verifica la contraseña
        btnEntrar.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // AQUÍ ESTÁN LAS CREDENCIALES FIJAS
            if (usuario == "profesores" && password == "gernikapp") {
                Toast.makeText(this, "Acceso concedido", Toast.LENGTH_SHORT).show()

                // Si es correcto, nos vamos al Panel de Administración (AstraAdminActivity)
                val intent = Intent(this, AstraAdminActivity::class.java)
                startActivity(intent)

                // Cerramos el login para que no se pueda volver atrás
                finish()
            } else {
                Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_SHORT).show()
                etPassword.text.clear() // Borra la contraseña para reintentar
            }
        }

        // Botón Volver: Por si el alumno entró sin querer
        btnVolver.setOnClickListener {
            finish()
        }
    }
}