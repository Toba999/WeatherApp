package com.example.weatherapp.splashScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherapp.R
import com.example.weatherapp.dialog.view.FirstTimeSettings
import com.example.weatherapp.homeScreen.view.HomeActivity
import kotlinx.coroutines.*


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
                delay(4000)
                startMainScreen()
            }
        }
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