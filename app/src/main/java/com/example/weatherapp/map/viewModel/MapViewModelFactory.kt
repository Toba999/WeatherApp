package com.example.weatherapp.map.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.Repository

class MapViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(repository) as T
    }
}