package com.example.weatherapp.dialog.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.MyLocationProvider

class DialogSettingViewModel (private val myLocationProvider: MyLocationProvider) : ViewModel() {

    fun getFreshLocation() {
        myLocationProvider.getFreshLocation()
    }

    fun observeLocation(): LiveData<ArrayList<Double>> {
        return myLocationProvider.locationList
    }

    fun observePermission():LiveData<String>{
        return myLocationProvider.denyPermission
    }

}