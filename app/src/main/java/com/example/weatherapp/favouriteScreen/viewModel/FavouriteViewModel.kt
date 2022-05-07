package com.example.weatherapp.favouriteScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.OpenWeatherApi
import com.example.weatherapp.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FavouriteViewModel(private val repository: Repository) : ViewModel() {

    fun getFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFavoritesWeatherFromLocalDataSource().collect {
                _favorites.emit(it)
            }
        }
    }

    fun deleteFavoriteWeather(id: Int) {
        viewModelScope.launch {
            repository.deleteFavoriteWeather(id)
        }
    }

    private var _favorites = MutableStateFlow<List<OpenWeatherApi>>(emptyList())
    val favorites = _favorites


}