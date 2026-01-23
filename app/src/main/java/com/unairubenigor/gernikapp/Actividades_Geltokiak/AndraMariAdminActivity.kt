package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.unairubenigor.gernikapp.R

class AndraMariAdminActivity : AppCompatActivity() {

    private lateinit var rvImages: RecyclerView
    private lateinit var rvVideos: RecyclerView
    private lateinit var progressBar: ProgressBar

    private var codigoClase: String = ""
    private val db = FirebaseFirestore.getInstance()
    private var imagesListener: ListenerRegistration? = null
    private var videosListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari_admin)

        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ADMIN: ANDRA MARI ($codigoClase)"

        rvImages = findViewById(R.id.images_container) // Asumiendo que ahora son RecyclerViews
        rvVideos = findViewById(R.id.videos_container)
        progressBar = findViewById(R.id.progressBarAdmin) // Asumiendo que tienes un ProgressBar

        rvImages.layoutManager = LinearLayoutManager(this)
        rvVideos.layoutManager = LinearLayoutManager(this)

        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        btnBack.visibility = View.VISIBLE
        btnBack.setOnClickListener { finish() }

        if (codigoClase.isNotEmpty()) {
            loadKoplaSolutions()
            listenForImages()
            listenForVideos()
        } else {
            Toast.makeText(this, "Errorea: Kodea falta da", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadKoplaSolutions() {
        // ... (código de la kopla se mantiene igual) ...
    }

    private fun listenForImages() {
        progressBar.visibility = View.VISIBLE
        imagesListener = db.collection("andra_mari_argazkiak") // Suposición del nombre de la colección
            .whereEqualTo("gela", codigoClase)
            .orderBy("data", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                progressBar.visibility = View.GONE
                if (error != null) {
                    Toast.makeText(this, "Errorea irudiak kargatzean: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    // Lógica para procesar y mostrar las imágenes en rvImages
                    // ... (necesitaríamos un Adapter)
                }
            }
    }

    private fun listenForVideos() {
        videosListener = db.collection("andra_mari_bideoak")
            .whereEqualTo("gela", codigoClase)
            .orderBy("data", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Errorea bideoak kargatzean: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    // Lógica para procesar y mostrar los vídeos en rvVideos
                    // ... (necesitaríamos un Adapter)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        imagesListener?.remove()
        videosListener?.remove()
    }
}