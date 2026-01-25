package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.unairubenigor.gernikapp.R

class AndraMari4Activity : AppCompatActivity() {

    private lateinit var etAnswer1: EditText
    private lateinit var etAnswer2: EditText
    private lateinit var btnSend: Button

    private var nombreUsuario: String = "Ezezaguna"
    private var codigoClase: String = ""
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari4)

        // --- CORRECCIÓN CLAVE 1: Recoger siempre los datos --- 
        nombreUsuario = intent.getStringExtra("USER_NAME") ?: "Ezezaguna"
        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI - 1. GELTOKIA"

        etAnswer1 = findViewById(R.id.edittext_answer1)
        etAnswer2 = findViewById(R.id.edittext_answer2)
        btnSend = findViewById(R.id.button_send_answers)

        btnSend.setOnClickListener {
            val r1 = etAnswer1.text.toString().trim()
            val r2 = etAnswer2.text.toString().trim()

            if (r1.isNotEmpty() && r2.isNotEmpty()) {
                // Comprobamos que el código de la clase exista antes de enviar
                if (codigoClase.isEmpty()){
                    Toast.makeText(this, "Errorea: Gelaren kodea falta da. Berrabiarazi aplikazioa.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                enviarRespuestas(r1, r2)
            } else {
                Toast.makeText(this, "Erantzun bi galderak mesedez", Toast.LENGTH_SHORT).show()
            }
        }

        setupFooter()
    }

    private fun enviarRespuestas(r1: String, r2: String) {
        val data = hashMapOf(
            "izena" to nombreUsuario,
            "gela" to codigoClase,
            "erantzuna1" to r1,
            "erantzuna2" to r2,
            "data" to System.currentTimeMillis()
        )

        btnSend.isEnabled = false

        // --- CORRECCIÓN CLAVE 2: Enviar a la colección correcta ---
        db.collection("andra_mari_4_erantzunak").add(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Ondo bidali da!", Toast.LENGTH_SHORT).show()
                btnSend.text = "BIDALITA ✅"
            }
            .addOnFailureListener {
                btnSend.isEnabled = true
                Toast.makeText(this, "Errorea bidaltzerakoan", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupFooter() {
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)
        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE

        btnBack.setOnClickListener { finish() }

        // --- CORRECCIÓN CLAVE 3: Pasar la "mochila" a la siguiente actividad ---
        btnNext.setOnClickListener {
            val intent = Intent(this, AndraMari5Activity::class.java)
            intent.putExtra("USER_NAME", nombreUsuario)
            intent.putExtra("CLASS_CODE", codigoClase)
            startActivity(intent)
        }
    }
}