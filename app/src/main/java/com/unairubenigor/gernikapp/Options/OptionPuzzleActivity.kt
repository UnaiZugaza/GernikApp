package com.unairubenigor.gernikapp

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Collections

class OptionPuzzleActivity : AppCompatActivity() {

    // Contenedores del layout
    private lateinit var piecesContainer: LinearLayout
    private lateinit var puzzleBoard: GridLayout
    private lateinit var puzzleBoardContainer: View

    // Lista para guardar las piezas
    private val pieceViews = mutableListOf<ImageView>()

    // Contador de piezas colocadas
    private var piecesPlaced = 0

    // Variables para la funcionalidad de ZOOM
    private var scaleFactor = 1.0f
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option_d)

        // --- 1. CONFIGURACIÓN INICIAL Y REFERENCIAS ---
        piecesContainer = findViewById(R.id.pieces_container)
        puzzleBoard = findViewById(R.id.puzzle_board)
        puzzleBoardContainer = findViewById(R.id.puzzle_board_container)

        // Configurar el Header
        val headerView = findViewById<View>(R.id.header_layout)
        val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
        headerTitle.text = "PUZZLE"

        // --- 2. PREPARAR PIEZAS, TABLERO Y ZOOM ---
        setupZoom()
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
        val pieceResources = mutableListOf<Int>().apply {
            for (i in 1..5) {
                for (j in 1..6) {
                    val resourceId = resources.getIdentifier("row${i}column${j}", "drawable", packageName)
                    if (resourceId != 0) add(resourceId)
                }
            }
        }

        Collections.shuffle(pieceResources)

        for (resId in pieceResources) {
            val piece = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(8, 8, 8, 8)
                }
                adjustViewBounds = true

                // --- ¡CORRECCIÓN CLAVE! ---
                // Cambiamos de CENTER_CROP a FIT_CENTER para que la imagen no se recorte.
                scaleType = ImageView.ScaleType.FIT_CENTER

                setImageResource(resId)
                tag = resources.getResourceEntryName(resId)

                setOnTouchListener { v, _ ->
                    val clipData = ClipData.newPlainText("piece_tag", v.tag.toString())
                    val shadowBuilder = View.DragShadowBuilder(v)
                    v.startDragAndDrop(clipData, shadowBuilder, v, 0)
                    v.visibility = View.INVISIBLE
                    true
                }
            }
            pieceViews.add(piece)
            piecesContainer.addView(piece)
        }
    }

    private fun setupPuzzleBoard() {
        puzzleBoard.post {
            val boardWidth = puzzleBoard.width
            val boardHeight = puzzleBoard.height
            val cellWidth = boardWidth / 6
            val cellHeight = boardHeight / 5

            for (i in 1..5) {
                for (j in 1..6) {
                    val slot = ImageView(this).apply {
                        layoutParams = GridLayout.LayoutParams().apply {
                            width = cellWidth
                            height = cellHeight
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

    private inner class PuzzleDragListener : View.OnDragListener {
        override fun onDrag(v: View, event: DragEvent): Boolean {
            val targetSlot = v as ImageView
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    return event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }
                DragEvent.ACTION_DROP -> {
                    val draggedPieceTag = event.clipData.getItemAt(0).text.toString()
                    val targetSlotTag = targetSlot.tag.toString()

                    if (draggedPieceTag == targetSlotTag) {
                        val draggedView = event.localState as ImageView
                        val originalPiece = pieceViews.find { it.tag == draggedPieceTag }

                        targetSlot.setImageDrawable(originalPiece?.drawable)
                        targetSlot.setBackground(null)
                        targetSlot.setOnDragListener(null)
                        draggedView.visibility = View.GONE

                        piecesPlaced++
                        checkCompletion()
                        return true
                    } else {
                        (event.localState as View).visibility = View.VISIBLE
                        Toast.makeText(this@OptionPuzzleActivity, "Pieza incorrecta", Toast.LENGTH_SHORT).show()
                        return false
                    }
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    if (!event.result) {
                        (event.localState as View).visibility = View.VISIBLE
                    }
                    return true
                }
                else -> return true
            }
        }
    }

    private fun checkCompletion() {
        if (piecesPlaced == 30) {
            Toast.makeText(this, "¡Felicidades! ¡Has completado el puzzle!", Toast.LENGTH_LONG).show()
        }
    }
}