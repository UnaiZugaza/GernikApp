package com.unairubenigor.gernikapp.irakasle

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.unairubenigor.gernikapp.Actividades_Geltokiak.AndraMariAdminActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.ArbolaAdminActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.AstraAdminActivity
import com.unairubenigor.gernikapp.R
import java.util.Calendar

class IrakasleActivity : AppCompatActivity() {

    private lateinit var tvCodigo: TextView
    private lateinit var btnGenerar: Button
    private lateinit var btnAstra: Button
    private lateinit var btnArbola: Button
    private lateinit var btnAndraMari: Button

    private var codigoClase: String = ""
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_irakasle)

        // Header Titulo
        val headerView = findViewById<View>(R.id.include_header)
        val title = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        title.text = getString(R.string.irakasle_gunea)

        // Footer Config
        val footerView = findViewById<View>(R.id.include_footer)
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = footerView.findViewById<ImageButton>(R.id.buttonContinue)
        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.GONE
        btnBack.setOnClickListener { finish() }

        // Referencias UI
        tvCodigo = findViewById(R.id.tvCodigoGenerado)
        btnGenerar = findViewById(R.id.btnGenerarCodigo)
        btnAstra = findViewById(R.id.btnAdminAstra)
        btnArbola = findViewById(R.id.btnAdminArbola)
        btnAndraMari = findViewById(R.id.btnAdminAndraMari)

        // 1. Botón para generar código
        btnGenerar.setOnClickListener {
            generarNuevaSala()
        }

        // 2. Botones de navegación (solo funcionan si hay código)
        btnAstra.setOnClickListener { irAAdmin(AstraAdminActivity::class.java) }
        btnArbola.setOnClickListener { irAAdmin(ArbolaAdminActivity::class.java) }
        btnAndraMari.setOnClickListener { irAAdmin(AndraMariAdminActivity::class.java) }
    }

    private fun generarNuevaSala() {
        val numeroRandom = (100000..999999).random()
        codigoClase = numeroRandom.toString()
        tvCodigo.text = codigoClase

        // Caducidad 2 días (TTL)
        val calendario = Calendar.getInstance()
        calendario.add(Calendar.DAY_OF_YEAR, 2)
        val fechaMuerte = calendario.time

        // Creamos la sala en Firebase
        val datosSala = hashMapOf(
            "estado" to "ESPERANDO",
            "asesino" to "",
            "fechaExpiracion" to fechaMuerte
        )

        db.collection("partidas").document(codigoClase)
            .set(datosSala)
            .addOnSuccessListener {
                Toast.makeText(this,
                    getString(R.string.gela_sortu_da, codigoClase), Toast.LENGTH_SHORT).show()

                // Habilitamos los botones para gestionar las actividades
                btnAstra.isEnabled = true
                btnArbola.isEnabled = true
                btnAndraMari.isEnabled = true
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.errorea_gela_sortzean), Toast.LENGTH_SHORT).show()
            }
    }

    private fun irAAdmin(claseDestino: Class<*>) {
        if (codigoClase.isNotEmpty()) {
            val intent = Intent(this, claseDestino)
            intent.putExtra("CLASS_CODE", codigoClase) // Pasamos el código
            startActivity(intent)
        }
    }
}