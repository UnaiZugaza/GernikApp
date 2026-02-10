package com.unairubenigor.gernikapp.Options

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.os.Bundle
import android.view.DragEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.GridLayout
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.unairubenigor.gernikapp.R
import java.util.Collections

class OptionPuzzleActivity : AppCompatActivity() {

    private lateinit var piecesContainer: LinearLayout
    private lateinit var puzzleBoard: GridLayout
    private lateinit var puzzleBoardContainer: View
    private lateinit var piecesContainerScrollView: HorizontalScrollView
    private lateinit var piecesScrollBar: SeekBar
    private val pieceViews = mutableListOf<ImageView>()
    private var piecesPlaced = 0
    private var scaleFactor = 1.0f
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    // --- NUEVAS VARIABLES PARA FIREBASE ---
    private val db = FirebaseFirestore.getInstance()
    private var nombreUsuario: String = ""
    private var codigoClase: String = ""

    private var row1Unlocked = false
    private var row2Unlocked = false
    private var row3Unlocked = false
    private var row4Unlocked = false
    private var row5Unlocked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzle)

        // --- RECUPERAR DATOS DEL INTENT ---
        nombreUsuario = intent.getStringExtra("USER_NAME") ?: ""
        codigoClase = intent.getStringExtra("CLASS_CODE") ?: ""

        piecesContainer = findViewById(R.id.pieces_container)
        puzzleBoard = findViewById(R.id.puzzle_board)
        puzzleBoardContainer = findViewById(R.id.puzzle_board_container)
        piecesContainerScrollView = findViewById(R.id.pieces_container_scrollview)
        piecesScrollBar = findViewById(R.id.pieces_scrollbar)

        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "PUZZLE"

        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        btnBack.setOnClickListener { finish() }

        setupZoom()
        checkProgressAndSetup()
        setupSeekBar()
    }

    // ... (Mantén setupZoom, setupPuzzlePieces, setupPuzzleBoard y setupSeekBar igual)

    private fun checkProgressAndSetup() {
        val prefs = getSharedPreferences("GernikaProgress", Context.MODE_PRIVATE)
        row1Unlocked = prefs.getBoolean("astraCompletado", false)
        row2Unlocked = prefs.getBoolean("arbolaCompletado", false)
        row3Unlocked = prefs.getBoolean("pasilekuCompletado", false)
        row4Unlocked = prefs.getBoolean("andraMariCompletado", false)
        row5Unlocked = prefs.getBoolean("muralaCompletado", false)

        setupPuzzlePieces()
        setupPuzzleBoard()
    }

    private fun setupZoom() {
        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 3.0f))
                puzzleBoard.scaleX = scaleFactor
                puzzleBoard.scaleY = scaleFactor
                return true
            }
        })
        puzzleBoardContainer.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun setupPuzzlePieces() {
        piecesContainer.removeAllViews()
        pieceViews.clear()

        val pieceResources = mutableListOf<Int>()

        if (row1Unlocked) for (j in 1..6) pieceResources.add(resources.getIdentifier("row1column$j", "drawable", packageName))
        if (row2Unlocked) for (j in 1..6) pieceResources.add(resources.getIdentifier("row2column$j", "drawable", packageName))
        if (row3Unlocked) for (j in 1..6) pieceResources.add(resources.getIdentifier("row3column$j", "drawable", packageName))
        if (row4Unlocked) for (j in 1..6) pieceResources.add(resources.getIdentifier("row4column$j", "drawable", packageName))
        if (row5Unlocked) for (j in 1..6) pieceResources.add(resources.getIdentifier("row5column$j", "drawable", packageName))

        Collections.shuffle(pieceResources)

        for (resId in pieceResources) {
            if (resId == 0) continue
            val piece = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply { setMargins(8, 8, 8, 8) }
                adjustViewBounds = true
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageResource(resId)
                tag = resources.getResourceEntryName(resId)
                setOnTouchListener { v, _ ->
                    val clipData = ClipData.newPlainText("piece_tag", v.tag.toString())
                    v.startDragAndDrop(clipData, View.DragShadowBuilder(v), v, 0)
                    v.visibility = View.INVISIBLE
                    true
                }
            }
            pieceViews.add(piece)
            piecesContainer.addView(piece)
        }
    }

    private fun setupPuzzleBoard() {
        puzzleBoard.removeAllViews()
        puzzleBoard.post {
            val cellWidth = puzzleBoard.width / 6
            val cellHeight = puzzleBoard.height / 5
            for (i in 1..5) {
                for (j in 1..6) {
                    val slot = ImageView(this).apply {
                        layoutParams = GridLayout.LayoutParams().apply {
                            width = cellWidth
                            height = cellHeight
                        }
                        setPadding(4, 4, 4, 4)
                        val resId = resources.getIdentifier("row${i}column${j}", "drawable", packageName)
                        if (resId != 0) {
                            setImageResource(resId)
                            alpha = 0.25f
                            scaleType = ImageView.ScaleType.FIT_XY
                        }
                        setBackgroundResource(R.drawable.rounded_border)
                        tag = "row${i}column${j}"
                        setOnDragListener(PuzzleDragListener())
                    }
                    puzzleBoard.addView(slot)
                }
            }
        }
    }

    private fun setupSeekBar() {
        piecesScrollBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val maxScroll = piecesContainer.width - piecesContainerScrollView.width
                    if (maxScroll > 0) {
                        val scrollTo = (maxScroll * progress) / 100
                        piecesContainerScrollView.scrollTo(scrollTo, 0)
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    // --- LÓGICA DE FIREBASE IMPLEMENTADA SIN ERRORES ---
    private fun checkCompletion() {
        if (piecesPlaced == 30) {
            if (codigoClase.isEmpty() || nombreUsuario.isEmpty()) {
                Toast.makeText(this, "Errorea: Erabiltzaile datuak falta dira", Toast.LENGTH_SHORT).show()
                return
            }

            val partidaRef = db.collection("partidas").document(codigoClase)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(partidaRef)
                val puzzleCompletado = snapshot.getBoolean("puzzleCompletado") ?: false

                if (!puzzleCompletado) {
                    // Soy el primero: marco el puzle como terminado globalmente
                    transaction.update(partidaRef, "puzzleCompletado", true)

                    // Sumo el punto al usuario
                    val jugadorRef = partidaRef.collection("jugadores").document(nombreUsuario)
                    transaction.update(jugadorRef, "puntuacion", FieldValue.increment(1))
                    true // Fui el primero
                } else {
                    false // Alguien ganó antes
                }
            }.addOnSuccessListener { soyElPrimero ->
                if (soyElPrimero) {
                    Toast.makeText(this, "ZORIONAK! Lehena izan zara! (+1 puntu)", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Puzzlea osatuta! (Beste bat lehenago iritsi da)", Toast.LENGTH_SHORT).show()
                }

                // Guardar progreso local también
                val prefs = getSharedPreferences("GernikaProgress", Context.MODE_PRIVATE)
                prefs.edit().putBoolean("puzzleCompletado", true).apply()

            }.addOnFailureListener { e ->
                Toast.makeText(this, "Errorea puntuak gordetzean: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private inner class PuzzleDragListener : View.OnDragListener {
        override fun onDrag(v: View, event: DragEvent): Boolean {
            val targetSlot = v as ImageView
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> return event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                DragEvent.ACTION_DROP -> {
                    val draggedPieceTag = event.clipData.getItemAt(0).text.toString()
                    if (draggedPieceTag == targetSlot.tag.toString()) {
                        val originalPiece = pieceViews.find { it.tag == draggedPieceTag }
                        targetSlot.setImageDrawable(originalPiece?.drawable)
                        targetSlot.setPadding(0, 0, 0, 0)
                        targetSlot.alpha = 1.0f
                        targetSlot.scaleType = ImageView.ScaleType.FIT_XY
                        targetSlot.setBackground(null)
                        targetSlot.setOnDragListener(null)
                        (event.localState as View).visibility = View.GONE
                        piecesPlaced++
                        checkCompletion()
                        return true
                    } else {
                        (event.localState as View).visibility = View.VISIBLE
                        return false
                    }
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    if (!event.result) (event.localState as View).visibility = View.VISIBLE
                    return true
                }
                else -> return true
            }
        }
    }
}