package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.unairubenigor.gernikapp.R
import java.util.UUID

class AndraMari7Activity : AppCompatActivity() {

    private lateinit var videoPreview: VideoView
    private lateinit var selectVideoButton: Button
    private lateinit var uploadVideoButton: Button

    private var videoUri: Uri? = null

    // El nuevo lanzador de actividad para seleccionar un vídeo
    private val selectVideoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            videoUri = it
            videoPreview.setVideoURI(it)
            videoPreview.visibility = View.VISIBLE

            val mediaController = MediaController(this)
            videoPreview.setMediaController(mediaController)
            mediaController.setAnchorView(videoPreview)

            videoPreview.start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari7)

        // 1. Configuración del Header
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI - 4. GELTOKIA"

        // 2. Configuración de vistas
        videoPreview = findViewById(R.id.video_preview)
        selectVideoButton = findViewById(R.id.button_select_video)
        uploadVideoButton = findViewById(R.id.button_upload_video)

        selectVideoButton.setOnClickListener {
            selectVideoLauncher.launch("video/*")
        }

        uploadVideoButton.setOnClickListener {
            uploadVideoToFirebase()
        }

        // 3. Configuración del Footer
        setupFooter()
    }

    private fun setupFooter() {
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)

        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE

        btnBack.setOnClickListener {
            finish() // Vuelve a AndraMari6Activity
        }

        // El botón de continuar ahora solo da una pista al usuario
        btnNext.setOnClickListener {
            Toast.makeText(this, "Lehenengo bideoa igo behar duzu jarraitzeko", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadVideoToFirebase() {
        if (videoUri == null) {
            Toast.makeText(this, "Lehenengo bideo bat aukeratu behar duzu", Toast.LENGTH_SHORT).show()
            return
        }

        uploadVideoButton.isEnabled = false
        selectVideoButton.isEnabled = false
        uploadVideoButton.text = "IGOTZEN..." // Feedback para el usuario

        val storage = Firebase.storage
        // --- RUTA CORREGIDA ---
        // Ahora guarda el video en la carpeta que el admin puede ver.
        val fileName = "videos/andra_mari/${UUID.randomUUID()}.mp4"
        val videoRef = storage.reference.child(fileName)

        videoRef.putFile(videoUri!!)
            .addOnSuccessListener {
                Toast.makeText(this, "Bideoa ondo igo da!", Toast.LENGTH_LONG).show()

                // --- NAVEGACIÓN CORREGIDA ---
                // Una vez subido el video con éxito, navega a la pantalla final.
                val intent = Intent(this, AndraMari8Activity::class.java)
                startActivity(intent)

                // Reseteamos los botones por si el usuario vuelve atrás
                uploadVideoButton.isEnabled = true
                selectVideoButton.isEnabled = true
                uploadVideoButton.text = "BIDEOA IGO"
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Errorea bideoa igotzean: ${e.message}", Toast.LENGTH_SHORT).show()
                uploadVideoButton.isEnabled = true
                selectVideoButton.isEnabled = true
                uploadVideoButton.text = "BIDEOA IGO"
            }
            .addOnProgressListener { snapshot ->
                val progress = (100.0 * snapshot.bytesTransferred) / snapshot.totalByteCount
                // Actualizamos el texto del botón para mostrar el progreso
                uploadVideoButton.text = "IGOTZEN: ${progress.toInt()}%"
            }
    }
}
