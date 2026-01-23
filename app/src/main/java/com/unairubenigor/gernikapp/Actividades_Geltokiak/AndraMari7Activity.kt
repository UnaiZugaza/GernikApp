package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.unairubenigor.gernikapp.R
import java.io.InputStream

class AndraMari7Activity : AppCompatActivity() {

    private lateinit var videoPreview: VideoView
    private lateinit var selectVideoButton: Button
    private lateinit var uploadVideoButton: Button
    private var videoUri: Uri? = null

    private var nombreUsuario: String = "Ezezaguna"
    private var codigoClase: String = ""
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val selectVideoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                videoUri = uri
                videoPreview.setVideoURI(uri)
                videoPreview.visibility = View.VISIBLE
                videoPreview.start()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari7)

        nombreUsuario = intent.getStringExtra("USER_NAME") ?: "Ezezaguna"
        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ANDRA MARI - 4. GELTOKIA"

        videoPreview = findViewById(R.id.video_preview)
        selectVideoButton = findViewById(R.id.button_select_video)
        uploadVideoButton = findViewById(R.id.button_upload_video)

        selectVideoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "video/*"
            selectVideoLauncher.launch(intent)
        }

        uploadVideoButton.setOnClickListener {
            if (videoUri != null) {
                if (codigoClase.isEmpty()) {
                    Toast.makeText(this, "Errorea: Gela kodea falta da", Toast.LENGTH_SHORT).show()
                } else {
                    uploadVideoAsBase64()
                }
            } else {
                Toast.makeText(this, "Lehenengo bideo bat aukeratu behar duzu", Toast.LENGTH_SHORT).show()
            }
        }

        setupFooter()
    }

    private fun setupFooter() {
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        val btnNext = findViewById<ImageButton>(R.id.buttonContinue)
        btnBack.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE
        btnNext.setOnClickListener {
            val intent = Intent(this, AndraMari8Activity::class.java)
            startActivity(intent)
        }
        btnBack.setOnClickListener { finish() }
    }

    private fun uploadVideoAsBase64() {
        setLoading(true)
        try {
            val videoStream: InputStream? = contentResolver.openInputStream(videoUri!!)
            val videoBytes = videoStream?.readBytes()
            videoStream?.close()

            if (videoBytes == null) {
                throw Exception("Ezin izan da bideoa irakurri.")
            }

            if (videoBytes.size > 5 * 1024 * 1024) {
                setLoading(false)
                Toast.makeText(this, "Bideoa handiegia da (max 5MB). Saiatu beste batekin.", Toast.LENGTH_LONG).show()
                return
            }

            val videoBase64 = Base64.encodeToString(videoBytes, Base64.NO_WRAP)
            saveVideoToFirestore(videoBase64)

        } catch (e: Exception) {
            setLoading(false)
            Toast.makeText(this, "Errorea bideoa prozesatzean: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveVideoToFirestore(base64String: String) {
        val videoData = hashMapOf(
            "izena" to nombreUsuario,
            "gela" to codigoClase,
            "base64" to base64String,
            "data" to System.currentTimeMillis()
        )

        db.collection("andra_mari_bideoak")
            .add(videoData)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(this, "Bideoa ondo igo da!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, AndraMari8Activity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Errorea bideoa gordetzean: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            uploadVideoButton.isEnabled = false
            selectVideoButton.isEnabled = false
            uploadVideoButton.text = "IGOTZEN..."
        } else {
            uploadVideoButton.isEnabled = true
            selectVideoButton.isEnabled = true
            uploadVideoButton.text = "BIDEOA IGO"
        }
    }

    private fun loadMedia(path: String, container: LinearLayout) {
        if (path.contains("images")) {
            val listRef = storage.reference.child(path)
            listRef.listAll()
                .addOnSuccessListener { listResult ->
                    if (listResult.items.isEmpty()) { // <-- ¡ERROR CORREGIDO AQUÍ!
                        addInfoText(container, "Oraindik ez dago irudirik.")
                    }
                    listResult.items.forEach { item ->
                        item.downloadUrl.addOnSuccessListener { uri ->
                            addImageToContainer(uri, container)
                        }
                    }
                }
                .addOnFailureListener {
                    addInfoText(container, "Errorea irudiak kargatzean.")
                }
        } else if (path.contains("videos")) {
            db.collection("andra_mari_bideoak")
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        addInfoText(container, "Oraindik ez dago bideorik.")
                    }
                    for (document in documents) {
                        val user = document.getString("izena") ?: "Ezezaguna"
                        val base64Video = document.getString("base64")
                        if (base64Video != null) {
                            addVideoToContainer(user, base64Video, container)
                        }
                    }
                }
                .addOnFailureListener {
                    addInfoText(container, "Errorea bideoak kargatzean.")
                }
        }
    }

    private fun addInfoText(container: LinearLayout, text: String) {
        val textView = TextView(this).apply {
            this.text = text
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.setMargins(0, 16, 0, 16) }
        }
        container.addView(textView)
    }

    private fun addImageToContainer(uri: Uri, container: LinearLayout) {
        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 600
            ).also { it.setMargins(0, 8, 0, 16) }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        Glide.with(this).load(uri).into(imageView)
        container.addView(imageView)
    }

    private fun addVideoToContainer(user: String, base64: String, container: LinearLayout) {
        val videoButton = Button(this).apply {
            text = "Ikusi $user-(r)en Bideoa"
            setOnClickListener {
                try {
                    Toast.makeText(context, "Bideoa deskargatu beharko litzateke.", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Ezin da bideoa erreproduzitu.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        container.addView(videoButton)
    }
}