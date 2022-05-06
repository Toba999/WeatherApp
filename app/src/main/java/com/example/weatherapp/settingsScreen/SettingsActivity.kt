package com.example.weatherapp.settingsScreen

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import com.example.weatherapp.utility.*
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivitySettingsBinding
import com.example.weatherapp.homeScreen.view.HomeActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var newUnitSetting: String
    private lateinit var newLanguageSetting: String
    private var newLocationSetting: Boolean = false

    private lateinit var oldUnitSetting: String
    private lateinit var oldLanguageSetting: String
    private var oldLocationSetting: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleBackButton()
        setSettings()
        binding.btnSave.setOnClickListener {
            getLocationSettings()
            getLanguagesSettings()
            getUnitSettings()
            if (newLocationSetting && oldLocationSetting) {
                changeMapLocationDialog()
            } else {
                setSettingsToSharedPreferences()
                backToHomeScreen()
            }
            val localLang = getCurrentLocale(this)
            val languageloc = getSharedPreferences(this).getString(
                getString(R.string.languageSetting), localLang?.language) ?: localLang?.language
            setLocale(languageloc!!)
        }
        binding.ivSettingsBack.setOnClickListener {
            backToHomeScreen()
        }
    }

    private fun setLocale(lang: String) {
        val myLocale = Locale(lang)
        Locale.setDefault(myLocale)
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.locale = myLocale
        conf.setLayoutDirection(myLocale)
        res.updateConfiguration(conf, dm)
    }


    private fun backToHomeScreen() {
        val refresh = Intent(this, HomeActivity::class.java)
        finish()
        startActivity(refresh)
    }

    private fun getUnitSettings() {
        when (binding.radioGroupUnits.checkedRadioButtonId) {
            R.id.radio_cms -> newUnitSetting = "metric"
            R.id.radio_fmh -> newUnitSetting = "imperial"
            R.id.radio_kms -> newUnitSetting = "standard"
        }
    }

    private fun getLanguagesSettings() {
        when (binding.radioGroupLang.checkedRadioButtonId) {
            R.id.radio_arabic -> newLanguageSetting = "ar"
            R.id.radio_english -> newLanguageSetting = "en"
        }
    }

    private fun getLocationSettings() {
        when (binding.radioGroupLoc.checkedRadioButtonId) {
            R.id.radio_setting_map -> newLocationSetting = true
            R.id.radio_setting_gps -> newLocationSetting = false
        }
    }

    private fun setSettings() {
        getSettingsFromSharedPreferences()

        when (oldUnitSetting) {
            "metric" -> binding.radioCms.isChecked = true
            "imperial" -> binding.radioFmh.isChecked = true
            "standard" -> binding.radioKms.isChecked = true
        }

        when (oldLanguageSetting) {
            "ar" -> binding.radioArabic.isChecked = true
            "en" -> binding.radioEnglish.isChecked = true
        }

        when (oldLocationSetting) {
            true -> binding.radioSettingMap.isChecked = true
            false -> binding.radioSettingGps.isChecked = true
        }

    }

    private fun getSettingsFromSharedPreferences() {
        getSharedPreferences(this).apply {
            oldUnitSetting = getString(getString(R.string.unitsSetting), "metric")!!
            oldLanguageSetting = getString(getString(R.string.languageSetting), "en")!!
            oldLocationSetting = getBoolean(getString(R.string.isMap), false)
        }
    }

    private fun setSettingsToSharedPreferences() {
        getSharedPreferences(this).edit().apply {
            putString(getString(R.string.unitsSetting), newUnitSetting)
            putString(getString(R.string.languageSetting), newLanguageSetting)
            if (newLocationSetting && !oldLocationSetting) {
                resetLocationData()
            } else if (oldLocationSetting && !newLocationSetting) {
                resetLocationData()
            }
            putBoolean(getString(R.string.isMap), newLocationSetting)
            apply()
        }
    }

    private fun resetLocationData() {
        getSharedPreferences(this).edit().apply {
            remove(getString(R.string.timeZone))
            remove(getString(R.string.location))
            remove(getString(R.string.lat))
            remove(getString(R.string.lon))
            apply()
        }
    }

    private fun handleBackButton() {
        binding.root.isFocusableInTouchMode = true
        binding.root.requestFocus()
        binding.root.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                backToHomeScreen()
                return@OnKeyListener true
            }
            false
        })
    }

    private fun changeMapLocationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.map_dialog_title))
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                setSettingsToSharedPreferences()
                backToHomeScreen()
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                resetLocationData()
                setSettingsToSharedPreferences()
                backToHomeScreen()
                dialog.dismiss()
            }
            .show()
    }

}