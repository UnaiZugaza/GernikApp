package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.R

class GernikakoMuralaOngiEtorriActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gernikako_murala_ongi_etorri)

        // --- Header ---
        val headerView = findViewById<View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "MURALA"

        // --- Footer ---
        val footerView = findViewById<View>(R.id.footer_layout)
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnContinue = footerView.findViewById<ImageButton>(R.id.buttonContinue)

        // Botón ATRÁS: Visible y cierra la actividad
        btnBack.visibility = View.VISIBLE
        btnBack.setOnClickListener {
            finish()
        }

        // Botón SIGUIENTE: Visible y navega al vídeo (PENDIENTE)
        btnContinue.visibility = View.VISIBLE
        btnContinue.setOnClickListener {
            // Aquí navegaremos a la pantalla del vídeo
            Toast.makeText(this, "Hurrengo pantaila: Bideoa (Laster)", Toast.LENGTH_SHORT).show()
            // val intent = Intent(this, MuralaBideoaActivity::class.java)
            // startActivity(intent)
        }
    }
}