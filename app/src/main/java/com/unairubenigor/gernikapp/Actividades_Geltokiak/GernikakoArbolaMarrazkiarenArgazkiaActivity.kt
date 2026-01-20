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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.FirebaseFirestore
import com.unairubenigor.gernikapp.R
import com.unairubenigor.gernikapp.ikasle.IkasleMenuActivity
import java.io.ByteArrayOutputStream
import java.io.InputStream

class GernikakoArbolaMarrazkiarenArgazkiaActivity : AppCompatActivity() {

    private var imagenUri: Uri? = null
    private lateinit var ivImagen: ImageView
    private lateinit var cardSeleccionar: CardView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnBidali: Button

    private var nombreUsuario: String = "Ezezaguna"
    private var codigoClase: String = ""
    private val db = FirebaseFirestore.getInstance()

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imagenUri = uri
            ivImagen.setImageURI(uri)
            ivImagen.setPadding(0, 0, 0, 0)
            ivImagen.scaleType = ImageView.ScaleType.CENTER_CROP
            ivImagen.imageTintList = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arbola_igo)

        nombreUsuario = intent.getStringExtra("USER_NAME") ?: "Ezezaguna"
        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "GERNIKAKO ARBOLA"

        ivImagen = findViewById(R.id.ivImagenSeleccionada)
        cardSeleccionar = findViewById(R.id.cardSeleccionarFoto)
        btnBidali = findViewById(R.id.btnBidali)
        progressBar = findViewById(R.id.progressBar)

        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        // --- CONFIGURACIÓN DE BOTONES ---
        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.GONE // <--- OCULTO (Antes era VISIBLE)

        cardSeleccionar.setOnClickListener { getContent.launch("image/*") }

        btnBidali.setOnClickListener {
            if (imagenUri != null) {
                if (codigoClase.isEmpty()) {
                    Toast.makeText(this, "Errorea: Gela kodea falta da", Toast.LENGTH_SHORT).show()
                }
                procesarYSubirImagen()
            } else {
                Toast.makeText(this, "Mesedez, aukeratu irudi bat lehenengo", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener { finish() }

        // btnNext ya no hace nada porque está oculto
    }

    private fun procesarYSubirImagen() {
        mostrarCarga(true)

        try {
            val imageStream: InputStream? = contentResolver.openInputStream(imagenUri!!)
            val originalBitmap = BitmapFactory.decodeStream(imageStream)

            // Redimensionar a 600px (puedes subir a 800 si quieres más calidad)
            val bitmapReducido = redimensionarBitmap(originalBitmap, 600)

            val streamSalida = ByteArrayOutputStream()
            // Calidad 50%
            bitmapReducido.compress(Bitmap.CompressFormat.JPEG, 50, streamSalida)
            val byteArray = streamSalida.toByteArray()
            val imagenBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)

            if (byteArray.size > 1000000) {
                mostrarCarga(false)
                Toast.makeText(this, "Irudia handiegia da. Saiatu beste batekin.", Toast.LENGTH_LONG).show()
                return
            }

            guardarEnBaseDeDatos(imagenBase64)

        } catch (e: Exception) {
            mostrarCarga(false)
            Toast.makeText(this, "Errorea: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun guardarEnBaseDeDatos(base64String: String) {
        val datosDibujo = hashMapOf(
            "izena" to nombreUsuario,
            "gela" to codigoClase,
            "base64" to base64String,
            "data" to System.currentTimeMillis()
        )

        db.collection("arbola_marrazkiak")
            .add(datosDibujo)
            .addOnSuccessListener {
                mostrarCarga(false)
                Toast.makeText(this, "Marrazkia ondo igo da!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, IkasleMenuActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                mostrarCarga(false)
                Toast.makeText(this, "Errorea datuetan: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarCarga(cargando: Boolean) {
        if (cargando) {
            progressBar.visibility = View.VISIBLE
            btnBidali.isEnabled = false
            btnBidali.text = "IGOTZEN..."
        } else {
            progressBar.visibility = View.GONE
            btnBidali.isEnabled = true
            btnBidali.text = "BIDALI"
        }
    }
}