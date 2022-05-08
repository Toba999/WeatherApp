package com.example.weatherapp.map.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import com.example.weatherapp.utility.*
import android.view.View
import androidx.activity.viewModels
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMapBinding
import com.example.weatherapp.favouriteScreen.view.FavouriteActivity
import com.example.weatherapp.homeScreen.view.HomeActivity
import com.example.weatherapp.map.viewModel.MapViewModel
import com.example.weatherapp.map.viewModel.MapViewModelFactory
import com.example.weatherapp.model.Repository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class MapActivity : AppCompatActivity() {
    private var lat = 30.044
    private var lon = 31.235
    private lateinit var binding : ActivityMapBinding
    private var isFavorite: Boolean = true
    private val viewModel: MapViewModel by viewModels {
        MapViewModelFactory(Repository.getRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleBackButton()

        isFavorite = intent.getBooleanExtra(getString(R.string.isFavourite),false)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        handleBackButton()
        binding.btnDone.setOnClickListener {
            if (isFavorite) {
                navigateToFavoriteScreen(lat, lon)
            } else {
                saveLocationInSharedPreferences(lon, lat)
            }
        }
    }


    private val callback = OnMapReadyCallback { googleMap ->

        val cairo = LatLng(lat, lon)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cairo, 10.0f))
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.setOnMapClickListener { location ->
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(location))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f))
            lat = location.latitude
            lon = location.longitude
            binding.btnDone.visibility = View.VISIBLE
        }
    }


    private fun navigateToFavoriteScreen(lat: Double, lon: Double) {
        val language = getSharedPreferences(this).getString(getString(R.string.languageSetting), "en")
        val units = getSharedPreferences(this).getString(getString(R.string.unitsSetting), "metric")
        try {
            viewModel.setFavorite("$lat", "$lon", language!!, units!!)
            startActivity(Intent(this, FavouriteActivity::class.java))
        } catch (e: Exception) {
            val snackBar = Snackbar.make(binding.root, "${e.message}", Snackbar.LENGTH_SHORT)
            snackBar.show()
        }
    }

    private fun saveLocationInSharedPreferences(long: Double, lat: Double) {
        val editor = getSharedPreferences(this).edit()
        editor.putFloat(getString(R.string.lat), lat.toFloat())
        editor.putFloat(getString(R.string.lon), long.toFloat())
        editor.apply()
        startActivity(Intent(this,HomeActivity::class.java))
    }

    private fun handleBackButton() {
        binding.root.isFocusableInTouchMode = true
        binding.root.requestFocus()
        binding.root.setOnKeyListener(View.OnKeyListener { view, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                if (isFavorite) {
                    startActivity(Intent(this, FavouriteActivity::class.java))

                } else {
                    startActivity(Intent(this,HomeActivity::class.java))
                }
                return@OnKeyListener true
            }
            false
        })
    }
}