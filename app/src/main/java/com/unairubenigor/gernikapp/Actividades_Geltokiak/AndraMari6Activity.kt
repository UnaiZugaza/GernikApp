package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.unairubenigor.gernikapp.R
import java.io.ByteArrayOutputStream
import java.io.InputStream

class AndraMari6Activity : AppCompatActivity() {

    private lateinit var imageUpload1: ImageView
    private lateinit var imageUpload2: ImageView
    private lateinit var imageUpload3: ImageView
    private lateinit var uploadButton: Button

    // Guardamos las URIs locales para procesarlas luego
    private val imageUris = mutableMapOf<Int, Uri?>(
        1 to null, 2 to null, 3 to null
    )

    private var currentImageSelection = 0
    private var nombreUsuario: String = ""
    private var codigoClase: String = ""

    private val db = FirebaseFirestore.getInstance()

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

        nombreUsuario = intent.getStringExtra("USER_NAME") ?: ""
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

        uploadButton.setOnClickListener { uploadImages() }

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

    // --- LÓGICA COPIADA DE ARBOLA (BASE64) ---

    private fun uploadImages() {
        val urisToUpload = imageUris.values.filterNotNull()
        if (urisToUpload.size < 3) {
            Toast.makeText(this, "Hiru irudiak aukeratu behar dituzu", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        try {
            // Procesamos las 3 imágenes y las subimos
            var subidasExitosas = 0

            imageUris.forEach { (_, uri) ->
                if (uri != null) {
                    val base64String = procesarImagenABase64(uri)

                    if (base64String != null) {
                        guardarEnFirestore(base64String) {
                            subidasExitosas++
                            if (subidasExitosas == 3) {
                                Toast.makeText(this, "Irudiak ondo igo dira!", Toast.LENGTH_LONG).show()
                                setLoading(false)
                            }
                        }
                    } else {
                        setLoading(false)
                        Toast.makeText(this, "Errorea irudia prozesatzean", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            setLoading(false)
            Toast.makeText(this, "Errorea: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun procesarImagenABase64(uri: Uri): String? {
        return try {
            val imageStream: InputStream? = contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(imageStream)
            // Redimensionamos a 600 como en Arbola para que no pese demasiado
            val bitmapReducido = redimensionarBitmap(originalBitmap, 600)
            val streamSalida = ByteArrayOutputStream()
            // Comprimimos al 50% como en Arbola
            bitmapReducido.compress(Bitmap.CompressFormat.JPEG, 50, streamSalida)
            val byteArray = streamSalida.toByteArray()
            Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }

    private fun guardarEnFirestore(base64String: String, onComplete: () -> Unit) {
        val datosDibujo = hashMapOf(
            "izena" to nombreUsuario,
            "gela" to codigoClase,
            "base64" to base64String, // Ahora usamos base64 en lugar de imageUrl
            "data" to System.currentTimeMillis(),
            "puntuazioa" to 0
        )

        db.collection("andra_mari_argazkiak")
            .add(datosDibujo)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Errorea gordetzean: ${e.message}", Toast.LENGTH_SHORT).show()
                setLoading(false)
            }
    }

    private fun redimensionarBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun setLoading(isLoading: Boolean) {
        uploadButton.isEnabled = !isLoading
        uploadButton.text = if (isLoading) "Igotzen..." else "ARGAZKIAK IGO"
    }
}