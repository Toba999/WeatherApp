package com.example.weatherapp.workManager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.R
import com.example.weatherapp.splashScreen.MainActivity
import com.example.weatherapp.utility.getIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyOneTimeWorkManger (private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    private val CHANNEL_ID = 14
    private val channel_name = "channel_name_toba"
    private val channel_description = "channel_description_toba"
    private var notificationManager: NotificationManager? = null
    var alertWindowManger: AlertWindowManger? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val description = inputData.getString("description")!!
        val icon = inputData.getString("icon")!!
        Log.e("MyOneTimeWorkManger","doWork")

        notificationChannel()
        makeNotification(description, icon)

        if (Settings.canDrawOverlays(context)) {
            GlobalScope.launch(Dispatchers.Main) {
                alertWindowManger = AlertWindowManger(context, description, icon)
                alertWindowManger!!.setMyWindowManger()
            }
        }
        return Result.success()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeNotification(description: String, icon: String){
        Log.e("MyOneTimeWorkManger","makeNotification")
        lateinit var builder: Notification.Builder

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val bitmap = BitmapFactory.decodeResource(context.resources, getIcon(icon))

        builder=Notification.Builder(applicationContext, "$CHANNEL_ID")
            .setSmallIcon(getIcon(icon))
            .setContentText(description)
            .setContentTitle("Weather Alarm")
            .setLargeIcon(bitmap)
            .setPriority(Notification.PRIORITY_DEFAULT)
            .setStyle(
                Notification.BigTextStyle()
                    .bigText(description)
            )
            //.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.notif_ring))
            .setAutoCancel(true)
        notificationManager?.notify(1234, builder.build())

    }

    private fun notificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("$CHANNEL_ID", channel_name, NotificationManager.IMPORTANCE_DEFAULT)
            val sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.notif_ring)
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            channel.enableVibration(true)
            //channel.setSound(sound, attributes)
            channel.description = channel_description
            notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
            Log.e("MyOneTimeWorkManger","notificationChannel")

        }
    }
}
