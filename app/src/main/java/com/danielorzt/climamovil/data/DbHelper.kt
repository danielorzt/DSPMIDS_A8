package com.danielorzt.climamovil.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/** Base de datos SQLite local con la tabla "ubicaciones". */
class DbHelper(context: Context) :
    SQLiteOpenHelper(context, "clima.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLA (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              ciudad TEXT UNIQUE, pais TEXT,
              temp REAL, humedad INTEGER, viento REAL,
              descripcion TEXT, icono TEXT,
              actualizado INTEGER)
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA")
        onCreate(db)
    }

    /** INSERT; si la ciudad ya existe, reemplaza la fila (así se actualiza el clima). */
    fun guardar(u: Ubicacion): Long {
        val valores = ContentValues().apply {
            put("ciudad", u.ciudad)
            put("pais", u.pais)
            put("temp", u.temp)
            put("humedad", u.humedad)
            put("viento", u.viento)
            put("descripcion", u.descripcion)
            put("icono", u.icono)
            put("actualizado", u.actualizado)
        }
        return writableDatabase.insertWithOnConflict(
            TABLA, null, valores, SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    /** SELECT: todas las ubicaciones guardadas, ordenadas por nombre. */
    fun listar(): List<Ubicacion> {
        val lista = mutableListOf<Ubicacion>()
        readableDatabase.query(
            TABLA, null, null, null, null, null, "ciudad COLLATE NOCASE ASC"
        ).use { c ->
            while (c.moveToNext()) {
                lista.add(
                    Ubicacion(
                        id = c.getLong(c.getColumnIndexOrThrow("id")),
                        ciudad = c.getString(c.getColumnIndexOrThrow("ciudad")),
                        pais = c.getString(c.getColumnIndexOrThrow("pais")).orEmpty(),
                        temp = c.getDouble(c.getColumnIndexOrThrow("temp")),
                        humedad = c.getInt(c.getColumnIndexOrThrow("humedad")),
                        viento = c.getDouble(c.getColumnIndexOrThrow("viento")),
                        descripcion = c.getString(c.getColumnIndexOrThrow("descripcion")).orEmpty(),
                        icono = c.getString(c.getColumnIndexOrThrow("icono")).orEmpty(),
                        actualizado = c.getLong(c.getColumnIndexOrThrow("actualizado"))
                    )
                )
            }
        }
        return lista
    }

    /** DELETE por identificador. Devuelve cuántas filas se borraron. */
    fun eliminar(id: Long): Int =
        writableDatabase.delete(TABLA, "id = ?", arrayOf(id.toString()))

    companion object {
        private const val TABLA = "ubicaciones"
    }
}
