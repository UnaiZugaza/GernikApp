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
    private var nombreUsuario: String? = null
    private var codigoClase: String? = null

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

        nombreUsuario = intent.getStringExtra("USER_NAME")
        codigoClase = intent.getStringExtra("CLASS_CODE")

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI - 3. GELTOKIA"

        imageUpload1 = findViewById(R.id.image_upload_1)
        imageUpload2 = findViewById(R.id.image_upload_2)
        imageUpload3 = findViewById(R.id.image_upload_3)
        uploadButton = findViewById(R.id.button_upload_to_firebase)

        imageUpload1.setOnClickListener { selectImage(1) }
        imageUpload2.setOnClickListener { selectImage(2) }
        imageUpload3.setOnClickListener { selectImage(3) }

        uploadButton.setOnClickListener { uploadImagesToFirebase() }

        setupFooter()
    }

    private fun setupFooter() {
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE

        btnBack.setOnClickListener { finish() }

        btnNext.setOnClickListener {
            val intent = Intent(this, AndraMari7Activity::class.java).apply {
                putExtra("USER_NAME", nombreUsuario)
                putExtra("CLASS_CODE", codigoClase)
            }
            startActivity(intent)
        }
    }

    private fun selectImage(imageNumber: Int) {
        currentImageSelection = imageNumber
        selectImageLauncher.launch("image/*")
    }

    private fun uploadImagesToFirebase() {
        val urisToUpload = imageUris.values.filterNotNull()
        if (urisToUpload.size < 3) {
            Toast.makeText(this, "Hiru irudiak aukeratu behar dituzu", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        val storageRef = Firebase.storage.reference
        var uploadedCount = 0
        val totalImages = urisToUpload.size

        urisToUpload.forEach { uri ->
            // --- PRUEBA DE FUEGO: Usamos la ruta que sabemos que funciona ---
            val imagePath = "images/arbol_marrazkia/${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child(imagePath)

            imageRef.putFile(uri)
                .addOnSuccessListener {
                    uploadedCount++
                    if (uploadedCount == totalImages) {
                        Toast.makeText(this, "Irudiak ondo igo dira!", Toast.LENGTH_LONG).show()
                        setLoading(false)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Errorea irudia igotzean: ${e.message}", Toast.LENGTH_SHORT).show()
                    setLoading(false)
                }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        uploadButton.isEnabled = !isLoading
        uploadButton.text = if (isLoading) "Igotzen..." else "ARGAZKIAK IGO"
    }
}