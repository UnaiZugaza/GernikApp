package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.unairubenigor.gernikapp.R

class ArbolaAdminActivity : AppCompatActivity() {

    private lateinit var rvLista: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnBidaliEmaitzak: Button

    private var codigoClase: String = ""
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistro: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arbola_admin)

        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "MARRAZKIAK ($codigoClase)"

        rvLista = findViewById(R.id.rvMarrazkiak)
        progressBar = findViewById(R.id.progressBarAdmin)
        btnBidaliEmaitzak = findViewById(R.id.btnBidaliEmaitzak)
        rvLista.layoutManager = LinearLayoutManager(this)

        // Configuración de Footer
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.GONE

        btnBack.setOnClickListener { finish() }

        btnBidaliEmaitzak.setOnClickListener { bidaliEmaitzakIkasleei() }

        if (codigoClase.isNotEmpty()) {
            escucharDibujosEnTiempoReal()
        } else {
            Toast.makeText(this, "Errorea: Kodea falta da", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bidaliEmaitzakIkasleei() {
        if (codigoClase.isEmpty()) {
            Toast.makeText(this, "Ezin dira emaitzak bidali, gelaren kodea falta da", Toast.LENGTH_SHORT).show()
            return
        }

        val gelaRef = db.collection("gelak").document(codigoClase)
        val data = hashMapOf("emaitzak_prest" to true)

        gelaRef.set(data) // Usamos set para crear o sobrescribir
            .addOnSuccessListener {
                Toast.makeText(this, "Emaitzak ikasleei bidali zaizkie!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Errorea emaitzak bidaltzean: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun escucharDibujosEnTiempoReal() {
        progressBar.visibility = View.VISIBLE

        listenerRegistro = db.collection("arbola_marrazkiak")
            .whereEqualTo("gela", codigoClase)
            .orderBy("data", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->

                progressBar.visibility = View.GONE

                if (error != null) {
                    Toast.makeText(this, "Errorea: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val listaDibujos = mutableListOf<Marrazkia>()

                    for (document in snapshots) {
                        val dibujo = document.toObject(Marrazkia::class.java)
                        dibujo.id = document.id
                        listaDibujos.add(dibujo)
                    }

                    rvLista.adapter = MarrazkiakAdapter(listaDibujos)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistro?.remove()
    }
}