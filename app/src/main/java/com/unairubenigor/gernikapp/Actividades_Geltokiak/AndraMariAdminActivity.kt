package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class AndraMariAdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No creamos XML para ahorrar tiempo, mostramos un texto por código
        val texto = TextView(this)
        texto.text = "PANEL ADMIN ANDRA MARI (EN CONSTRUCCIÓN)"
        texto.textSize = 24f
        texto.gravity = android.view.Gravity.CENTER
        setContentView(texto)
    }
}