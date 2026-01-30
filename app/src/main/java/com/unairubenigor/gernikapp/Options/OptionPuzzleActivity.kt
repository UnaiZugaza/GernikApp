package com.unairubenigor.gernikapp.Options

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context // Importante para SharedPreferences
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

    // Estados de desbloqueo por fila
    private var row1Unlocked = false // Astra
    private var row2Unlocked = false // Arbola
    private var row3Unlocked = false // Pasileku
    private var row4Unlocked = false // Andra Mari
    private var row5Unlocked = false // Murala

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzle)

        piecesContainer = findViewById(R.id.pieces_container)
        puzzleBoard = findViewById(R.id.puzzle_board)
        puzzleBoardContainer = findViewById(R.id.puzzle_board_container)
        piecesContainerScrollView = findViewById(R.id.pieces_container_scrollview)
        piecesScrollBar = findViewById(R.id.pieces_scrollbar)

        val headerView = findViewById<View>(R.id.header_layout)
        val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "PUZZLE"

        // Configurar Footer (Solo Atrás)
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        btnBack.setOnClickListener {
            finish()
        }

        setupZoom()
        checkProgressAndSetup() // Aquí leemos la memoria local
        setupSeekBar()
    }

    private fun checkProgressAndSetup() {
        // Accedemos a la memoria interna llamada "GernikaProgress"
        val prefs = getSharedPreferences("GernikaProgress", Context.MODE_PRIVATE)

        // Leemos si las geltokias han sido completadas (por defecto false)
        row1Unlocked = prefs.getBoolean("astraCompletado", false)
        row2Unlocked = prefs.getBoolean("arbolaCompletado", false)
        row3Unlocked = prefs.getBoolean("pasilekuCompletado", false)
        row4Unlocked = prefs.getBoolean("andraMariCompletado", false)
        row5Unlocked = prefs.getBoolean("muralaCompletado", false)

        setupPuzzlePieces()
        setupPuzzleBoard()

        // Mensaje si no hay nada desbloqueado
        if (!row1Unlocked && !row2Unlocked && !row3Unlocked && !row4Unlocked && !row5Unlocked) {
            Toast.makeText(this, "Geltokiak osatu puzzleko piezak lortzeko!", Toast.LENGTH_LONG).show()
        }
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

        // Cargar recursos dinámicamente según las filas desbloqueadas
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

                        // Guía transparente
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

    private fun checkCompletion() {
        val totalPiezasDesbloqueadas = (if(row1Unlocked) 6 else 0) + (if(row2Unlocked) 6 else 0) + (if(row3Unlocked) 6 else 0) + (if(row4Unlocked) 6 else 0) + (if(row5Unlocked) 6 else 0)

        // Lógica de victoria puramente local
        if (piecesPlaced == 30 && totalPiezasDesbloqueadas == 30) {
            // Guardamos localmente que el puzzle está completo por si queremos consultarlo luego
            val prefs = getSharedPreferences("GernikaProgress", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("puzzleCompletado", true).apply()

            Toast.makeText(this, "ZORIONAK! Puzzlea osatu duzu!", Toast.LENGTH_LONG).show()

        } else if (piecesPlaced == totalPiezasDesbloqueadas && totalPiezasDesbloqueadas < 30) {
            Toast.makeText(this, "Geltoki gehiago osatu behar dituzu!", Toast.LENGTH_SHORT).show()
        }
    }
}