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

        btnNext.setOnClickListener {
            // TODO: ¿Qué hacer al finalizar? ¿Ir al menú principal? 
            Toast.makeText(this, "Zorionak! Ibilbidea amaitu duzu!", Toast.LENGTH_LONG).show()
            // Ejemplo: Volver al menú de Geltokiak
            // val intent = Intent(this, GeltokiakActivity::class.java)
            // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            // startActivity(intent)
        }
    }

    private fun uploadVideoToFirebase() {
        if (videoUri == null) {
            Toast.makeText(this, "Lehenengo bideo bat aukeratu behar duzu", Toast.LENGTH_SHORT).show()
            return
        }

        uploadVideoButton.isEnabled = false
        selectVideoButton.isEnabled = false

        val storage = Firebase.storage
        val fileName = "videos/${UUID.randomUUID()}.mp4"
        val videoRef = storage.reference.child(fileName)

        videoRef.putFile(videoUri!!)
            .addOnSuccessListener { 
                Toast.makeText(this, "Bideoa ondo igo da!", Toast.LENGTH_LONG).show()
                uploadVideoButton.isEnabled = true
                selectVideoButton.isEnabled = true
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Errorea bideoa igotzean: ${e.message}", Toast.LENGTH_SHORT).show()
                uploadVideoButton.isEnabled = true
                selectVideoButton.isEnabled = true
            }
            .addOnProgressListener { snapshot ->
                val progress = (100.0 * snapshot.bytesTransferred) / snapshot.totalByteCount
                // Aquí podrías mostrar el progreso en un TextView o ProgressBar
                // Ejemplo: progressText.text = "Igotzen: ${progress.toInt()}%"
            }
    }
}
