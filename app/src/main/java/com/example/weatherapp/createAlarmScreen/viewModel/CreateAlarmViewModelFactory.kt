package com.example.weatherapp.createAlarmScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.Repository

class CreateAlarmViewModelFactory (private val repository: Repository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateAlarmViewModel(repository) as T
    }
}