package com.unairubenigor.gernikapp.irakasle

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.AndraMariAdminActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.ArbolaAdminActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.AstraAdminActivity
import com.unairubenigor.gernikapp.R

class IrakasleMenuActivity : AppCompatActivity() {

    // Variable para guardar el código de la clase
    private var codigoClase: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_irakasle_menu)

        // 1. Recuperamos el código de la clase desde la pantalla anterior
        codigoClase = intent.getStringExtra("CLASS_CODE")

        // Comprobamos si el código ha llegado bien
        if (codigoClase.isNullOrEmpty()) {
            Toast.makeText(this, "Errorea: Ezin izan da gelaren kodea lortu", Toast.LENGTH_LONG).show()
            finish() // Cerramos si no hay código
            return
        }

        // 2. Botón Astra
        findViewById<Button>(R.id.btnAdminAstra).setOnClickListener {
            val intent = Intent(this, AstraAdminActivity::class.java)
            intent.putExtra("CLASS_CODE", codigoClase)
            startActivity(intent)
        }

        // 3. Botón Andra Mari
        findViewById<Button>(R.id.btnAdminAndraMari).setOnClickListener {
            val intent = Intent(this, AndraMariAdminActivity::class.java)
            intent.putExtra("CLASS_CODE", codigoClase)
            startActivity(intent)
        }

        // 4. Botón Arbola
        findViewById<Button>(R.id.btnAdminArbola).setOnClickListener {
            val intent = Intent(this, ArbolaAdminActivity::class.java)
            intent.putExtra("CLASS_CODE", codigoClase)
            startActivity(intent)
        }
    }
}