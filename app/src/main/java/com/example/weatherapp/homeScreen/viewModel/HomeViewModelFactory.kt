package com.example.weatherapp.homeScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.MyLocationProvider
import com.example.weatherapp.model.RepositoryInterface

class HomeViewModelFactory (private val repository: RepositoryInterface,
    private val myLocationProvider: MyLocationProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository, myLocationProvider) as T
    }
}