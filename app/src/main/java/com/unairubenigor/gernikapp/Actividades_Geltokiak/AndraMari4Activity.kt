package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
    private lateinit var buttonContinue: ImageButton // Referencia a la flecha

    private var nombreUsuario: String = "Ezezaguna"
    private var codigoClase: String = ""
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari4)

        nombreUsuario = intent.getStringExtra("USER_NAME") ?: "Ezezaguna"
        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI"

        val buttonPlayVideo = findViewById<ImageButton>(R.id.button_play_youtube_4)
        buttonPlayVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/SuXIYJuH7z0"))
            startActivity(intent)
        }

        etAnswer1 = findViewById(R.id.edittext_answer1)
        etAnswer2 = findViewById(R.id.edittext_answer2)
        btnSend = findViewById(R.id.button_send_answers)

        btnSend.setOnClickListener {
            val r1 = etAnswer1.text.toString().trim()
            val r2 = etAnswer2.text.toString().trim()

            if (r1.isNotEmpty() && r2.isNotEmpty()) {
                if (codigoClase.isEmpty()){
                    Toast.makeText(this,
                        getString(R.string.errorea_gelaren_kodea_falta_da), Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                validateAndSendAnswers(r1, r2)
            } else {
                Toast.makeText(this,
                    getString(R.string.erantzun_bi_galderak_mesedez), Toast.LENGTH_SHORT).show()
            }
        }

        setupFooter()
    }

    private fun validateAndSendAnswers(r1: String, r2: String) {
        Toast.makeText(this, getString(R.string.erantzunak_bidaltzen), Toast.LENGTH_SHORT).show()
        enviarRespuestas(r1, r2)
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
        btnSend.text = getString(R.string.bidaltzen)

        db.collection("andra_mari_4_erantzunak").add(data)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.ondo_bidali_da), Toast.LENGTH_SHORT).show()
                btnSend.text = getString(R.string.bidalita)
                // --- ¡CORRECCIÓN CLAVE! --- 
                // Una vez enviado, hacemos visible la flecha para continuar.
                buttonContinue.visibility = View.VISIBLE
            }
            .addOnFailureListener { e ->
                btnSend.isEnabled = true
                btnSend.text = getString(R.string.bidali)
                // AÑADIDO: en caso de fallo, mostramos el mensaje de error original.
                Toast.makeText(this, "Errorea bidaltzean: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun setupFooter() {
        findViewById<ImageButton>(R.id.buttonBack).setOnClickListener { finish() }
        buttonContinue = findViewById(R.id.buttonContinue)

        // --- ¡CORRECCIÓN CLAVE! --- 
        // Ocultamos la flecha de continuar al iniciar la actividad.
        buttonContinue.visibility = View.GONE

        buttonContinue.setOnClickListener {
            val intent = Intent(this, AndraMari5Activity::class.java).apply {
                putExtra("USER_NAME", nombreUsuario)
                putExtra("CLASS_CODE", codigoClase)
            }
            startActivity(intent)
        }
    }
}