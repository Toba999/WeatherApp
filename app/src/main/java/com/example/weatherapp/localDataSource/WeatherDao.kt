package com.example.weatherapp.localDataSource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.example.weatherapp.model.OpenWeatherApi
import com.example.weatherapp.model.WeatherAlert
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("select * from weather where isFavorite = 0")
    fun getCurrentWeather(): OpenWeatherApi

    //insert obj and return auto generated data base
    @Insert(onConflict = REPLACE)
    suspend fun insertWeather(weather: OpenWeatherApi): Long

    @Update
    suspend fun updateWeather(weather: OpenWeatherApi)

    @Query("DELETE FROM weather where isFavorite = 0")
    suspend fun deleteCurrentWeather()

    @Query("select * from weather where isFavorite = 1")
    fun getFavoritesWeather(): Flow<List<OpenWeatherApi>>

    @Query("DELETE FROM weather where id = :id")
    suspend fun deleteFavoriteWeather(id: Int)

    @Query("select * from weather where id = :id")
    fun getFavoriteWeather(id: Int): OpenWeatherApi

    @Insert(onConflict = REPLACE)
    suspend fun insertAlert(alert: WeatherAlert): Long

    @Query("select * from alert")
    fun getAlertsList(): Flow<List<WeatherAlert>>

    @Query("DELETE FROM alert where id = :id")
    suspend fun deleteAlert(id: Int)

    @Query("select * from alert where id = :id")
    fun getAlert(id: Int): WeatherAlert
}