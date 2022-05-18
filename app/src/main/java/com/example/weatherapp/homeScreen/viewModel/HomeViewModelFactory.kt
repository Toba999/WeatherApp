package com.example.weatherapp.homeScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.WeatherLocationProvider
import com.example.weatherapp.model.RepositoryInterface

class HomeViewModelFactory (private val repository: RepositoryInterface,
    private val weatherLocationProvider: WeatherLocationProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository, weatherLocationProvider) as T
    }
}