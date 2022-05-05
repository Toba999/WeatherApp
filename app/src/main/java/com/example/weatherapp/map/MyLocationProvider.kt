package com.example.weatherapp.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity


class MyLocationProvider(private val activity: Activity) {
    private var myLocationList = ArrayList<Double>()
    private var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity.applicationContext)

    fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(activity.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(activity.applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(activity.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getFreshLocation()
        }else{
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),100)
        }
    }

    // for get last location
    fun isLocationEnabled(): Boolean {
        val locationManager =
            activity.application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }


    fun getFreshLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = 120000
        locationRequest.fastestInterval = 120000
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.apply {
                    requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                }
            } else {
                enableLocationSetting()
            }
        } else {
            requestPermission()
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val location = locationResult.lastLocation
            myLocationList.apply {
                add(location.latitude)
                add(location.longitude)
            }
            _locationList.postValue(myLocationList)
            stopLocationUpdates()
        }
    }

    private var _locationList = MutableLiveData<ArrayList<Double>>()
    val locationList = _locationList

    private var _denyPermission = MutableLiveData<String>()
    val denyPermission = _denyPermission

    private fun enableLocationSetting() {
        val settingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity.startActivity(settingIntent)
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

}