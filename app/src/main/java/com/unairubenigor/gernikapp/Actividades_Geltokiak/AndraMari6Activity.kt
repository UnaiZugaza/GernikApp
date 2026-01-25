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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

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

    private val db = FirebaseFirestore.getInstance()

    private fun uploadImagesToFirebase() {
        val urisToUpload = imageUris.values.filterNotNull()
        // Comprobamos que las 3 imágenes estén seleccionadas
        if (urisToUpload.size < 3) {
            Toast.makeText(this, "Hiru irudiak aukeratu behar dituzu", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        val base64List = mutableListOf<String>()

        try {
            // Procesamos cada imagen como hacías en GernikakoArbola
            urisToUpload.forEach { uri ->
                val base64 = procesarImagenABase64(uri)
                if (base64 != null) base64List.add(base64)
            }

            if (base64List.size == 3) {
                guardarEnFirestore(base64List)
            } else {
                setLoading(false)
                Toast.makeText(this, "Errorea irudiak prozesatzean", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            setLoading(false)
            Toast.makeText(this, "Errorea: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun procesarImagenABase64(uri: Uri): String? {
        val imageStream = contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(imageStream)

        // Redimensionamos a 400px para que las 3 fotos quepan en un solo documento de Firestore
        val bitmapReducido = redimensionarBitmap(originalBitmap, 400)

        val streamSalida = ByteArrayOutputStream()
        // Comprimimos al 40% para no superar el límite de 1MB de Firestore
        bitmapReducido.compress(Bitmap.CompressFormat.JPEG, 40, streamSalida)
        val byteArray = streamSalida.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun redimensionarBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val ratio = width.toFloat() / height.toFloat()
        if (ratio > 1) {
            width = maxSize
            height = (width / ratio).toInt()
        } else {
            height = maxSize
            width = (height * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun guardarEnFirestore(imagenes: List<String>) {
        val datos = hashMapOf(
            "izena" to (nombreUsuario ?: "Ezezaguna"),
            "gela" to (codigoClase ?: ""),
            "argazkiak" to imagenes,
            "data" to System.currentTimeMillis()
        )

        // Guardamos en una colección nueva para Andra Mari
        db.collection("andramari_argazkiak")
            .add(datos)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(this, "Irudiak ondo igo dira!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                setLoading(false)
                Toast.makeText(this, "Errorea: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun setLoading(isLoading: Boolean) {
        uploadButton.isEnabled = !isLoading
        uploadButton.text = if (isLoading) "Igotzen..." else "ARGAZKIAK IGO"
    }
}