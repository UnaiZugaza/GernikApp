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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.unairubenigor.gernikapp.R
import java.util.UUID

class AndraMari6Activity : AppCompatActivity() {

    private lateinit var imageUpload1: ImageView
    private lateinit var imageUpload2: ImageView
    private lateinit var imageUpload3: ImageView
    private lateinit var uploadButton: Button

    private var nombreUsuario: String = ""
    private var codigoClase: String = ""
    private val db = FirebaseFirestore.getInstance()

    private val imageUris = mutableMapOf<Int, Uri?>(
        1 to null, 2 to null, 3 to null
    )

    private var currentImageSelection = 0

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

        // --- CORRECCIÓN CLAVE 1: Recoger SIEMPRE los datos ---
        nombreUsuario = intent.getStringExtra("USER_NAME") ?: "Ezezaguna"
        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI - 3. GELTOKIA"

        imageUpload1 = findViewById(R.id.image_upload_1)
        imageUpload2 = findViewById(R.id.image_upload_2)
        imageUpload3 = findViewById(R.id.image_upload_3)
        uploadButton = findViewById(R.id.button_upload_to_firebase)

        imageUpload1.setOnClickListener { selectImage(1) }
        imageUpload2.setOnClickListener { selectImage(2) }
        imageUpload3.setOnClickListener { selectImage(3) }

        uploadButton.setOnClickListener { 
            if (codigoClase.isEmpty()){
                Toast.makeText(this, "Errorea: Gelaren kodea falta da.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            uploadImagesToFirebase() 
        }

        setupFooter()
    }

    private fun setupFooter() {
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE

        btnBack.setOnClickListener { finish() }

        // --- CORRECCIÓN CLAVE 2: Pasar la "mochila" a la siguiente actividad ---
        btnNext.setOnClickListener {
            val intent = Intent(this, AndraMari7Activity::class.java)
            intent.putExtra("USER_NAME", nombreUsuario)
            intent.putExtra("CLASS_CODE", codigoClase)
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

        uploadButton.isEnabled = false
        uploadButton.text = "IGOTZEN..."

        val storage = Firebase.storage
        var successCount = 0
        var completedCount = 0

        urisToUpload.forEach { uri ->
            val fileName = "images/${UUID.randomUUID()}.jpg"
            val imageRef = storage.reference.child(fileName)

            imageRef.putFile(uri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) task.exception?.let { throw it }
                    imageRef.downloadUrl
                }
                .addOnCompleteListener { task ->
                    completedCount++
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        saveImageUrlToFirestore(downloadUri.toString()) { success ->
                            if (success) successCount++
                        }
                    }
                    if (completedCount == urisToUpload.size) {
                        Toast.makeText(this, "$successCount/${urisToUpload.size} argazki ondo gorde dira.", Toast.LENGTH_LONG).show()
                        uploadButton.text = "BIDALITA"
                    }
                }
        }
    }

    private fun saveImageUrlToFirestore(imageUrl: String, onResult: (Boolean) -> Unit) {
        val imageData = hashMapOf(
            "izena" to nombreUsuario,
            "gela" to codigoClase,
            "imageUrl" to imageUrl,
            "data" to System.currentTimeMillis()
        )
        db.collection("andra_mari_argazkiak").add(imageData)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}