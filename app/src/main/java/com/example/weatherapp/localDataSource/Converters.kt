package com.example.weatherapp.localDataSource

import androidx.room.TypeConverter
import com.example.weatherapp.model.*
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun currentToJson(current: Current?) = Gson().toJson(current)

    @TypeConverter
    fun jsonToCurrent(currentString: String) =
        Gson().fromJson(currentString, Current::class.java)

    @TypeConverter
    fun hourlyListToJson(hourlyList: List<Hourly>?) = Gson().toJson(hourlyList)

    @TypeConverter
    fun jsonToHourlyList(hourlyString: String) =
        Gson().fromJson(hourlyString, Array<Hourly>::class.java)?.toList()

    @TypeConverter
    fun dailyListToJson(dailyList: List<Daily>) = Gson().toJson(dailyList)

    @TypeConverter
    fun jsonToDailyList(dailyString: String) =
        Gson().fromJson(dailyString, Array<Daily>::class.java).toList()

    @TypeConverter
    fun weatherListToJson(weatherList: List<Weather>) = Gson().toJson(weatherList)

    @TypeConverter
    fun jsonToWeatherList(weatherString: String) =
        Gson().fromJson(weatherString, Array<Weather>::class.java).toList()

    @TypeConverter
    fun alertListToJson(alertList: List<Alerts>?) = Gson().toJson(alertList)

    @TypeConverter
    fun jsonToAlertList(alertString: String?): List<Alerts>? {
        alertString?.let {
            return Gson().fromJson(alertString, Array<Alerts>::class.java)?.toList()
        }
        return emptyList()
    }
}