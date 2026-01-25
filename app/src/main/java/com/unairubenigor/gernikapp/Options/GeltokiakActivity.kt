package com.unairubenigor.gernikapp.Options

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.*
import com.unairubenigor.gernikapp.R

class GeltokiakActivity : AppCompatActivity() {

    private var nombreUsuario: String? = ""
    private var codigoClase: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geltokiak)

        // 1. RECOGEMOS LOS DATOS DEL MENÚ
        nombreUsuario = intent.getStringExtra("USER_NAME")
        codigoClase = intent.getStringExtra("CLASS_CODE")

        // --- Header ---
        val headerView = findViewById<View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "GELTOKIAK"

        // --- Footer ---
        val footerView = findViewById<View>(R.id.footer_layout)
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnContinue = footerView.findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnBack.setOnClickListener { finish() }
        btnContinue.visibility = View.INVISIBLE

        // --- BOTONES CON PASO DE DATOS ---
        findViewById<Button>(R.id.btnAstra).setOnClickListener {
            abrirActividad(AstraActivity::class.java)
        }
        findViewById<Button>(R.id.btnArbola).setOnClickListener {
            abrirActividad(GernikakoArbolaOngiEtorriActivity::class.java)
        }
        findViewById<Button>(R.id.btnPasileku).setOnClickListener {
            abrirActividad(PasilekuActivity::class.java)
        }
        findViewById<Button>(R.id.btnAndraMari).setOnClickListener {
            abrirActividad(AndraMari1Activity::class.java)
        }
        findViewById<Button>(R.id.btnMurala).setOnClickListener {
            abrirActividad(GernikakoMuralaOngiEtorriActivity::class.java)
        }
    }

    // ¡¡¡AQUÍ ESTÁ LA SOLUCIÓN!!!
    // Ahora esta función SIEMPRE pasa los datos del alumno a la siguiente pantalla.
    private fun abrirActividad(clase: Class<*>) {
        val intent = Intent(this, clase)
        intent.putExtra("USER_NAME", nombreUsuario)
        intent.putExtra("CLASS_CODE", codigoClase)
        startActivity(intent)
    }
}