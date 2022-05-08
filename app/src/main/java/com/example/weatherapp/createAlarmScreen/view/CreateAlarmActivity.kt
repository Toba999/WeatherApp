package com.example.weatherapp.createAlarmScreen.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import com.example.weatherapp.utility.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import androidx.activity.viewModels
import androidx.work.*
import com.example.weatherapp.R
import com.example.weatherapp.alarmScreen.view.AlarmActivity
import com.example.weatherapp.createAlarmScreen.viewModel.CreateAlarmViewModel
import com.example.weatherapp.createAlarmScreen.viewModel.CreateAlarmViewModelFactory
import com.example.weatherapp.databinding.ActivityCreateAlarmBinding
import com.example.weatherapp.model.Repository
import com.example.weatherapp.model.WeatherAlert
import com.example.weatherapp.utility.convertLongToDayDate
import com.example.weatherapp.utility.convertLongToTime
import java.util.*
import java.util.concurrent.TimeUnit

class CreateAlarmActivity : AppCompatActivity() {

    private val viewModel: CreateAlarmViewModel by viewModels {
        CreateAlarmViewModelFactory(Repository.getRepository(this))
    }
    private  var from: Long = 0
    private  var to: Long = 0
    private  var time: Long = 0

    private lateinit var language: String

    private lateinit var binding:ActivityCreateAlarmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        language = getSharedPreferences(this).getString(
            getString(R.string.languageSetting),
            getCurrentLocale(this)?.language
        )!!
        setInitialData()

        binding.btnFrom.setOnClickListener {
            showDatePicker(true)
        }

        binding.btnTo.setOnClickListener {
            showDatePicker(false)
        }

        binding.btnTime.setOnClickListener {
            showTimePicker()
        }

        binding.btnAlarmDone.setOnClickListener {
            viewModel.insertAlert(from,to,time)
        }

        binding.ivBackAlarm.setOnClickListener {
            startActivity(Intent(this,AlarmActivity::class.java))
        }

        viewModel.id.observe(this) {
           // setPeriodWorkManger(it)
            startActivity(Intent(this,AlarmActivity::class.java))
        }
    }


    private fun setInitialData() {
        val rightNow = Calendar.getInstance()
        // init day
        val year = rightNow.get(Calendar.YEAR)
        val month = rightNow.get(Calendar.MONTH)
        val day = rightNow.get(Calendar.DAY_OF_MONTH)
        val date = "$day/${month + 1}/$year"
        val dayNow = getDateMillis(date,language)
        val currentDate = convertLongToDayDate(dayNow, language)

        //init text
        binding.fromCreate.text = currentDate
        binding.toCreate.text = currentDate
    }

    private fun showTimePicker() {
        Locale.setDefault(Locale(language))
        val rightNow = Calendar.getInstance()
        val currentHour = rightNow.get(Calendar.HOUR_OF_DAY)
        val currentMinute = rightNow.get(Calendar.MINUTE)
        val listener: (TimePicker?, Int, Int) -> Unit = { _: TimePicker?, hour: Int, minute: Int ->
                time = (TimeUnit.MINUTES.toMillis(minute.toLong()) + TimeUnit.HOURS.toMillis(hour.toLong()))
                Log.e("time",time.toString())
                binding.timeCreate.text=convertLongToTime(time, language)
            }

        val timePickerDialog = TimePickerDialog(
            this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            listener, currentHour, currentMinute, false
        )

        timePickerDialog.setTitle(getString(R.string.time_picker))
        timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        timePickerDialog.show()
    }

    private fun showDatePicker(isFrom: Boolean) {
        Locale.setDefault(Locale(language))
        val myCalender = Calendar.getInstance()
        val year = myCalender[Calendar.YEAR]
        val month = myCalender[Calendar.MONTH]
        val day = myCalender[Calendar.DAY_OF_MONTH]
        val myDateListener =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                if (view.isShown) {
                    val date = "$day/${month + 1}/$year"
                    if (isFrom){
                        binding.fromCreate.text = date
                        from=getDateMillis(date, language)
                        Log.e("from",from.toString())
                    }else{
                        binding.toCreate.text = date
                        to=getDateMillis(date, language)
                        Log.e("to",to.toString())

                    }
                }
            }
        val datePickerDialog = DatePickerDialog(
            this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            myDateListener, year, month, day
        )
        datePickerDialog.setTitle(getString(R.string.date_picker))
        datePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        datePickerDialog.show()
    }


/*
    private fun setPeriodWorkManger(id: Long) {

        val data = Data.Builder()
        data.putLong("id", id)

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            AlertPeriodicWorkManger::class.java,
            24, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "$id",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )
    }
*/
}