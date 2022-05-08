package com.example.weatherapp.alarmScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.WeatherAlert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AlarmViewModel (private val repository: Repository) : ViewModel() {

    private var _alerts = MutableStateFlow<List<WeatherAlert>>(emptyList())
    val alerts = _alerts

    fun getAlertsList() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAlertsList().collect {
                _alerts.emit(it)
            }
        }
    }

    fun deleteAlert(id: Int) {
        viewModelScope.launch {
            repository.deleteAlert(id)
        }
    }
}