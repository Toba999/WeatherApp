package com.example.weatherapp.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "weather")
data class OpenWeatherApi(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Int,
    var isFavorite: Boolean = false,
    @SerializedName("lat") var lat: Double,
    @SerializedName("lon") var lon: Double,
    @SerializedName("timezone") var timezone: String,
    @SerializedName("timezone_offset") var timezoneOffset: Int,
    @SerializedName("current") var current: Current,
    @SerializedName("hourly") var hourly: List<Hourly>,
    @SerializedName("daily") var daily: List<Daily>,
    @SerializedName("alerts") var alerts: List<Alerts>?
)

data class Weather(
    @SerializedName("id") var id: Int,
    @SerializedName("main") var main: String,
    @SerializedName("description") var description: String,
    @SerializedName("icon") var icon: String
)

data class Current(
    @SerializedName("dt") var dt: Long,
    @SerializedName("sunrise") var sunrise: Int,
    @SerializedName("sunset") var sunset: Int,
    @SerializedName("temp") var temp: Double,
    @SerializedName("feels_like") var feelsLike: Double,
    @SerializedName("pressure") var pressure: Int,
    @SerializedName("humidity") var humidity: Int,
    @SerializedName("dew_point") var dewPoint: Double,
    @SerializedName("uvi") var uvi: Double,
    @SerializedName("clouds") var clouds: Int,
    @SerializedName("visibility") var visibility: Int,
    @SerializedName("wind_speed") var windSpeed: Double,
    @SerializedName("wind_deg") var windDeg: Int,
    @SerializedName("wind_gust") var windGust: Double,
    @SerializedName("weather") var weather: List<Weather>
)

data class Hourly(

    @SerializedName("dt") var dt: Long,
    @SerializedName("temp") var temp: Double,
    @SerializedName("feels_like") var feelsLike: Double,
    @SerializedName("pressure") var pressure: Int,
    @SerializedName("humidity") var humidity: Int,
    @SerializedName("dew_point") var dewPoint: Double,
    @SerializedName("uvi") var uvi: Double,
    @SerializedName("clouds") var clouds: Int,
    @SerializedName("visibility") var visibility: Int,
    @SerializedName("wind_speed") var windSpeed: Double,
    @SerializedName("wind_deg") var windDeg: Int,
    @SerializedName("wind_gust") var windGust: Double,
    @SerializedName("weather") var weather: List<Weather>,
    @SerializedName("pop") var pop: Double

)

data class Temp(

    @SerializedName("day") var day: Double,
    @SerializedName("min") var min: Double,
    @SerializedName("max") var max: Double,
    @SerializedName("night") var night: Double,
    @SerializedName("eve") var eve: Double,
    @SerializedName("morn") var morn: Double

)

data class FeelsLike(

    @SerializedName("day") var day: Double,
    @SerializedName("night") var night: Double,
    @SerializedName("eve") var eve: Double,
    @SerializedName("morn") var morn: Double

)

data class Daily(

    @SerializedName("dt") var dt: Long,
    @SerializedName("sunrise") var sunrise: Int,
    @SerializedName("sunset") var sunset: Int,
    @SerializedName("moonrise") var moonrise: Int,
    @SerializedName("moonset") var moonset: Int,
    @SerializedName("moon_phase") var moonPhase: Double,
    @SerializedName("temp") var temp: Temp,
    @SerializedName("feels_like") var feelsLike: FeelsLike,
    @SerializedName("pressure") var pressure: Int,
    @SerializedName("humidity") var humidity: Int,
    @SerializedName("dew_point") var dewPoint: Double,
    @SerializedName("wind_speed") var windSpeed: Double,
    @SerializedName("wind_deg") var windDeg: Int,
    @SerializedName("wind_gust") var windGust: Double,
    @SerializedName("weather") var weather: List<Weather>,
    @SerializedName("clouds") var clouds: Int,
    @SerializedName("pop") var pop: Double,
    @SerializedName("uvi") var uvi: Double

)

data class Alerts(

    @SerializedName("sender_name") var senderName: String? = null,
    @SerializedName("event") var event: String? = null,
    @SerializedName("start") var start: Long? = null,
    @SerializedName("end") var end: Long? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("tags") var tags: List<String>

)

@Entity(tableName = "alert")
data class WeatherAlert(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    val id: Int? = null,
    var time: Long,
    var startDate: Long,
    var endDate: Long
)

