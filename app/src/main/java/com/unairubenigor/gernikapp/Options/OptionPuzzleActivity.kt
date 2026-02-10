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

    // MODIFICACIÓN 1: Inicializamos todo en true por si acaso, aunque lo forzaremos abajo
    private var row1Unlocked = true
    private var row2Unlocked = true
    private var row3Unlocked = true
    private var row4Unlocked = true
    private var row5Unlocked = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzle)

        piecesContainer = findViewById(R.id.pieces_container)
        puzzleBoard = findViewById(R.id.puzzle_board)
        puzzleBoardContainer = findViewById(R.id.puzzle_board_container)
        piecesContainerScrollView = findViewById(R.id.pieces_container_scrollview)
        piecesScrollBar = findViewById(R.id.pieces_scrollbar)

        // Configurar Header (si existe en el layout)
        val headerView = findViewById<View>(R.id.header_layout)
        if (headerView != null) {
            val headerTitle = headerView.findViewById<TextView>(R.id.textViewHeaderTitle)
            if (headerTitle != null) {
                headerTitle.text = "PUZZLE"
            }
        } else {
            // Intento alternativo por si el header no es un include directo
            val headerTitle = findViewById<TextView>(R.id.textViewHeaderTitle)
            headerTitle?.text = "PUZZLE"
        }

        // Configurar Footer (Solo Atrás)
        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        btnBack.setOnClickListener {
            finish()
        }

        setupZoom()
        checkProgressAndSetup()
        setupSeekBar()
    }

    private fun checkProgressAndSetup() {
        // MODIFICACIÓN 2: IGNORAMOS SharedPreferences para el desbloqueo.
        // Forzamos a que todas las filas estén desbloqueadas siempre.

        row1Unlocked = true
        row2Unlocked = true
        row3Unlocked = true
        row4Unlocked = true
        row5Unlocked = true

        setupPuzzlePieces()
        setupPuzzleBoard()
    }

    private fun setupZoom() {
        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                // Limitamos el zoom entre 1x y 3x
                scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 3.0f))
                puzzleBoard.scaleX = scaleFactor
                puzzleBoard.scaleY = scaleFactor
                return true
            }
        })

        // Hacemos que el contenedor detecte el toque para el zoom
        puzzleBoardContainer.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            // Necesitamos devolver true para indicar que consumimos el evento,
            // pero si quieres que también se pueda hacer scroll/drag, hay que tener cuidado.
            // Para un ScaleGestureDetector simple, esto suele bastar.
            true
        }
    }

    private fun setupPuzzlePieces() {
        piecesContainer.removeAllViews()
        pieceViews.clear()

        val pieceResources = mutableListOf<Int>()

        // MODIFICACIÓN 3: Como las variables rowXUnlocked ahora son siempre TRUE,
        // cargará todas las piezas (del row1 al row5).

        if (row1Unlocked) for (j in 1..6) pieceResources.add(resources.getIdentifier("row1column$j", "drawable", packageName))
        if (row2Unlocked) for (j in 1..6) pieceResources.add(resources.getIdentifier("row2column$j", "drawable", packageName))
        if (row3Unlocked) for (j in 1..6) pieceResources.add(resources.getIdentifier("row3column$j", "drawable", packageName))
        if (row4Unlocked) for (j in 1..6) pieceResources.add(resources.getIdentifier("row4column$j", "drawable", packageName))
        if (row5Unlocked) for (j in 1..6) pieceResources.add(resources.getIdentifier("row5column$j", "drawable", packageName))

        // Barajamos las piezas para que no salgan en orden
        Collections.shuffle(pieceResources)

        for (resId in pieceResources) {
            if (resId == 0) continue // Evitar errores si falta alguna imagen
            val piece = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, // Ancho ajustado al contenido
                    300 // Altura fija para que todas las piezas se vean uniformes en la tira
                ).apply { setMargins(16, 8, 16, 8) }

                adjustViewBounds = true
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageResource(resId)

                // El tag es el nombre del recurso (ej: "row1column1")
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
        puzzleBoard.removeAllViews()
        // Usamos post para asegurarnos de que el Grid tiene dimensiones calculadas
        puzzleBoard.post {
            // Calculamos el tamaño de cada celda basándonos en el tamaño total del tablero
            // Tablero de 6 columnas x 5 filas
            val cellWidth = puzzleBoard.width / 6
            val cellHeight = puzzleBoard.height / 5

            for (i in 1..5) { // 5 Filas
                for (j in 1..6) { // 6 Columnas
                    val slot = ImageView(this).apply {
                        layoutParams = GridLayout.LayoutParams().apply {
                            width = cellWidth
                            height = cellHeight
                            // Importante definir row y column spec para GridLayout
                            rowSpec = GridLayout.spec(i - 1)
                            columnSpec = GridLayout.spec(j - 1)
                        }

                        setPadding(4, 4, 4, 4)

                        // Guía transparente de fondo
                        val resId = resources.getIdentifier("row${i}column${j}", "drawable", packageName)
                        if (resId != 0) {
                            setImageResource(resId)
                            alpha = 0.25f // Semi-transparente para dar pista
                            scaleType = ImageView.ScaleType.FIT_XY
                        }

                        setBackgroundResource(R.drawable.rounded_border) // Borde para saber dónde soltar
                        tag = "row${i}column${j}" // Tag coincide con la pieza correcta
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
                    // Calculamos cuánto scrollear basándonos en el ancho total de las piezas vs el ancho de la pantalla
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
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Solo aceptamos drags que contengan texto plano (nuestro tag)
                    return event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }
                DragEvent.ACTION_DROP -> {
                    val draggedPieceTag = event.clipData.getItemAt(0).text.toString()

                    // Verificamos si la pieza soltada coincide con el hueco (targetSlot)
                    if (draggedPieceTag == targetSlot.tag.toString()) {
                        // ¡Correcto!
                        val originalPiece = pieceViews.find { it.tag == draggedPieceTag }
                        targetSlot.setImageDrawable(originalPiece?.drawable)
                        targetSlot.setPadding(0, 0, 0, 0)
                        targetSlot.alpha = 1.0f // Opacidad completa
                        targetSlot.scaleType = ImageView.ScaleType.FIT_XY
                        targetSlot.setBackground(null) // Quitamos borde
                        targetSlot.setOnDragListener(null) // Ya no se puede interactuar con este hueco

                        // Ocultamos la pieza original del scroll (ya está colocada)
                        (event.localState as View).visibility = View.GONE

                        piecesPlaced++
                        checkCompletion()
                        return true
                    } else {
                        // Incorrecto: volvemos a mostrar la pieza en el scroll
                        (event.localState as View).visibility = View.VISIBLE
                        Toast.makeText(this@OptionPuzzleActivity, "Pieza incorrecta", Toast.LENGTH_SHORT).show()
                        return false
                    }
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    // Si el drag falló (se soltó fuera), volvemos a mostrar la pieza
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
        // Al estar desbloqueado, el total siempre será 30
        val totalPiezas = 30

        if (piecesPlaced == totalPiezas) {
            // Guardamos que se completó el puzzle
            val prefs = getSharedPreferences("GernikaProgress", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("puzzleCompletado", true).apply()

            Toast.makeText(this, getString(R.string.zorionak_puzzlea_osatu_duzu), Toast.LENGTH_LONG).show()
        }
    }
}