package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.content.Context
import android.os.Bundle
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
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.unairubenigor.gernikapp.R

// --- DATA CLASSES ---
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
    val imageUrl: String = "",
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
            progressBar.visibility = View.VISIBLE
            setupRecyclerViews()
            showKoplaSolutions()
            listenForAllDynamicData()
        } else {
            Toast.makeText(this, "Errorea: Gelaren kodea falta da", Toast.LENGTH_SHORT).show()
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
        val correctAnswers = listOf(
            "Santu", "Kristoren", "Maria", "Jose", "Jesus", "Maria", "Etxetxu", "Nor", "zeunke"
        )
        containerKopla5.removeAllViews()
        val tv = TextView(this).apply {
            text = correctAnswers.joinToString(separator = ", ")
            textSize = 16f
        }
        containerKopla5.addView(tv)
    }

    private fun listenForAllDynamicData() {
        listenForAndraMari4Answers()
        listenForAndraMari6Images()
        listenForAndraMari7Opinions()
        progressBar.postDelayed({ progressBar.visibility = View.GONE }, 2500)
    }

    // --- CORRECCIÓN: AHORA SE CAPTURAN LOS ERRORES DE LECTURA ---

    private fun listenForAndraMari4Answers() {
        val listener = db.collection("andra_mari_4_erantzunak")
            .whereEqualTo("gela", codigoClase).orderBy("data", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("Firestore Error", "Error en AndraMari4: ", error)
                    Toast.makeText(this, "Errorea 4. erantzunak irakurtzean", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                containerAnswers4.removeAllViews()
                snapshots?.forEach { doc ->
                    val answer = doc.toObject<AndraMari4Answer>()
                    val tv = TextView(this).apply {
                        text = "👤 ${answer.izena}:\n\t- ${answer.erantzuna1}\n\t- ${answer.erantzuna2}"
                        setPadding(0, 10, 0, 10)
                    }
                    containerAnswers4.addView(tv)
                }
            }
        listeners.add(listener)
    }

    private fun listenForAndraMari6Images() {
        val listener = db.collection("andra_mari_argazkiak")
            .whereEqualTo("gela", codigoClase).orderBy("data", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("Firestore Error", "Error en AndraMari6: ", error)
                    Toast.makeText(this, "Errorea 6. irudiak irakurtzean", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                val imageList = snapshots?.toObjects(AndraMari6Image::class.java) ?: emptyList()
                rvImages6.adapter = AndraMari6ImageAdapter(this, imageList)
            }
        listeners.add(listener)
    }

    private fun listenForAndraMari7Opinions() {
        val listener = db.collection("andra_mari_iritziak")
            .whereEqualTo("gela", codigoClase).orderBy("data", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("Firestore Error", "Error en AndraMari7: ", error)
                    Toast.makeText(this, "Errorea 7. iritziak irakurtzean", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                containerOpinions7.removeAllViews()
                snapshots?.forEach { doc ->
                    val opinion = doc.toObject<AndraMari7Opinion>()
                    val tv = TextView(this).apply {
                        text = "👤 ${opinion.izena}:\n\t- ${opinion.iritzia}"
                        setPadding(0, 10, 0, 10)
                    }
                    containerOpinions7.addView(tv)
                }
            }
        listeners.add(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        listeners.forEach { it.remove() } // Limpiamos todos los listeners
    }
}

class AndraMari6ImageAdapter(private val context: Context, private val items: List<AndraMari6Image>) : 
    RecyclerView.Adapter<AndraMari6ImageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreAlumno)
        val ivImagen: ImageView = view.findViewById(R.id.ivDibujoAlumno)
        val puntuazioaLayout: View = view.findViewById(R.id.puntuazioa_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_marrazkia_admin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvNombre.text = item.izena
        holder.puntuazioaLayout.visibility = View.GONE 

        Glide.with(context)
            .load(item.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.stat_notify_error)
            .into(holder.ivImagen)
    }

    override fun getItemCount() = items.size
}