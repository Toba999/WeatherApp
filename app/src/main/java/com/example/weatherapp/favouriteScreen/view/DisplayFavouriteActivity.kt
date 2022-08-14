package com.example.weatherapp.favouriteScreen.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityDisplayFavouriteActivityBinding
import com.example.weatherapp.favouriteScreen.viewModel.DisplayFavouriteViewModel
import com.example.weatherapp.favouriteScreen.viewModel.DisplayFavouriteViewModelFactory
import com.example.weatherapp.homeScreen.view.TempPerDayAdapter
import com.example.weatherapp.homeScreen.view.TempPerTimeAdapter
import com.example.weatherapp.model.Daily
import com.example.weatherapp.model.Hourly
import com.example.weatherapp.model.OpenWeatherApi
import com.example.weatherapp.model.Repository
import com.example.weatherapp.utility.convertCalenderToDayString
import com.example.weatherapp.utility.convertLongToDayDate
import com.example.weatherapp.utility.convertNumbersToArabic
import com.example.weatherapp.utility.getCityText
import java.util.*
import kotlin.collections.ArrayList
import com.example.weatherapp.utility.*
import com.google.android.material.snackbar.Snackbar


class DisplayFavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDisplayFavouriteActivityBinding

    private lateinit var tempPerDayAdapter: TempPerDayAdapter
    private lateinit var tempPerTimeAdapter: TempPerTimeAdapter
    private lateinit var windSpeedUnit: String
    private lateinit var temperatureUnit: String
    private val viewModel: DisplayFavouriteViewModel by viewModels {
        DisplayFavouriteViewModelFactory(Repository.getRepository(this)) }

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var language: String = "en"
    private var units: String = "metric"
    //private lateinit var dialog: Dialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayFavouriteActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
       // dialog = ProgressDialog.setProgressDialog(this, "Loading..")
       // dialog.show()

        if(getCurrentTime() !in (morningTime + 1) until nightTime){
            binding.favParent.setBackgroundResource(R.drawable.night_sky)
        }

        handleBackButton()
        binding.ivBackDf.setOnClickListener {
            startActivity(Intent(this,FavouriteActivity::class.java))
        }

        //tempPerHourAdapter
        initTimeRecyclerView()

        //tempPerDayAdapter
        initDayRecyclerView()

        val id = intent.getIntExtra("id",0)
        if (isOnline(this)) {
            getOnlineNeeds()
            viewModel.updateWeather(latitude, longitude, units, language, id)
        } else {
            val snackBar = Snackbar.make(binding.root, "You are offline", Snackbar.LENGTH_LONG)
            snackBar.view.setBackgroundColor(Color.RED)
            snackBar.show()
            viewModel.getWeather(id)
        }
        viewModel.weather.observe(this) {
            setUnitSetting(units)
            it?.let { it1 -> setData(it1) }
            fetchTempPerTimeRecycler(it?.hourly as ArrayList<Hourly>, temperatureUnit)
            fetchTempPerDayRecycler(it.daily as ArrayList<Daily>, temperatureUnit)
        }
    }

    private fun getOnlineNeeds() {
        latitude = intent.getDoubleExtra("lat",0.0)
        longitude = intent.getDoubleExtra("lon",0.0)
        units = getSharedPreferences(this).getString(
            getString(R.string.unitsSetting), "metric")!!
        language = getSharedPreferences(this).getString(
            getString(R.string.languageSetting), "en")!!
    }

    private fun initTimeRecyclerView() {
        val tempPerTimeLinearLayoutManager = LinearLayoutManager(this)
        tempPerTimeLinearLayoutManager.orientation = RecyclerView.HORIZONTAL
        tempPerTimeAdapter = TempPerTimeAdapter(this)
        binding.rvHorizHourFav.layoutManager = tempPerTimeLinearLayoutManager
        binding.rvHorizHourFav.adapter = tempPerTimeAdapter
    }

    private fun initDayRecyclerView() {
        val tempPerDayLinearLayoutManager = LinearLayoutManager(this)
        tempPerDayAdapter = TempPerDayAdapter(this)
        binding.rvVertDayFav.layoutManager = tempPerDayLinearLayoutManager
        binding.rvVertDayFav.adapter = tempPerDayAdapter
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
        //dialog.dismiss()
        val weather = model.current?.weather?.get(0)
        binding.apply {
            tvDateFav.text = convertCalenderToDayString(Calendar.getInstance(), language)
            tvDayFav.text =
                convertLongToDayDate(Calendar.getInstance().timeInMillis, language)
            if (weather != null) {
                tvStateFav.text = weather.description
            }
            tvCityFav.text = getCityText(this@DisplayFavouriteActivity, model.lat, model.lon, language)
            if (language == "ar") {
                bindArabicUnits(model)
            } else {
                bindEnglishUnits(model)
            }
        }
    }

    private fun handleBackButton() {
        binding.root.isFocusableInTouchMode = true
        binding.root.requestFocus()
        binding.root.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                startActivity(Intent(this,FavouriteActivity::class.java))
                return@OnKeyListener true
            }
            return@OnKeyListener false
        })
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
            tvTempFav.text =
                model.current?.temp?.toInt()
                    ?.let { convertNumbersToArabic(it).plus(temperatureUnit) }
            tvHuumidityFav.text = model.current?.let {
                convertNumbersToArabic(it.humidity)
                    .plus("٪")
            }
            tvPressureFav.text = model.current?.let {
                convertNumbersToArabic(it.pressure)
                    .plus(" هب")
            }
            tvCloudFav.text = model.current?.let {
                convertNumbersToArabic(it.clouds)
                    .plus("٪")
            }
            tvUvFav.text = model.current?.uvi?.let { convertNumbersToArabic(it.toInt()) }
            tvWindFav.text =
                model.current?.let { convertNumbersToArabic(it.windSpeed).plus(windSpeedUnit) }
            tvAppaTempFav.text= model.current?.temp?.let { convertNumbersToArabic(it.toInt()).plus(temperatureUnit) }
        }
    }

    private fun bindEnglishUnits(model: OpenWeatherApi) {
        binding.apply {
            tvAppaTempFav.text = model.current?.temp?.toInt().toString().plus(temperatureUnit)
            tvTempFav.text = model.current?.temp?.toInt().toString().plus(temperatureUnit)
            tvHuumidityFav.text = model.current?.humidity.toString().plus("%")
            tvPressureFav.text = model.current?.pressure.toString().plus(" hPa")
            tvCloudFav.text = model.current?.clouds.toString().plus("%")
            tvUvFav.text = model.current?.uvi.toString()
            tvWindFav.text = model.current?.windSpeed.toString().plus(windSpeedUnit)
        }
    }
}