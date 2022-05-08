package com.example.weatherapp.createAlarmScreen.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.WeatherAlert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateAlarmViewModel (private val repository: Repository) : ViewModel() {

    fun insertAlert(from: Long,to:Long,time:Long) {
        var response: Long? = null
        val alert =WeatherAlert(null,time,from,to)
        viewModelScope.launch(Dispatchers.IO) {
            val job = viewModelScope.launch(Dispatchers.IO) {
                response = repository.insertAlert(alert)
            }
            job.join()
            if (response != null) {
                _id.postValue(response!!)
            }
        }
    }

    private var _id = MutableLiveData<Long>()
    val id = _id
}