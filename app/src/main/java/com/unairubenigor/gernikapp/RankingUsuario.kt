package com.unairubenigor.gernikapp

// Esta clase representa una fila en tu tabla de puntuaciones
data class RankingUsuario(
    val posicion: Int,
    val nombre: String,
    val puntuacion: Int
)