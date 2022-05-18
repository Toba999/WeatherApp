package com.example.weatherapp.dialog.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.WeatherLocationProvider

class DialogSettingViewModelFactory (private val weatherLocationProvider: WeatherLocationProvider) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DialogSettingViewModel(weatherLocationProvider) as T
    }
}