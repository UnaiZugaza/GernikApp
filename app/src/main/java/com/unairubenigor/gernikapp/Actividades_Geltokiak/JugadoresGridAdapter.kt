package com.unairubenigor.gernikapp.Actividades_Geltokiak

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.unairubenigor.gernikapp.R

class JugadoresGridAdapter(private var lista: List<JugadorEstadoGrid>) :
    RecyclerView.Adapter<JugadoresGridAdapter.JugadorViewHolder>() {

    class JugadorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreJugador)
        val ivAvatar: ImageView = view.findViewById(R.id.ivAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JugadorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jugador_grid, parent, false)
        return JugadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: JugadorViewHolder, position: Int) {
        val jugador = lista[position]
        holder.tvNombre.text = jugador.nombre
        val avatarView = holder.ivAvatar

        // 1. Definir cómo se ve normalmente (Vivo = Opaco / Muerto = Transparente)
        val alphaFinal = if (jugador.vivo) 1.0f else 0.5f

        // 2. Lógica del Parpadeo (El "Tic" del Asesino)
        if (jugador.necesitaParpadeo) {
            // Animación: Se desvanece rápido y vuelve
            avatarView.animate()
                .alpha(0.2f)       // Se vuelve casi invisible
                .setDuration(150)  // Duración del "tic"
                .withEndAction {
                    // Vuelve a su estado normal (según si está vivo o muerto)
                    avatarView.animate()
                        .alpha(alphaFinal)
                        .setDuration(150)
                        .start()
                }
                .start()

            // Apagamos el interruptor para que solo lo haga una vez
            jugador.necesitaParpadeo = false
        } else {
            // Si no tiene que parpadear, aseguramos que tenga la transparencia correcta
            // (Esto es importante porque el RecyclerView recicla las vistas)
            avatarView.alpha = alphaFinal
        }
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<JugadorEstadoGrid>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}

// Clase de datos que controla el estado de cada casilla
data class JugadorEstadoGrid(
    val nombre: String,
    val vivo: Boolean,
    var necesitaParpadeo: Boolean = false // Esta es la clave para la pista del asesino
)