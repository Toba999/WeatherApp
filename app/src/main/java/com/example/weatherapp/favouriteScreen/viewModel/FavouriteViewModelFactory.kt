package com.example.weatherapp.favouriteScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.Repository

class FavouriteViewModelFactory (private val repository: Repository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteViewModel(repository) as T
    }
}