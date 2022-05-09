package com.example.weatherapp.alarmScreen.view

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.example.weatherapp.utility.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.alarmScreen.viewModel.AlarmViewModel
import com.example.weatherapp.alarmScreen.viewModel.AlarmViewModelFactory
import com.example.weatherapp.createAlarmScreen.view.CreateAlarmActivity
import com.example.weatherapp.databinding.ActivityAlarmBinding
import com.example.weatherapp.homeScreen.view.HomeActivity
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.WeatherAlert
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmBinding

    private val viewModel: AlarmViewModel by viewModels {
        AlarmViewModelFactory(Repository.getRepository(this))
    }

    private lateinit var alertsAdapter: AlarmAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backToHomeScreen()

        initFavoritesRecyclerView()

        binding.ivBackAlarm.setOnClickListener {
            startActivity(Intent(this,HomeActivity::class.java))
        }

        binding.btnAddAlert.setOnClickListener {
            if (checkFirstTime()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkDrawOverlayPermission()
                    setNotFirstTime()
                } else {
                    navigateToCreateAlarmDialog()
                }
            } else {
                navigateToCreateAlarmDialog()
            }
        }

        viewModel.getAlertsList()

        lifecycleScope.launchWhenStarted {
            viewModel.alerts.collect {
                if (!it.isNullOrEmpty()) {
                    binding.textEmptyAlert.visibility = View.GONE
                } else {
                    binding.textEmptyAlert.visibility = View.VISIBLE
                }
                fetchAlertsRecycler(it)
            }
        }

    }

    private fun navigateToCreateAlarmDialog() {
        startActivity(Intent(this,CreateAlarmActivity::class.java))
    }

    private fun setNotFirstTime() {
        getSharedPreferences(this).edit().putBoolean("permission", false).apply()
    }

    private fun checkFirstTime(): Boolean {
        return getSharedPreferences(this).getBoolean("permission", true)
    }

    private fun fetchAlertsRecycler(list: List<WeatherAlert>?) {
        alertsAdapter.alertsList = list ?: emptyList()
        alertsAdapter.notifyDataSetChanged()
    }

    private fun initFavoritesRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        alertsAdapter = AlarmAdapter(this, viewModel)
        binding.alertRecyclerView.layoutManager = linearLayoutManager
        binding.alertRecyclerView.adapter = alertsAdapter
    }



    private fun backToHomeScreen() {
        binding.root.isFocusableInTouchMode = true
        binding.root.requestFocus()
        binding.root.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
               startActivity(Intent(this,HomeActivity::class.java))
                return@OnKeyListener true
            }
            false
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkDrawOverlayPermission() {
        // Check if we already  have permission to draw over other apps
        if (!Settings.canDrawOverlays(this)) {
            // if not construct intent to request permission
            val alertDialogBuilder = MaterialAlertDialogBuilder(this)
            alertDialogBuilder.setTitle(getString(R.string.overlay_title))
                .setMessage(getString(R.string.overlay_message))
                .setPositiveButton(getString(R.string.overlay_postive_button)) { dialog: DialogInterface, _: Int ->
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + this.packageName)
                    )
                    // request permission via start activity for result
                    startActivityForResult(intent, 1)
                    //It will call onActivityResult Function After you press Yes/No and go Back after giving permission
                    dialog.dismiss()

                }.setNegativeButton(
                    getString(R.string.overlay_negative_button)
                ) { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                    navigateToCreateAlarmDialog()
                }.show()
        }
    }
}