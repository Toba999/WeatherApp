package com.example.weatherapp.workManager

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.WeatherAlert
import com.example.weatherapp.utility.checkTime
import java.util.*
import java.util.concurrent.TimeUnit

class MyPeriodicManager (private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    val repository = Repository.getRepository(context)

    override suspend fun doWork(): Result {
        if (!isStopped) {
            val data = inputData
            val id = data.getLong("id", 0)
            getCurrentData(id.toInt())
        }
        return Result.success()
    }

    private suspend fun getCurrentData(id: Int) {
        val currentWeather = repository.getCurrentWeatherFromLocalDataSource()
        Log.e("MyPeriodicManager","getCurrentData")
        val alert = repository.getAlert(id)
        if (checkTime(alert,context)) {
            val delay: Long = getDelay(alert)
            if (currentWeather.alerts.isNullOrEmpty()) {
                Log.e("MyPeriodicManager","isNullOrEmpty "+delay.toString())
                currentWeather.current?.weather?.get(0)?.let {
                    setOneTimeWorkManger(
                        delay,
                        alert.id,
                        it.description,
                        currentWeather.current!!.weather[0].icon
                    )
                }
            } else {
                currentWeather.current?.weather?.get(0)?.let {
                    setOneTimeWorkManger(
                        delay,
                        alert.id,
                        currentWeather.alerts!![0].tags[0],
                        it.icon
                    )
                }
            }
        } else {
            repository.deleteAlert(id)
            WorkManager.getInstance().cancelUniqueWork("$id")

        }
    }

    private fun setOneTimeWorkManger(delay: Long, id: Int?, description: String, icon: String) {
        val data = Data.Builder()
        data.putString("description", description)
        data.putString("icon", icon)
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyOneTimeWorkManger::class.java,)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "$id",
            ExistingWorkPolicy.REPLACE,
            oneTimeWorkRequest
        )
    }

    private fun getDelay(alert: WeatherAlert): Long {
        val hour =
            TimeUnit.HOURS.toMillis(Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toLong())
        val minute =
            TimeUnit.MINUTES.toMillis(Calendar.getInstance().get(Calendar.MINUTE).toLong())
        Log.e("getDelay",(hour + minute).toString()+" "+alert.time.toString())

        return alert.time - ((hour + minute))
    }



}