package com.unairubenigor.gernikapp.ikasle

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.unairubenigor.gernikapp.R

class IkasleActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var btnContinue: ImageButton // La hacemos global para poder bloquearla

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ikasle_codigo)

        // 1. Recoger el nombre (Viene de MainActivity, donde ERA OBLIGATORIO)
        val userName = intent.getStringExtra("USER_NAME") ?: ""

        // --- Header ---
        val headerView = findViewById<View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "KODEA"

        // --- Footer ---
        val footerView = findViewById<View>(R.id.footer_layout)
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        btnContinue = footerView.findViewById<ImageButton>(R.id.buttonContinue) // Asignamos a la variable global

        btnBack.visibility = View.VISIBLE
        btnBack.setOnClickListener { finish() }

        btnContinue.visibility = View.VISIBLE

        // --- Lógica ---
        val editTextCode = findViewById<EditText>(R.id.editTextClassCode)

        btnContinue.setOnClickListener {
            val code = editTextCode.text.toString().trim()

            if (code.length == 6) {
                // Bloqueamos botón y comprobamos
                verificarSalaYEntrar(code, userName)
            } else {
                Toast.makeText(this, "Mesedez, sartu 6 digituko kodea.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verificarSalaYEntrar(code: String, userName: String) {
        // 1. BLOQUEAR BOTÓN para evitar doble click
        btnContinue.isEnabled = false
        btnContinue.alpha = 0.5f // Efecto visual de deshabilitado
        Toast.makeText(this, "Egiaztatzen...", Toast.LENGTH_SHORT).show()

        db.collection("partidas").document(code).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // ¡SALA ENCONTRADA!
                    val intent = Intent(this, IkasleMenuActivity::class.java)
                    intent.putExtra("CLASS_CODE", code)
                    intent.putExtra("USER_NAME", userName)
                    startActivity(intent)

                    // Cerramos esta pantalla para que no vuelvan atrás al código
                    finish()
                } else {
                    // SALA NO EXISTE
                    btnContinue.isEnabled = true
                    btnContinue.alpha = 1.0f
                    Toast.makeText(this, "Gela hori ez da existitzen ($code).", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                // ERROR DE CONEXIÓN
                btnContinue.isEnabled = true
                btnContinue.alpha = 1.0f
                Toast.makeText(this, "Errorea konexioan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}