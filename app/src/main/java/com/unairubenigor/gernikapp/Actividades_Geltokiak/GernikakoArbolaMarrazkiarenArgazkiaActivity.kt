package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
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
import com.google.firebase.firestore.ListenerRegistration
import com.unairubenigor.gernikapp.R
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

    // Listener para esperar los resultados del profesor
    private var listenerResultados: ListenerRegistration? = null

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

        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.GONE

        cardSeleccionar.setOnClickListener { getContent.launch("image/*") }

        btnBidali.setOnClickListener {
            if (imagenUri != null) {
                if (codigoClase.isEmpty()) {
                    Toast.makeText(this, getString(R.string.errorea_gelaren_kodea_falta_da), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                procesarYSubirImagen()
            } else {
                Toast.makeText(this,
                    getString(R.string.mesedez_aukeratu_irudi_bat_lehenengo), Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener { finish() }
    }

    private fun procesarYSubirImagen() {
        mostrarCarga(true)
        try {
            val imageStream: InputStream? = contentResolver.openInputStream(imagenUri!!)
            val originalBitmap = BitmapFactory.decodeStream(imageStream)
            val bitmapReducido = redimensionarBitmap(originalBitmap, 600)
            val streamSalida = ByteArrayOutputStream()
            bitmapReducido.compress(Bitmap.CompressFormat.JPEG, 50, streamSalida)
            val byteArray = streamSalida.toByteArray()
            val imagenBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)

            if (byteArray.size > 1000000) { // Límite de 1MB para Firestore
                mostrarCarga(false)
                Toast.makeText(this,
                    getString(R.string.irudia_handiegia_da_saiatu_beste_batekin), Toast.LENGTH_LONG).show()
                return
            }
            guardarEnBaseDeDatos(imagenBase64)
        } catch (e: Exception) {
            mostrarCarga(false)
            Toast.makeText(this, getString(R.string.errorea_irudia_prozesatzean), Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarEnBaseDeDatos(base64String: String) {
        val datosDibujo = hashMapOf(
            "izena" to nombreUsuario,
            "gela" to codigoClase,
            "base64" to base64String,
            "data" to System.currentTimeMillis(),
            "puntuazioa" to 0 // Inicializamos la puntuación a 0
        )

        db.collection("arbola_marrazkiak").add(datosDibujo)
            .addOnSuccessListener {
                // --- CAMBIO DE LÓGICA ---
                progressBar.visibility = View.GONE
                btnBidali.text = getString(R.string.bidalita_emaitzen_zain)
                cardSeleccionar.isEnabled = false
                btnBidali.isEnabled = false

                Toast.makeText(this,
                    getString(R.string.marrazkia_ondo_igo_da_emaitzen_zain), Toast.LENGTH_LONG).show()
                escucharResultados()
            }
            .addOnFailureListener {
                mostrarCarga(false)
                Toast.makeText(this,
                    getString(R.string.errorea_datu_basean_gordetzean, it.message), Toast.LENGTH_SHORT).show()
            }
    }

    private fun escucharResultados() {
        if (codigoClase.isEmpty()) return

        val gelaRef = db.collection("gelak").document(codigoClase)
        listenerResultados = gelaRef.addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener

            if (snapshot != null && snapshot.exists()) {
                if (snapshot.getBoolean("emaitzak_prest") == true) {
                    listenerResultados?.remove() // Dejamos de escuchar
                    gelaRef.update("emaitzak_prest", false) // Reseteamos la bandera

                    try {
                        val mediaPlayer = MediaPlayer.create(this, R.raw.jardueraburututa)
                        mediaPlayer?.setOnCompletionListener { it.release() }
                        mediaPlayer?.start()
                    } catch (ex: Exception) { /* Ignorar error de audio */ }

                    val intent = Intent(this, ArbolaIrabazleActivity::class.java)
                    intent.putExtra("GELA_CODE", codigoClase)
                    intent.putExtra("USER_NAME", nombreUsuario)
                    startActivity(intent)
                    finish()
                }
            }
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

    private fun mostrarCarga(cargando: Boolean) {
        if (cargando) {
            progressBar.visibility = View.VISIBLE
            btnBidali.isEnabled = false
            btnBidali.text = getString(R.string.igotzen)
        } else {
            progressBar.visibility = View.GONE
            btnBidali.isEnabled = true
            btnBidali.text = getString(R.string.bidali)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // MUY IMPORTANTE: Nos aseguramos de quitar el listener si la actividad se destruye
        listenerResultados?.remove()
    }
}