package com.example.weatherapp.favouriteScreen.view

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityFavouriteBinding
import com.example.weatherapp.favouriteScreen.viewModel.FavouriteViewModel
import com.example.weatherapp.favouriteScreen.viewModel.FavouriteViewModelFactory
import com.example.weatherapp.homeScreen.view.HomeActivity
import com.example.weatherapp.map.view.MapActivity
import com.example.weatherapp.model.OpenWeatherApi
import com.example.weatherapp.model.Repository
import com.example.weatherapp.utility.ConnectivityChecker
import com.google.android.material.snackbar.Snackbar

class FavouriteActivity : AppCompatActivity(), ConnectivityChecker.ConnectivityReceiverListener {
private lateinit var binding: ActivityFavouriteBinding
    private val viewModel: FavouriteViewModel by viewModels {
        FavouriteViewModelFactory(Repository.getRepository(this))
    }

    private lateinit var favouriteAdapter: FavouriteAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ConnectivityChecker.connectivityReceiverListener = this

        handleBackButton()

        initFavoritesRecyclerView()

        viewModel.getFavorites()

        binding.btnAddFavourite.setOnClickListener {
            val intent =Intent(this, MapActivity::class.java)
            intent.putExtra(getString(R.string.isFavourite),true)
            startActivity(intent)
        }

        binding.ivBackFavourite.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.favorites.collect {
                if (it.isNullOrEmpty()) {
                    binding.textEmptyList.visibility = View.VISIBLE
                } else {
                    binding.textEmptyList.visibility = View.GONE
                }
                fetchFavoritesRecycler(it)
            }
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (binding != null) {
            if (isConnected) {
                binding.btnAddFavourite.visibility=View.VISIBLE
                val snackBar = Snackbar.make(binding.root, "Back Online", Snackbar.LENGTH_SHORT)
                snackBar.view.setBackgroundColor(Color.GREEN)
                snackBar.show()
            } else {
                binding.btnAddFavourite.visibility=View.GONE
                val snackBar = Snackbar.make(binding.root, "You are offline", Snackbar.LENGTH_LONG)
                snackBar.view.setBackgroundColor(Color.RED)
                snackBar.show()
            }
        }
    }


    private fun fetchFavoritesRecycler(list: List<OpenWeatherApi>?) {
        favouriteAdapter.favoriteList = list ?: emptyList()
        favouriteAdapter.notifyDataSetChanged()
    }

    private fun initFavoritesRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        favouriteAdapter = FavouriteAdapter(this, viewModel)
        binding.favoriteRecyclerView.layoutManager = linearLayoutManager
        binding.favoriteRecyclerView.adapter = favouriteAdapter
    }



    private fun handleBackButton() {
        binding.root.isFocusableInTouchMode = true
        binding.root.requestFocus()
        binding.root.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
               startActivity(Intent(this,HomeActivity::class.java))
                return@OnKeyListener true
            }
            return@OnKeyListener false
        })
    }
}