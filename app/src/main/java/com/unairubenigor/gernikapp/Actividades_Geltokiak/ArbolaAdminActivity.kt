package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.unairubenigor.gernikapp.R

class ArbolaAdminActivity : AppCompatActivity() {

    private lateinit var rvLista: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arbola_admin)

        // Configurar Header
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "MARRAZKIAK (ADMIN)"

        rvLista = findViewById(R.id.rvMarrazkiak)
        progressBar = findViewById(R.id.progressBarAdmin)
        rvLista.layoutManager = LinearLayoutManager(this)

        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        btnBack.visibility = View.VISIBLE
        btnBack.setOnClickListener { finish() }

        cargarDibujos()
    }

    private fun cargarDibujos() {
        progressBar.visibility = View.VISIBLE

        // Leemos la colección donde guardamos las fotos
        db.collection("arbola_marrazkiak")
            .orderBy("data", com.google.firebase.firestore.Query.Direction.DESCENDING) // Los más nuevos primero
            .get()
            .addOnSuccessListener { result ->
                progressBar.visibility = View.GONE

                val listaDibujos = mutableListOf<Marrazkia>()
                for (document in result) {
                    // Convertimos el documento a nuestro objeto Marrazkia
                    val dibujo = document.toObject(Marrazkia::class.java)
                    listaDibujos.add(dibujo)
                }

                if (listaDibujos.isEmpty()) {
                    Toast.makeText(this, "Ez dago marrazkirik oraindik", Toast.LENGTH_SHORT).show()
                } else {
                    rvLista.adapter = MarrazkiakAdapter(listaDibujos)
                }
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Errorea kargatzean", Toast.LENGTH_SHORT).show()
            }
    }
}