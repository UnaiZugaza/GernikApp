package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore
import com.unairubenigor.gernikapp.R

data class Marrazkia(
    var id: String = "",
    val izena: String = "",
    val base64: String = "",
    var puntuazioa: Int = 0
)

class MarrazkiakAdapter(private val lista: List<Marrazkia>) :
    RecyclerView.Adapter<MarrazkiakAdapter.MarrazkiaViewHolder>() {

    // 1. ViewHolder actualizado con los nuevos elementos
    class MarrazkiaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreAlumno)
        val ivImagen: ImageView = view.findViewById(R.id.ivDibujoAlumno)
        val etPuntuacion: EditText = view.findViewById(R.id.etPuntuacion)
        val btnGuardarNota: Button = view.findViewById(R.id.btnGuardarNota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarrazkiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_marrazkia_admin, parent, false)
        return MarrazkiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarrazkiaViewHolder, position: Int) {
        val item = lista[position]
        holder.tvNombre.text = item.izena

        // 2. Mostrar la puntuación actual si existe
        if (item.puntuazioa > 0) {
            holder.etPuntuacion.setText(item.puntuazioa.toString())
        } else {
            holder.etPuntuacion.setText("")
        }

        // Lógica de la imagen (sin cambios)
        if (item.base64.isNotEmpty()) {
            try {
                val imageBytes = Base64.decode(item.base64, Base64.DEFAULT)
                Glide.with(holder.itemView.context).asBitmap().load(imageBytes)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                    .into(holder.ivImagen)
                holder.ivImagen.setOnClickListener {
                    mostrarImagenGrande(holder.itemView.context, imageBytes, item.izena)
                }
            } catch (e: Exception) {
                holder.ivImagen.setImageResource(android.R.drawable.stat_notify_error)
            }
        } else {
            holder.ivImagen.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // 3. Lógica para el botón de GUARDAR
        holder.btnGuardarNota.setOnClickListener {
            val notaStr = holder.etPuntuacion.text.toString()
            if (notaStr.isEmpty()) {
                Toast.makeText(holder.itemView.context, "Idatzi nota bat", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nota = notaStr.toIntOrNull()
            if (nota == null || nota < 0 || nota > 10) {
                Toast.makeText(holder.itemView.context, "Nota 0 eta 10 artean egon behar da", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guardar en Firebase
            guardarPuntuacionEnFirebase(item.id, nota, holder.itemView.context)
        }
    }

    private fun guardarPuntuacionEnFirebase(documentId: String, nota: Int, context: Context) {
        if (documentId.isEmpty()) {
            Toast.makeText(context, "Errorea: Marrazkiaren IDa ez da aurkitu", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("arbola_marrazkiak").document(documentId)
            .update("puntuazioa", nota)
            .addOnSuccessListener {
                Toast.makeText(context, "Nota ondo gorde da", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Errorea nota gordetzean: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun getItemCount() = lista.size

    private fun mostrarImagenGrande(context: Context, imageBytes: ByteArray, nombre: String) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_popup_imagen)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        val ivGrande = dialog.findViewById<ImageView>(R.id.ivPopupImagen)
        val btnCerrar = dialog.findViewById<ImageButton>(R.id.btnPopupCerrar)
        val tvTitulo = dialog.findViewById<TextView>(R.id.tvPopupNombre)

        tvTitulo.text = nombre
        Glide.with(context).load(imageBytes).into(ivGrande)
        btnCerrar.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
}