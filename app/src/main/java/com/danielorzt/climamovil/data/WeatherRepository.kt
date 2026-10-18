package com.danielorzt.climamovil.data

import com.danielorzt.climamovil.BuildConfig
import com.danielorzt.climamovil.api.RetrofitClient
import com.danielorzt.climamovil.api.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/** Une la API con la base de datos: consulta el clima y lo guarda en SQLite. */
class WeatherRepository(
    private val db: DbHelper,
    private val api: WeatherApi = RetrofitClient.api
) {

    /** Consulta la ciudad en la API y la guarda (o actualiza) en SQLite. */
    suspend fun agregar(ciudad: String) {
        val ubicacion = consultar(ciudad)
        withContext(Dispatchers.IO) { db.guardar(ubicacion) }
    }

    /** Vuelve a consultar todas las ubicaciones. Devuelve (actualizadas, con error). */
    suspend fun actualizarTodas(): Pair<Int, Int> {
        var ok = 0
        var fallos = 0
        for (u in listar()) {
            try {
                val nueva = consultar("${u.ciudad},${u.pais}")
                withContext(Dispatchers.IO) { db.guardar(nueva) }
                ok++
            } catch (e: ClimaException) {
                fallos++
            }
        }
        return ok to fallos
    }

    suspend fun listar(): List<Ubicacion> = withContext(Dispatchers.IO) { db.listar() }

    suspend fun eliminar(id: Long) {
        withContext(Dispatchers.IO) { db.eliminar(id) }
    }

    private suspend fun consultar(ciudad: String): Ubicacion {
        if (BuildConfig.OPENWEATHER_API_KEY.isBlank()) {
            throw ClimaException("Falta la clave de la API. Agrégala en local.properties.")
        }
        return try {
            api.clima(ciudad.trim(), BuildConfig.OPENWEATHER_API_KEY).toUbicacion()
        } catch (e: HttpException) {
            throw ClimaException(
                when (e.code()) {
                    404 -> "No encontré esa ciudad"
                    401 -> "La clave de la API no es válida o todavía no está activa"
                    429 -> "Demasiadas consultas, espera un minuto"
                    else -> "Error de la API (${e.code()})"
                }
            )
        } catch (e: IOException) {
            throw ClimaException("Sin conexión a internet")
        }
    }
}
