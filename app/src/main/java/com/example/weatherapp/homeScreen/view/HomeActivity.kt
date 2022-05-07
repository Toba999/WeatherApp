package com.example.weatherapp.homeScreen.view

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityHomeBinding
import com.example.weatherapp.favouriteScreen.view.FavouriteActivity
import com.example.weatherapp.homeScreen.viewModel.HomeViewModel
import com.example.weatherapp.homeScreen.viewModel.HomeViewModelFactory
import com.example.weatherapp.map.view.MapActivity
import com.example.weatherapp.model.MyLocationProvider
import com.example.weatherapp.model.Daily
import com.example.weatherapp.model.Hourly
import com.example.weatherapp.model.OpenWeatherApi
import com.example.weatherapp.model.Repository
import com.example.weatherapp.settingsScreen.SettingsActivity
import com.example.weatherapp.utility.*
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity(), ConnectivityChecker.ConnectivityReceiverListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var tempPerDayAdapter: TempPerDayAdapter
    private lateinit var tempPerTimeAdapter: TempPerTimeAdapter
    private lateinit var windSpeedUnit: String
    private lateinit var temperatureUnit: String
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var language: String = "en"
    private var units: String = "metric"
    private var flagNoConnection: Boolean = false
    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: HomeViewModelFactory
    private lateinit var dialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = ProgressDialog.setProgressDialog(this, "Loading..")
        dialog.show()

        ConnectivityChecker.connectivityReceiverListener = this

        viewModelFactory = HomeViewModelFactory(
            Repository.getRepository(this), MyLocationProvider(this)
        )
        viewModel = ViewModelProvider(this,viewModelFactory)[HomeViewModel::class.java]
        this.registerReceiver(ConnectivityChecker(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        if (!flagNoConnection) {
            if (isSharedPreferencesLocationAndTimeZoneNull(this)) {
                if (!isSharedPreferencesLatAndLongNull(this)) {
                    setValuesFromSharedPreferences()
                    try {

                        viewModel.getDataFromRemoteToLocal(
                            "$latitude",
                            "$longitude",
                            language,
                            units
                        )
                    } catch (e: Exception) {
                        val snackBar =
                            Snackbar.make(binding.root, "${e.message}", Snackbar.LENGTH_SHORT)
                        snackBar.show()
                    }
                } else if (getIsMap()) {
                    val intent = Intent(this,MapActivity::class.java)
                    intent.putExtra("isFavourite",false)
                    startActivity(intent)
                } else {
                    //dialog to get fresh location
                    val location = MyLocationProvider(this)
                    if (location.checkPermission() && location.isLocationEnabled()) {
                        viewModel.getFreshLocation()
                    }
                }
            } else {
                setValuesFromSharedPreferences()
                try {
                    viewModel.getDataFromRemoteToLocal(
                        "$latitude",
                        "$longitude",
                        language,
                        units
                    )
                } catch (e: Exception) {
                    val snackBar =
                        Snackbar.make(binding.root, "${e.message}", Snackbar.LENGTH_SHORT)
                    snackBar.show()
                }
            }
        }
        //tempPerHourAdapter
        initTimeRecyclerView()

        //tempPerDayAdapter
        initDayRecyclerView()

        viewModel.observeLocation().observe(this) {
            if (it[0] != 0.0 && it[1] != 0.0) {
                latitude = it[0]
                longitude = it[1]
                val local = getCurrentLocale(this)
                language = getSharedPreferences(this).getString(getString(R.string.languageSetting), local?.language)!!
                units = getSharedPreferences(this).getString(getString(R.string.unitsSetting), "metric")!!
                try {
                    viewModel.getDataFromRemoteToLocal(
                        "$latitude",
                        "$longitude",
                        language,
                        units
                    )
                } catch (e: Exception) {
                    val snackBar =
                        Snackbar.make(binding.root, "${e.message}", Snackbar.LENGTH_SHORT)
                    snackBar.show()
                }
            }
        }

        viewModel.openWeatherAPI.observe(this) {
            if (it != null) {
                updateSharedPreferences(
                    this,
                    it.lat,
                    it.lon,
                    getCityText(this, it.lat, it.lon, language),
                    it.timezone
                )
                setUnitSetting(units)
                setData(it)
                fetchTempPerTimeRecycler(it.hourly as ArrayList<Hourly>, temperatureUnit)
                fetchTempPerDayRecycler(it.daily as ArrayList<Daily>, temperatureUnit)
            }
        }

    }

    fun menuAction(view : View){
        val popupMenu = PopupMenu(this,view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.setting_item ->
                    startActivity(Intent(this,SettingsActivity::class.java))
                R.id.favourite_item ->
                    startActivity(Intent(this,FavouriteActivity::class.java))
                R.id.alarm_item ->
                    //Todo navigate to alarm screen
                    Toast.makeText(this, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
            }
            true
        }
        popupMenu.show()
    }



    private fun getIsMap(): Boolean {
        return getSharedPreferences(this).getBoolean(getString(R.string.isMap), false)
    }

    private fun setUnitSetting(units: String) {
        if (language == "en") {
            setEnglishUnits(units)
        } else {
            setArabicUnit(units)
        }
    }

    private fun fetchTempPerDayRecycler(daily: ArrayList<Daily>, temperatureUnit: String) {
        tempPerDayAdapter.apply {
            this.daily = daily
            this.temperatureUnit = temperatureUnit
            notifyDataSetChanged()
        }
    }

    private fun fetchTempPerTimeRecycler(hourly: ArrayList<Hourly>, temperatureUnit: String) {
        tempPerTimeAdapter.apply {
            this.hourly = hourly
            this.temperatureUnit = temperatureUnit
            notifyDataSetChanged()
        }
    }

    private fun setData(model: OpenWeatherApi) {
        dialog.dismiss()
        val weather = model.current.weather[0]
        binding.apply {
            tvDate.text = convertCalenderToDayString(Calendar.getInstance(), language)
            tvDay.text =
                convertLongToDayDate(Calendar.getInstance().timeInMillis, language)
            tvState.text = weather.description
            tvCity.text = getCityText(this@HomeActivity, model.lat, model.lon, language)
            if (language == "ar") {
                bindArabicUnits(model)
            } else {
                bindEnglishUnits(model)
            }
        }
    }

    private fun initTimeRecyclerView() {
        val tempPerTimeLinearLayoutManager = LinearLayoutManager(this)
        tempPerTimeLinearLayoutManager.orientation = RecyclerView.HORIZONTAL
        tempPerTimeAdapter = TempPerTimeAdapter(this)
        binding.rvHorizHour.layoutManager = tempPerTimeLinearLayoutManager
        binding.rvHorizHour.adapter = tempPerTimeAdapter
    }

    private fun initDayRecyclerView() {
        val tempPerDayLinearLayoutManager = LinearLayoutManager(this)
        tempPerDayAdapter = TempPerDayAdapter(this)
        binding.rvVertDay.layoutManager = tempPerDayLinearLayoutManager
        binding.rvVertDay.adapter = tempPerDayAdapter
    }

    private fun setValuesFromSharedPreferences() {
        getSharedPreferences(this).apply {
            latitude = getFloat(getString(R.string.lat), 0.0f).toDouble()
            longitude = getFloat(getString(R.string.lon), 0.0f).toDouble()
            language = getString(getString(R.string.languageSetting), "en") ?: "en"
            units = getString(getString(R.string.unitsSetting), "metric") ?: "metric"
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (binding != null) {
            if (isConnected) {
                if (flagNoConnection) {
                    val snackBar = Snackbar.make(binding.root, "Back Online", Snackbar.LENGTH_SHORT)
                    snackBar.view.setBackgroundColor(Color.GREEN)
                    snackBar.show()
                    flagNoConnection = false
                }
            } else {
                flagNoConnection = true
                val snackBar = Snackbar.make(binding.root, "You are offline", Snackbar.LENGTH_LONG)
                snackBar.view.setBackgroundColor(Color.RED)
                snackBar.show()
                getLocalData()
            }
        }
    }

    private fun getLocalData() {
        if (!isSharedPreferencesLocationAndTimeZoneNull(this)) {
            viewModel.getDataFromDatabase()
        }
    }

    private fun setArabicUnit(units: String) {
        when (units) {
            "metric" -> {
                temperatureUnit = " °م"
                windSpeedUnit = " م/ث"
            }
            "imperial" -> {
                temperatureUnit = " °ف"
                windSpeedUnit = " ميل/س"
            }
            "standard" -> {
                temperatureUnit = " °ك"
                windSpeedUnit = " م/ث"
            }
        }
    }

    private fun setEnglishUnits(units: String) {
        when (units) {
            "metric" -> {
                temperatureUnit = " °C"
                windSpeedUnit = " m/s"
            }
            "imperial" -> {
                temperatureUnit = " °F"
                windSpeedUnit = " miles/h"
            }
            "standard" -> {
                temperatureUnit = " °K"
                windSpeedUnit = " m/s"
            }
        }
    }

    private fun bindArabicUnits(model: OpenWeatherApi) {
        binding.apply {
            tvTemp.text =
                convertNumbersToArabic(model.current.temp.toInt()).plus(temperatureUnit)
            tvHuumidity.text = convertNumbersToArabic(model.current.humidity)
                .plus("٪")
            tvPressure.text = convertNumbersToArabic(model.current.pressure)
                .plus(" هب")
            tvCloud.text = convertNumbersToArabic(model.current.clouds)
                .plus("٪")
            tvUv.text = convertNumbersToArabic(model.current.uvi.toInt())
            tvWind.text =
                convertNumbersToArabic(model.current.windSpeed).plus(windSpeedUnit)
            tvAppaTemp.text=convertNumbersToArabic(model.current.temp.toInt()).plus(temperatureUnit)
        }
    }

    private fun bindEnglishUnits(model: OpenWeatherApi) {
        binding.apply {
            tvAppaTemp.text = model.current.temp.toInt().toString().plus(temperatureUnit)
            tvTemp.text = model.current.temp.toInt().toString().plus(temperatureUnit)
            tvHuumidity.text = model.current.humidity.toString().plus("%")
            tvPressure.text = model.current.pressure.toString().plus(" hPa")
            tvCloud.text = model.current.clouds.toString().plus("%")
            tvUv.text = model.current.uvi.toString()
            tvWind.text = model.current.windSpeed.toString().plus(windSpeedUnit)
        }
    }



}