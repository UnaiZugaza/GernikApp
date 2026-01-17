package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.unairubenigor.gernikapp.R
import java.util.UUID

class AndraMari6Activity : AppCompatActivity() {

    private lateinit var imageUpload1: ImageView
    private lateinit var imageUpload2: ImageView
    private lateinit var imageUpload3: ImageView
    private lateinit var uploadButton: Button

    private val imageUris = mutableMapOf<Int, Uri?>(
        1 to null, 2 to null, 3 to null
    )

    private var currentImageSelection = 0

    // El nuevo lanzador de actividad para seleccionar imágenes
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUris[currentImageSelection] = it
            when (currentImageSelection) {
                1 -> imageUpload1.setImageURI(it)
                2 -> imageUpload2.setImageURI(it)
                3 -> imageUpload3.setImageURI(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari6)

        // 1. Configuración del Header
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI - 3. GELTOKIA"

        // 2. Configuración de vistas de la actividad
        imageUpload1 = findViewById(R.id.image_upload_1)
        imageUpload2 = findViewById(R.id.image_upload_2)
        imageUpload3 = findViewById(R.id.image_upload_3)
        uploadButton = findViewById(R.id.button_upload_to_firebase)

        imageUpload1.setOnClickListener { selectImage(1) }
        imageUpload2.setOnClickListener { selectImage(2) }
        imageUpload3.setOnClickListener { selectImage(3) }

        uploadButton.setOnClickListener { uploadImagesToFirebase() }

        // 3. Configuración del Footer
        setupFooter()
    }

    private fun setupFooter() {
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE

        btnBack.setOnClickListener {
            finish() // Vuelve a la actividad anterior
        }

        btnNext.setOnClickListener {
            val intent = Intent(this, AndraMari7Activity::class.java)
            startActivity(intent)
        }
    }

    private fun selectImage(imageNumber: Int) {
        currentImageSelection = imageNumber
        // Usamos el nuevo lanzador para abrir la galería
        selectImageLauncher.launch("image/*")
    }

    private fun uploadImagesToFirebase() {
        val urisToUpload = imageUris.values.filterNotNull()
        if (urisToUpload.size < 3) {
            Toast.makeText(this, "Hiru irudiak aukeratu behar dituzu", Toast.LENGTH_SHORT).show()
            return
        }

        uploadButton.isEnabled = false
        val storage = Firebase.storage
        val storageRef = storage.reference
        var uploadedCount = 0

        urisToUpload.forEach { uri ->
            val fileName = "images/${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child(fileName)

            imageRef.putFile(uri)
                .addOnSuccessListener { 
                    uploadedCount++
                    if (uploadedCount == urisToUpload.size) {
                        Toast.makeText(this, "Irudiak ondo igo dira!", Toast.LENGTH_LONG).show()
                        uploadButton.isEnabled = true
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Errorea irudia igotzean: ${e.message}", Toast.LENGTH_SHORT).show()
                    uploadButton.isEnabled = true
                }
        }
    }
}