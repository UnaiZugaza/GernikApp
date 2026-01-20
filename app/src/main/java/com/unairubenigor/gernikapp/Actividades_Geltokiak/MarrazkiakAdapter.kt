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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.unairubenigor.gernikapp.R

data class Marrazkia(
    val izena: String = "",
    val base64: String = ""
)

class MarrazkiakAdapter(private val lista: List<Marrazkia>) :
    RecyclerView.Adapter<MarrazkiakAdapter.MarrazkiaViewHolder>() {

    class MarrazkiaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreAlumno)
        val ivImagen: ImageView = view.findViewById(R.id.ivDibujoAlumno)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarrazkiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_marrazkia_admin, parent, false)
        return MarrazkiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarrazkiaViewHolder, position: Int) {
        val item = lista[position]
        holder.tvNombre.text = item.izena

        if (item.base64.isNotEmpty()) {
            try {
                // 1. Decodificar para la miniatura
                val imageBytes = Base64.decode(item.base64, Base64.DEFAULT)

                Glide.with(holder.itemView.context)
                    .asBitmap()
                    .load(imageBytes)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.stat_notify_error)
                    .into(holder.ivImagen)

                // 2. EVENTO CLICK: Al tocar la imagen, abrirla en grande
                holder.ivImagen.setOnClickListener {
                    mostrarImagenGrande(holder.itemView.context, imageBytes, item.izena)
                }

            } catch (e: Exception) {
                holder.ivImagen.setImageResource(android.R.drawable.stat_notify_error)
            }
        } else {
            holder.ivImagen.setImageResource(android.R.drawable.ic_menu_gallery)
            holder.ivImagen.setOnClickListener(null)
        }
    }

    override fun getItemCount() = lista.size

    // --- FUNCIÓN PARA EL POPUP DE PANTALLA COMPLETA ---
    private fun mostrarImagenGrande(context: Context, imageBytes: ByteArray, nombre: String) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        // Creamos un diseño simple por código para no crear otro XML
        dialog.setContentView(R.layout.layout_popup_imagen)
        // OJO: Necesitamos crear este XML pequeño (paso 2 abajo)

        // Configurar fondo transparente para que se vea moderno
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        val ivGrande = dialog.findViewById<ImageView>(R.id.ivPopupImagen)
        val btnCerrar = dialog.findViewById<ImageButton>(R.id.btnPopupCerrar)
        val tvTitulo = dialog.findViewById<TextView>(R.id.tvPopupNombre)

        tvTitulo.text = nombre

        // Cargar imagen en grande usando FIT_CENTER (para verla entera)
        Glide.with(context)
            .load(imageBytes)
            .into(ivGrande)

        btnCerrar.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
}