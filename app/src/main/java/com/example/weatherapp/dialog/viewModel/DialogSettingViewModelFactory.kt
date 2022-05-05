package com.example.weatherapp.dialog.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.map.MyLocationProvider

class DialogSettingViewModelFactory (private val myLocationProvider: MyLocationProvider) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DialogSettingViewModel(myLocationProvider) as T
    }
}