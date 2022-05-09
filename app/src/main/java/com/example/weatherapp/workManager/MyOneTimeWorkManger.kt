package com.example.weatherapp.workManager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service.START_NOT_STICKY
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.R
import com.example.weatherapp.splashScreen.MainActivity
import com.example.weatherapp.utility.getIcon

class MyOneTimeWorkManger (private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val description = inputData.getString("description")!!
        val icon = inputData.getString("icon")!!
        startAlertService(description, icon)
        return Result.success()
    }

    private fun startAlertService(description: String, icon: String) {
        val intent = Intent(applicationContext, AlertService::class.java)
        intent.putExtra("description", description)
        intent.putExtra("icon", icon)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(applicationContext, intent)
        } else {
            applicationContext.startService(intent)
        }
    }
}