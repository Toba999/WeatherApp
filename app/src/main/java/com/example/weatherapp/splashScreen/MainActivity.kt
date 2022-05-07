package com.example.weatherapp.splashScreen

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import com.example.weatherapp.R
import com.example.weatherapp.dialog.view.FirstTimeSettings
import com.example.weatherapp.homeScreen.view.HomeActivity
import com.example.weatherapp.utility.getCurrentLocale
import kotlinx.coroutines.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private val parentJob = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isFirstTime()) {
            FirstTimeSettings().show(supportFragmentManager,"First time settings")
        } else {
            val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)
            coroutineScope.launch {
                val localLang = getCurrentLocale(this@MainActivity)
                val languageloc = com.example.weatherapp.utility.getSharedPreferences(this@MainActivity).getString(
                    getString(R.string.languageSetting), localLang?.language) ?: localLang?.language
                setLocale(languageloc!!)
                delay(4000)
                startMainScreen()
            }
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun startMainScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStop() {
        super.onStop()
        parentJob.cancel()
    }

    private fun isFirstTime(): Boolean {
        return com.example.weatherapp.utility.getSharedPreferences(this).getBoolean("firstTime", true)
    }
}