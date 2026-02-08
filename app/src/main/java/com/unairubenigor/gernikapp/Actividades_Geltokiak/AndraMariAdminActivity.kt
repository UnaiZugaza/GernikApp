package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.unairubenigor.gernikapp.R

// --- DATA CLASSES ACTUALIZADAS ---
data class AndraMari4Answer(
    val izena: String = "",
    val gela: String = "",
    val erantzuna1: String = "",
    val erantzuna2: String = "",
    val data: Long = 0L
)

data class AndraMari6Image(
    val izena: String = "",
    val gela: String = "",
    val base64: String = "", // CAMBIADO: Antes era imageUrl
    val data: Long = 0L
)

data class AndraMari7Opinion(
    val izena: String = "",
    val gela: String = "",
    val iritzia: String = "",
    val data: Long = 0L
)

class AndraMariAdminActivity : AppCompatActivity() {

    private lateinit var containerKopla5: LinearLayout
    private lateinit var containerAnswers4: LinearLayout
    private lateinit var rvImages6: RecyclerView
    private lateinit var containerOpinions7: LinearLayout
    private lateinit var progressBar: ProgressBar

    private var codigoClase: String = ""
    private val db = FirebaseFirestore.getInstance()
    private var listeners: MutableList<ListenerRegistration> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andramari_admin)

        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "ADMIN: ANDRA MARI ($codigoClase)"

        containerKopla5 = findViewById(R.id.andramari5_kopla_solutions_container)
        containerAnswers4 = findViewById(R.id.andramari4_answers_container)
        rvImages6 = findViewById(R.id.andramari6_images_container)
        containerOpinions7 = findViewById(R.id.andramari7_opinions_container)
        progressBar = findViewById(R.id.progressBarAdmin)

        setupFooter()

        if (codigoClase.isNotEmpty()) {
            setupRecyclerViews()
            showKoplaSolutions()
            listenForAllDynamicData()
        } else {
            Toast.makeText(this, getString(R.string.errorea_gelaren_kodea_falta_da), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupFooter() {
        findViewById<ImageButton>(R.id.buttonBack).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.buttonBack).visibility = View.VISIBLE
        findViewById<ImageButton>(R.id.buttonContinue).visibility = View.GONE
    }

    private fun setupRecyclerViews() {
        rvImages6.layoutManager = LinearLayoutManager(this)
    }

    private fun showKoplaSolutions() {
        val correctAnswers = listOf("Santu", "Kristoren", "Maria", "Jose", "Jesus", "Maria", "Etxetxu", "Nor", "zeunke")
        containerKopla5.removeAllViews()
        val tv = TextView(this).apply {
            text = correctAnswers.joinToString(separator = ", ")
            textSize = 16f
        }
        containerKopla5.addView(tv)
    }

    private fun listenForAllDynamicData() {
        progressBar.visibility = View.VISIBLE
        listenForAndraMari4Answers()
        listenForAndraMari6Images()
        listenForAndraMari7Opinions()
    }

    private fun listenForAndraMari4Answers() {
        // Quitamos el .orderBy("data") temporalmente para descartar errores de índice en Firebase
        val listener = db.collection("andra_mari_4_erantzunak")
            .whereEqualTo("gela", codigoClase)
            .addSnapshotListener { snapshots, error ->
                progressBar.visibility = View.GONE
                if (error != null) return@addSnapshotListener

                containerAnswers4.removeAllViews()
                snapshots?.forEach { doc ->
                    val answer = doc.toObject(AndraMari4Answer::class.java)
                    val tv = TextView(this).apply {
                        text = "👤 ${answer.izena}:\n   1: ${answer.erantzuna1}\n   2: ${answer.erantzuna2}"
                        setPadding(0, 10, 0, 10)
                        setTextColor(resources.getColor(android.R.color.black))
                    }
                    containerAnswers4.addView(tv)
                }
            }
        listeners.add(listener)
    }

    private fun listenForAndraMari6Images() {
        val listener = db.collection("andra_mari_argazkiak")
            .whereEqualTo("gela", codigoClase)
            .addSnapshotListener { snapshots, error ->
                if (error != null) return@addSnapshotListener
                val imageList = snapshots?.toObjects(AndraMari6Image::class.java) ?: emptyList()
                rvImages6.adapter = AndraMari6ImageAdapter(this, imageList)
            }
        listeners.add(listener)
    }

    private fun listenForAndraMari7Opinions() {
        val listener = db.collection("andra_mari_iritziak")
            .whereEqualTo("gela", codigoClase)
            .addSnapshotListener { snapshots, error ->
                if (error != null) return@addSnapshotListener
                containerOpinions7.removeAllViews()
                snapshots?.forEach { doc ->
                    val opinion = doc.toObject(AndraMari7Opinion::class.java)
                    val tv = TextView(this).apply {
                        text = "👤 ${opinion.izena}:\n   - ${opinion.iritzia}"
                        setPadding(0, 10, 0, 10)
                        setTextColor(resources.getColor(android.R.color.black))
                    }
                    containerOpinions7.addView(tv)
                }
            }
        listeners.add(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        listeners.forEach { it.remove() }
    }
}

// --- ADAPTER CORREGIDO PARA BASE64 (IGUAL QUE ARBOLA) ---
class AndraMari6ImageAdapter(private val context: Context, private val items: List<AndraMari6Image>) :
    RecyclerView.Adapter<AndraMari6ImageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreAlumno)
        val ivImagen: ImageView = view.findViewById(R.id.ivDibujoAlumno)
        val container: View = view // Usamos la vista entera para el clic
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_marrazkia_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvNombre.text = item.izena

        if (item.base64.isNotEmpty()) {
            val decodedString = Base64.decode(item.base64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            holder.ivImagen.setImageBitmap(bitmap)

            // --- CLIC PARA AMPLIAR ---
            holder.container.setOnClickListener {
                mostrarImagenAmpliada(bitmap, item.izena)
            }
        }
    }

    private fun mostrarImagenAmpliada(bitmap: android.graphics.Bitmap, nombre: String) {
        val builder = android.app.AlertDialog.Builder(context)
        val imageView = ImageView(context)
        imageView.setImageBitmap(bitmap)
        imageView.setPadding(20, 20, 20, 20)
        imageView.adjustViewBounds = true

        builder.setTitle("${nombre}-(r)en argazkia")
        builder.setView(imageView)
        builder.setPositiveButton("Itxi") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    override fun getItemCount() = items.size
}