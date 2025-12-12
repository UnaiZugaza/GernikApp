package com.unairubenigor.gernikapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.unairubenigor.gernikapp.Programas_Ranking.RankingUsuario

class AdminSQL(context: Context) : SQLiteOpenHelper(context, "GernikappDB", null, 1) {

    // 1. Se ejecuta la primera vez para crear la tabla
    override fun onCreate(db: SQLiteDatabase) {
        // Tabla 'puntuaciones': guarda el NOMBRE (clave única) y los PUNTOS
        db.execSQL("CREATE TABLE puntuaciones (nombre TEXT PRIMARY KEY, puntos INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Aquí iría la lógica si cambias la versión de la base de datos en el futuro
    }

    // 2. Función para REGISTRAR o SUMAR puntos
    // Si le pasas 0 puntos, solo sirve para asegurar que el usuario existe en la tabla
    fun registrarPuntos(usuario: String, puntosNuevos: Int) {
        val db = this.writableDatabase

        // Buscamos si el usuario ya existe
        val cursor = db.rawQuery("SELECT puntos FROM puntuaciones WHERE nombre = ?", arrayOf(usuario))

        if (cursor.moveToFirst()) {
            // SI YA EXISTE: Le sumamos los puntos nuevos a los que tenga
            val puntosActuales = cursor.getInt(0)
            val total = puntosActuales + puntosNuevos

            val contentValues = ContentValues()
            contentValues.put("puntos", total)
            db.update("puntuaciones", contentValues, "nombre = ?", arrayOf(usuario))
        } else {
            // SI NO EXISTE: Lo creamos nuevo
            val contentValues = ContentValues()
            contentValues.put("nombre", usuario)
            contentValues.put("puntos", puntosNuevos) // Si es el inicio, serán 0
            db.insert("puntuaciones", null, contentValues)
        }
        cursor.close()
        db.close()
    }

    // 3. Función para OBTENER EL RANKING
    fun obtenerRanking(): List<RankingUsuario> {
        val lista = ArrayList<RankingUsuario>()
        val db = this.readableDatabase

        // Pedimos los datos ordenados de MAYOR a MENOR puntuación (DESC)
        val cursor = db.rawQuery("SELECT nombre, puntos FROM puntuaciones ORDER BY puntos DESC", null)

        var posicion = 1
        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(0)
                val puntos = cursor.getInt(1)

                lista.add(RankingUsuario(posicion, nombre, puntos))
                posicion++
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
}