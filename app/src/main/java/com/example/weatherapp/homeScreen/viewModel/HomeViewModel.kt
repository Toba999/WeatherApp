package com.example.weatherapp.homeScreen.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.MyLocationProvider
import com.example.weatherapp.model.OpenWeatherApi
import com.example.weatherapp.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class HomeViewModel (private val repository: RepositoryInterface,
    private val myLocationProvider: MyLocationProvider) : ViewModel() {

    private val _openWeatherAPI = MutableLiveData<OpenWeatherApi>()
    val openWeatherAPI: LiveData<OpenWeatherApi> = _openWeatherAPI

    fun getDataFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            _openWeatherAPI.postValue(
                repository.getCurrentWeatherFromLocalDataSource()
            )
        }
    }

    fun getDataFromRemoteToLocal(lat: String, long: String, language: String, units: String) {
        var result: OpenWeatherApi? = null
        viewModelScope.launch(Dispatchers.Main) {
            val job = viewModelScope.launch(Dispatchers.IO) {
                    try {
                        result =
                            repository.insertCurrentWeatherFromRemoteToLocal(
                                lat, long, language, units)

                    } catch (e: Exception) {
                        result?.let { getDataFromDatabase() }
                    }

                }
            job.join()
            result?.let { getDataFromDatabase() }
            this.cancel()
        }
    }

    fun getFreshLocation() {
        myLocationProvider.getFreshLocation()
    }

    fun observeLocation(): LiveData<ArrayList<Double>> {
        return myLocationProvider.locationList
    }


}