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

        // 1. ELEGIR LA IMAGEN SEGÚN EL ID QUE VIENE DE FIREBASE
        val imagenRes = when (jugador.avatarId) {
            "2" -> R.drawable.cartman
            "3" -> R.drawable.kenny
            else -> R.drawable.kyle // Por defecto el 1
        }
        avatarView.setImageResource(imagenRes)

        // 2. ESTADO VIVO/MUERTO (Transparencia)
        val alphaFinal = if (jugador.vivo) 1.0f else 0.5f

        // 3. PARPADEO DEL ASESINO
        if (jugador.necesitaParpadeo) {
            avatarView.animate()
                .alpha(0.2f)
                .setDuration(150)
                .withEndAction {
                    avatarView.animate().alpha(alphaFinal).setDuration(150).start()
                }
                .start()
            jugador.necesitaParpadeo = false
        } else {
            avatarView.alpha = alphaFinal
        }
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<JugadorEstadoGrid>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}

// AÑADIMOS EL CAMPO 'avatarId'
data class JugadorEstadoGrid(
    val nombre: String,
    val vivo: Boolean,
    var necesitaParpadeo: Boolean = false,
    val avatarId: String = "1"
)