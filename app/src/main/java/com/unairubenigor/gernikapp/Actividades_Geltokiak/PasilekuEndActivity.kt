package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.R
import com.unairubenigor.gernikapp.ikasle.IkasleMenuActivity

class PasilekuEndActivity : AppCompatActivity() {

    private var nombreUsuario: String? = ""
    private var codigoClase: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasileku_end)

        // Recoger datos
        nombreUsuario = intent.getStringExtra("USER_NAME")
        codigoClase = intent.getStringExtra("CLASS_CODE")

        // Configurar Header
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "PASILEKU PLAZA"

        // Configurar Footer
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.INVISIBLE // No hay siguiente pantalla

        btnBack.setOnClickListener {
            finish()
        }

        // Botón Menura Bueltatu
        val btnVolverMenu = findViewById<Button>(R.id.btn_volver_menu)
        btnVolverMenu.setOnClickListener {
            val intent = Intent(this, IkasleMenuActivity::class.java)
            // Pasamos los datos de vuelta para que el menú sepa quién es el usuario
            intent.putExtra("USER_NAME", nombreUsuario)
            intent.putExtra("CLASS_CODE", codigoClase)
            
            // FLAG_ACTIVITY_CLEAR_TOP cierra todas las actividades que hay por encima de IkasleMenuActivity
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}