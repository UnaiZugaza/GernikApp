package com.unairubenigor.gernikapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RankingAdapter(private val listaUsuarios: List<RankingUsuario>) :
    RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    // Crea la vista visual para cada elemento
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ranking, parent, false)
        return RankingViewHolder(view)
    }

    // Conecta los datos con la vista
    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val usuario = listaUsuarios[position]
        holder.tvPosicion.text = "#${usuario.posicion}"
        holder.tvNombreUsuario.text = usuario.nombre
        holder.tvPuntuacion.text = "${usuario.puntuacion} pts"
    }

    // Dice cuántos elementos hay en total
    override fun getItemCount(): Int = listaUsuarios.size

    // Clase interna para guardar las referencias a los TextViews
    class RankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPosicion: TextView = itemView.findViewById(R.id.tvPosicion)
        val tvNombreUsuario: TextView = itemView.findViewById(R.id.tvNombreUsuario)
        val tvPuntuacion: TextView = itemView.findViewById(R.id.tvPuntuacion)
    }
}