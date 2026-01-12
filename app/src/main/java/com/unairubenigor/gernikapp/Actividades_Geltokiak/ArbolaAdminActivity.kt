package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class ArbolaAdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val texto = TextView(this)
        texto.text = "PANEL ADMIN ARBOLA (EN CONSTRUCCIÓN)"
        texto.textSize = 24f
        texto.gravity = android.view.Gravity.CENTER
        setContentView(texto)
    }
}