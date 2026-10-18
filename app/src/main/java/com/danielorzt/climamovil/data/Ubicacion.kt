package com.danielorzt.climamovil.data

import com.danielorzt.climamovil.api.WeatherResponse

/** Una ubicación guardada con su último clima conocido. */
data class Ubicacion(
    val id: Long = 0,
    val ciudad: String,
    val pais: String,
    val temp: Double,
    val humedad: Int,
    val viento: Double,
    val descripcion: String,
    val icono: String,
    val actualizado: Long
)

/** Error con un mensaje que se le puede mostrar al usuario. */
class ClimaException(mensaje: String) : Exception(mensaje)

/** Convierte la respuesta de la API en una fila lista para guardar en SQLite. */
fun WeatherResponse.toUbicacion(ahora: Long = System.currentTimeMillis()): Ubicacion {
    val primero = weather?.firstOrNull()
    return Ubicacion(
        ciudad = name ?: throw ClimaException("La API no devolvió el nombre de la ciudad"),
        pais = sys?.country.orEmpty(),
        temp = main?.temp ?: 0.0,
        humedad = main?.humidity ?: 0,
        viento = wind?.speed ?: 0.0,
        descripcion = primero?.description.orEmpty().replaceFirstChar { it.uppercase() },
        icono = primero?.icon.orEmpty(),
        actualizado = ahora
    )
}
