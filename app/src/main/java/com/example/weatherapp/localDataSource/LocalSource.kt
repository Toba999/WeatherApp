package com.example.weatherapp.localDataSource

import com.example.weatherapp.model.OpenWeatherApi
import com.example.weatherapp.model.WeatherAlert
import kotlinx.coroutines.flow.Flow

interface LocalSource {

    fun getCurrentWeather(): OpenWeatherApi

    suspend fun insertCurrentWeather(weather: OpenWeatherApi):Long

    suspend fun updateWeather(weather: OpenWeatherApi)

    suspend fun deleteWeathers()

    fun getFavoritesWeather(): Flow<List<OpenWeatherApi>>

    suspend fun deleteFavoriteWeather(id:Int)

    fun getFavoriteWeather(id:Int): OpenWeatherApi

    suspend fun insertAlert(alert: WeatherAlert):Long

    fun getAlertsList(): Flow<List<WeatherAlert>>

    suspend fun deleteAlert(id: Int)

    fun getAlert(id: Int): WeatherAlert

}