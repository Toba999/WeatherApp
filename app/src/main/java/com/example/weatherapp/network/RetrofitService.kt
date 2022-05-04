package com.example.weatherapp.network

import com.example.weatherapp.model.OpenWeatherApi
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val appId = "0d33c9075af75cf59cf139b160459b05"
private const val excludeMinutely = "minutely"

interface RetrofitService {
    @GET("onecall")
    suspend fun getCurrentWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("exclude") exclude: String = excludeMinutely,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("appid") app_id: String = appId
    ): Response<OpenWeatherApi>
}