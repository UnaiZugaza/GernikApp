package com.unairubenigor.gernikapp

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OptionRankingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option_ranking)

        // --- 1. HEADER ---
        val headerView = findViewById<View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "RANKING"

        // --- 2. FOOTER (Con tu XML nuevo) ---
        val footerView = findViewById<View>(R.id.footer_layout)
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnContinue = footerView.findViewById<ImageButton>(R.id.buttonContinue)

        // Botón ATRÁS: Visible
        btnBack.visibility = View.VISIBLE
        btnBack.setOnClickListener { finish() }

        // Botón JARRAITU: Invisible
        btnContinue.visibility = View.INVISIBLE

        // --- 3. BASE DE DATOS Y USUARIO ACTUAL ---

        // Recuperamos el nombre del usuario que ha iniciado sesión
        // Si por lo que sea viene vacío, le llamamos "Ezezaguna"
        val nombreUsuarioActual = intent.getStringExtra("USER_NAME") ?: "Ezezaguna"

        val dbHelper = AdminSQL(this)

        // TRUCO: Sumamos 0 puntos.
        // Si el usuario es nuevo, esto lo CREA en la base de datos con 0 puntos.
        // Si ya existía, se queda como está.
        dbHelper.registrarPuntos(nombreUsuarioActual, 0)

        // --- 4. CONFIGURAR LISTA (RECYCLERVIEW) ---
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRanking)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Pedimos la lista real a la base de datos
        val listaRanking = dbHelper.obtenerRanking()

        // Se la pasamos al adaptador
        val adapter = RankingAdapter(listaRanking)
        recyclerView.adapter = adapter
    }
}