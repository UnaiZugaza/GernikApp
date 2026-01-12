package com.unairubenigor.gernikapp.irakasle

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.AstraAdminActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.AndraMariAdminActivity
import com.unairubenigor.gernikapp.Actividades_Geltokiak.ArbolaAdminActivity
import com.unairubenigor.gernikapp.R

class IrakasleMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_irakasle_menu)

        // 1. Botón Astra (Lleva al panel que ya creamos antes)
        findViewById<Button>(R.id.btnAdminAstra).setOnClickListener {
            startActivity(Intent(this, AstraAdminActivity::class.java))
        }

        // 2. Botón Andra Mari (Lleva a la nueva vacía)
        findViewById<Button>(R.id.btnAdminAndraMari).setOnClickListener {
            startActivity(Intent(this, AndraMariAdminActivity::class.java))
        }

        // 3. Botón Arbola (Lleva a la nueva vacía)
        findViewById<Button>(R.id.btnAdminArbola).setOnClickListener {
            startActivity(Intent(this, ArbolaAdminActivity::class.java))
        }
    }
}