package com.unairubenigor.gernikapp.Options

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.AndraMariActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.ArbolaActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.AstraActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.GernikakoMuralaOngiEtorriActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.PasilekuActivity
import com.unairubenigor.gernikapp.R

class GeltokiakActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geltokiak)

        // --- Header ---
        val headerView = findViewById<View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "GELTOKIAK"

        // --- Footer (Botones de navegación) ---
        // Aquí usas la lógica del footer que ya tenías, que está bien para esta pantalla
        val footerView = findViewById<View>(R.id.footer_layout)
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnContinue = footerView.findViewById<ImageButton>(R.id.buttonContinue)

        // Configurar botón ATRÁS (visible) -> Vuelve al menú principal
        btnBack.visibility = View.VISIBLE
        btnBack.setOnClickListener {
            finish()
        }

        // Configurar botón SIGUIENTE (invisible)
        btnContinue.visibility = View.INVISIBLE

        // --- LÓGICA DE BOTONES DE OPCIONES (Navegación) ---

        // 1. Astra -> AstraActivity
        findViewById<Button>(R.id.btnAstra).setOnClickListener {
            val intent = Intent(this, AstraActivity::class.java)
            startActivity(intent)
        }

        // 2. Gernikako Arbola -> ArbolaActivity
        findViewById<Button>(R.id.btnArbola).setOnClickListener {
            val intent = Intent(this, ArbolaActivity::class.java)
            startActivity(intent)
        }

        // 3. Pasileku -> PasilekuActivity
        findViewById<Button>(R.id.btnPasileku).setOnClickListener {
            val intent = Intent(this, PasilekuActivity::class.java)
            startActivity(intent)
        }

        // 4. Andra Mari -> AndraMariActivity
        findViewById<Button>(R.id.btnAndraMari).setOnClickListener {
            val intent = Intent(this, AndraMariActivity::class.java)
            startActivity(intent)
        }

        // 5. GERNIKAKO MURALA -> GernikakoMuralaOngiEtorriActivity
        findViewById<Button>(R.id.btnMurala).setOnClickListener {
            val intent = Intent(this, GernikakoMuralaOngiEtorriActivity::class.java)
            startActivity(intent)
        }
    }
}