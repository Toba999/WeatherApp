package com.example.weatherapp.map.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MapViewModel(private val repository: Repository): ViewModel() {

    fun setFavorite(lat: String, lon: String, lang: String, units: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insertFavoriteWeatherFromRemoteToLocal(lat, lon, lang, units)
            } catch (e: Exception) {
                throw e
            }
            this.cancel()
        }
    }

}