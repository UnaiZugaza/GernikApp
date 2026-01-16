package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.unairubenigor.gernikapp.R

class JugadoresGridAdapter(private var lista: List<String>) :
    RecyclerView.Adapter<JugadoresGridAdapter.JugadorViewHolder>() {

    class JugadorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreJugador)
        // Aquí podrías acceder al ImageView (ivAvatar) si quisieras cambiar la foto
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JugadorViewHolder {
        // "Inflamos" el diseño item_jugador_grid.xml
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jugador_grid, parent, false)
        return JugadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: JugadorViewHolder, position: Int) {
        // Ponemos el nombre del jugador en el texto
        holder.tvNombre.text = lista[position]
    }

    override fun getItemCount() = lista.size

    // Esta es la función que te daba error "Unresolved reference: actualizarLista"
    fun actualizarLista(nuevaLista: List<String>) {
        lista = nuevaLista
        notifyDataSetChanged() // Avisa a la pantalla para refrescarse
    }
}