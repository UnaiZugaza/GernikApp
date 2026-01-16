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

    // Instancia de la base de datos
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ikasle_codigo)

        // 1. Recoger el nombre de usuario (y evitar nulos)
        val userName = intent.getStringExtra("USER_NAME") ?: ""

        // --- Configuración del Header ---
        val headerView = findViewById<View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "KODEA"

        // --- Configuración del Footer ---
        val footerView = findViewById<View>(R.id.footer_layout)
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnContinue = footerView.findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnBack.setOnClickListener { finish() }

        btnContinue.visibility = View.VISIBLE

        // --- Lógica Principal ---
        val editTextCode = findViewById<EditText>(R.id.editTextClassCode)

        btnContinue.setOnClickListener {
            val code = editTextCode.text.toString().trim()

            if (code.length == 6) {
                // AHORA VALIDAMOS AQUÍ EN LUGAR DE SOLO PASAR
                verificarSalaYEntrar(code, userName)
            } else {
                Toast.makeText(this, "Por favor, introduce un código de 6 dígitos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- FUNCIÓN DE VALIDACIÓN ---
    private fun verificarSalaYEntrar(code: String, userName: String) {
        // Opcional: Podrías mostrar un ProgressBar aquí si quisieras

        db.collection("partidas").document(code).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // ¡SALA ENCONTRADA! -> Pasamos al menú
                    Toast.makeText(this, "Código correcto. Entrando...", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, IkasleMenuActivity::class.java)
                    intent.putExtra("CLASS_CODE", code)
                    intent.putExtra("USER_NAME", userName)
                    startActivity(intent)

                    // Opcional: finish() si no quieres que puedan volver a meter el código
                } else {
                    // SALA NO EXISTE -> Error y nos quedamos aquí
                    Toast.makeText(this, "⛔ La sala $code no existe.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}