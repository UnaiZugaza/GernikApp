package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.unairubenigor.gernikapp.R

// --- CÓDIGO CORREGIDO Y LIMPIADO ---
class AndraMariAdminActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    // Contenedores de los paneles
    private lateinit var koplaSolutionsContainer: LinearLayout
    private lateinit var imagesContainer: LinearLayout
    private lateinit var videosContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari_admin)

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Inicializar contenedores del layout
        koplaSolutionsContainer = findViewById(R.id.kopla_solutions_container)
        imagesContainer = findViewById(R.id.images_container)
        videosContainer = findViewById(R.id.videos_container)

        // Configurar Header
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ADMIN: ANDRA MARI"

        // Cargar los datos
        loadKoplaSolutions()
        loadMedia("images/andra_mari/", imagesContainer) // Ruta en Storage para imágenes
        loadMedia("videos/andra_mari/", videosContainer) // Ruta en Storage para videos
    }

    private fun loadKoplaSolutions() {
        // Muestra la solución correcta de la kopla como un texto fijo.
        val fullKopla = """
            Solista: "Santu honetan..."
            Solista: "Kristoren jaiotzea"
            Koro: "Kontentuaren amiz, guztioz pozkaitian, Maria, Jose, Jesus, Maria"
            Solista: "Nor eta nor bizi da?"
            Solista: "Etxetxu zahar horretan"
            Koro: "Nor eta zer gura zeunke..."
        """.trimIndent()

        val koplaText = TextView(this).apply {
            text = fullKopla
            textSize = 15f
            setBackgroundColor(Color.parseColor("#E0E0E0")) // Un color gris claro
            setPadding(24, 24, 24, 24)

        }
        koplaSolutionsContainer.addView(koplaText)
    }

    private fun loadMedia(path: String, container: LinearLayout) {
        val listRef = storage.reference.child(path)

        listRef.listAll()
            .addOnSuccessListener { listResult ->
                if (listResult.items.isEmpty()) {
                    val info = if (path.contains("images")) "irudirik" else "bideorik"
                    addInfoText(container, "Oraindik ez dago $info.")
                }
                listResult.items.forEach { item ->
                    item.downloadUrl.addOnSuccessListener { uri ->
                        if (path.contains("images")) {
                            addImageToContainer(uri, container)
                        } else {
                            addVideoToContainer(uri, container)
                        }
                    }
                }
            }
            .addOnFailureListener {
                val errorInfo = if (path.contains("images")) "irudiak" else "bideoak"
                addInfoText(container, "Errorea $errorInfo kargatzean.")
            }
    }

    private fun addImageToContainer(uri: Uri, container: LinearLayout) {
        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                600 // Altura fija para la imagen
            ).also { it.setMargins(0, 8, 0, 16) }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        Glide.with(this).load(uri).into(imageView)
        container.addView(imageView)
    }

    private fun addVideoToContainer(uri: Uri, container: LinearLayout) {
        val videoButton = Button(this).apply {
            text = "Ikusi Bideoa"
            setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
        container.addView(videoButton)
    }

    private fun addInfoText(container: LinearLayout, message: String) {
        val infoText = TextView(this).apply {
            text = message
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 24)
        }
        container.addView(infoText)
    }
}
