package com.example.weatherapp.model

import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun insertFavoriteWeatherFromRemoteToLocal(
        lat: String, long: String, language: String = "en", units: String = "metric")

    suspend fun insertCurrentWeatherFromRemoteToLocal(
        lat: String, long: String, language: String = "en", units: String = "metric"
    ): OpenWeatherApi

    fun getCurrentWeatherFromLocalDataSource(): OpenWeatherApi

    suspend fun deleteWeathersFromLocalDataSource()

    fun getFavoritesWeatherFromLocalDataSource(): Flow<List<OpenWeatherApi>>

    suspend fun deleteFavoriteWeather(id: Int)

    fun getFavoriteWeather(id: Int): OpenWeatherApi

    suspend fun updateWeather(weather: OpenWeatherApi)

    suspend fun updateFavoriteWeather(
        latitude: String, longitude: String, units: String, language: String, id: Int): OpenWeatherApi

    suspend fun insertAlert(alert: WeatherAlert):Long

    fun getAlertsList(): Flow<List<WeatherAlert>>

    suspend fun deleteAlert(id: Int)

    fun getAlert(id: Int): WeatherAlert

}