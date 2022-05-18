package com.example.weatherapp.dialog.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.WeatherLocationProvider

class DialogSettingViewModel (private val weatherLocationProvider: WeatherLocationProvider) : ViewModel() {

    fun getFreshLocation() {
        weatherLocationProvider.getLocation()
    }

    fun observeLocation(): LiveData<ArrayList<Double>> {
        return weatherLocationProvider.locationList
    }

    fun observePermission():LiveData<String>{
        return weatherLocationProvider.denyPermission
    }

}