package com.unairubenigor.gernikapp.Options

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unairubenigor.gernikapp.Programas_Ranking.RankingAdapter
import com.unairubenigor.gernikapp.Programas_Ranking.RankingUsuario
import com.unairubenigor.gernikapp.R

class OptionRankingActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var codigoClase: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option_ranking)

        // 1. RECUPERAR DATOS
        val nombreUsuario = intent.getStringExtra("USER_NAME") ?: "Ezezaguna"
        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        // --- HEADER ---
        val headerView = findViewById<View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "RANKING ONLINE ($codigoClase)" // Mostramos la sala

        // --- FOOTER ---
        val footerView = findViewById<View>(R.id.footer_layout)
        val btnBack = footerView.findViewById<ImageButton>(R.id.buttonBack)
        val btnContinue = footerView.findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnBack.setOnClickListener { finish() }
        btnContinue.visibility = View.INVISIBLE

        // --- CONFIGURAR LISTA ---
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRanking)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (codigoClase.isNotEmpty()) {
            cargarRankingFirebase(recyclerView)
        } else {
            Toast.makeText(this, "Ez zaude gela batean konektatuta", Toast.LENGTH_LONG).show()
        }
    }

    private fun cargarRankingFirebase(recyclerView: RecyclerView) {
        // Escuchamos en tiempo real la colección de jugadores de ESTA sala
        // Ordenados por 'puntuacion' descendente (el que más tiene, primero)
        db.collection("partidas").document(codigoClase)
            .collection("jugadores")
            .orderBy("puntuacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Errorea: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val listaRanking = ArrayList<RankingUsuario>()
                var posicion = 1

                if (snapshots != null) {
                    for (doc in snapshots) {
                        val nombre = doc.id
                        // Si no tiene puntos todavía, asumimos 0
                        val puntos = doc.getLong("puntuacion")?.toInt() ?: 0

                        listaRanking.add(RankingUsuario(posicion, nombre, puntos))
                        posicion++
                    }
                    // Actualizamos el adaptador con la nueva lista
                    recyclerView.adapter = RankingAdapter(listaRanking)
                }
            }
    }
}