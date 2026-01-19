package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                // 1. Convertimos el texto Base64 a Bytes (Array de datos)
                val imageBytes = Base64.decode(item.base64, Base64.DEFAULT)

                // 2. Usamos GLIDE para cargar esos Bytes.
                // Glide gestiona la memoria y evita que salga corrupta o gris.
                Glide.with(holder.itemView.context)
                    .asBitmap()
                    .load(imageBytes)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // No cachear en disco (ya viene de DB)
                    .skipMemoryCache(true) // Ahorrar memoria RAM
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.stat_notify_error)
                    .into(holder.ivImagen)

            } catch (e: Exception) {
                holder.ivImagen.setImageResource(android.R.drawable.stat_notify_error)
            }
        } else {
            holder.ivImagen.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    override fun getItemCount() = lista.size
}