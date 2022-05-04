package com.example.weatherapp.model

import android.content.Context
import com.example.weatherapp.localDataSource.LocalSource
import com.example.weatherapp.localDataSource.RoomLocalClass
import com.example.weatherapp.localDataSource.WeatherDatabase
import com.example.weatherapp.network.RemoteSource
import com.example.weatherapp.network.RetrofitHelper
import kotlinx.coroutines.flow.Flow

class Repository(
    private val weatherRemoteDataSource: RemoteSource, private val weatherLocalDataSource: LocalSource,
) : RepositoryInterface {

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getRepository(context: Context): Repository {
            return INSTANCE ?: synchronized(this) {
                Repository(
                    RetrofitHelper,
                    RoomLocalClass(WeatherDatabase.getDatabase(context).weatherDao())
                ).also {
                    INSTANCE = it
                }
            }
        }
    }

    override suspend fun insertFavoriteWeatherFromRemoteToLocal(
        lat: String, long: String, language: String, units: String) {
        val remoteWeather = weatherRemoteDataSource.getCurrentWeather(lat, long, language, units)
        if (remoteWeather.isSuccessful) {
            remoteWeather.body()?.let {
                it.isFavorite = true
                weatherLocalDataSource.insertCurrentWeather(it)
            }
        } else {
            throw Exception("${remoteWeather.errorBody()}")
        }
    }

    override suspend fun insertCurrentWeatherFromRemoteToLocal(
        lat: String, long: String, language: String, units: String): OpenWeatherApi {
        val remoteWeather = weatherRemoteDataSource.getCurrentWeather(lat, long, language, units)
        return if (remoteWeather.isSuccessful) {
            remoteWeather.body()?.let {
                deleteWeathersFromLocalDataSource()
                it.isFavorite = false
                weatherLocalDataSource.insertCurrentWeather(it)
            }
            remoteWeather.body()!!
        } else {
            throw Exception("${remoteWeather.errorBody()}")
        }
    }

    override fun getCurrentWeatherFromLocalDataSource(): OpenWeatherApi {
        return weatherLocalDataSource.getCurrentWeather()
    }

    override suspend fun deleteWeathersFromLocalDataSource() {
        weatherLocalDataSource.deleteWeathers()
    }

    override fun getFavoritesWeatherFromLocalDataSource(): Flow<List<OpenWeatherApi>> {
        return weatherLocalDataSource.getFavoritesWeather()
    }

    override suspend fun deleteFavoriteWeather(id: Int) {
        weatherLocalDataSource.deleteFavoriteWeather(id)
    }

    override fun getFavoriteWeather(id: Int): OpenWeatherApi {
        return weatherLocalDataSource.getFavoriteWeather(id)
    }

    override suspend fun updateWeather(weather: OpenWeatherApi) {
        weatherLocalDataSource.updateWeather(weather)
    }

    override suspend fun updateFavoriteWeather(
        latitude: String, longitude: String, units: String, language: String, id: Int
    ): OpenWeatherApi {
        val response =
            weatherRemoteDataSource.getCurrentWeather(latitude, longitude, language, units)
        if (response.isSuccessful) {
            response.body()?.let {
                it.id = id
                it.isFavorite = true
                updateWeather(it)
            }
            return getFavoriteWeather(id)
        } else {
            throw Exception("${response.errorBody()}")
        }
    }

    override suspend fun insertAlert(alert: WeatherAlert): Long {
        return weatherLocalDataSource.insertAlert(alert)
    }

    override fun getAlertsList(): Flow<List<WeatherAlert>> {
        return weatherLocalDataSource.getAlertsList()
    }

    override suspend fun deleteAlert(id: Int) {
        weatherLocalDataSource.deleteAlert(id)
    }

    override fun getAlert(id: Int): WeatherAlert {
        return weatherLocalDataSource.getAlert(id)
    }

}