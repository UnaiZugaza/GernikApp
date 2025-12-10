package com.unairubenigor.gernikapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OptionRankingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option_ranking)

        // 1. Poner título al Header
        val headerView = findViewById<android.view.View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "RANKING"

        // 2. Configurar el botón de Volver (Flecha abajo-izquierda)
        val btnAtras = findViewById<ImageView>(R.id.btnFlechaAtras)
        btnAtras.setOnClickListener {
            finish() // Cierra y vuelve al menú anterior
        }

        // 3. Configurar la Lista (RecyclerView)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRanking)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Cargar datos (Aquí luego conectarás tu SQL)
        val listaDatos = obtenerDatos()
        val adapter = RankingAdapter(listaDatos)
        recyclerView.adapter = adapter
    }

    // Datos de prueba para ver el diseño
    private fun obtenerDatos(): List<RankingUsuario> {
        val lista = ArrayList<RankingUsuario>()
        lista.add(RankingUsuario(1, "Unai", 2500))
        lista.add(RankingUsuario(2, "Ruben", 2300))
        lista.add(RankingUsuario(3, "Igor", 2150))
        lista.add(RankingUsuario(4, "Ane", 1900))
        lista.add(RankingUsuario(5, "Mikel", 1800))
        lista.add(RankingUsuario(6, "Maite", 1500))
        return lista
    }
}