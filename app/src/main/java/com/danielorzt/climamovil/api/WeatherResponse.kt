package com.danielorzt.climamovil.api

/**
 * Parte del JSON de OpenWeatherMap que usa la app.
 * Todos los campos aceptan null porque Gson no valida que el JSON los traiga.
 */
data class WeatherResponse(
    val name: String?,
    val sys: Sys?,
    val main: Main?,
    val weather: List<Weather>?,
    val wind: Wind?
) {
    data class Sys(val country: String?)
    data class Main(val temp: Double?, val humidity: Int?)
    data class Weather(val description: String?, val icon: String?)
    data class Wind(val speed: Double?)
}
