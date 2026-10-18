package com.danielorzt.climamovil.api

import retrofit2.http.GET
import retrofit2.http.Query

/** Llamada a la API de OpenWeatherMap (clima actual de una ciudad). */
interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun clima(
        @Query("q") ciudad: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "es"
    ): WeatherResponse
}
