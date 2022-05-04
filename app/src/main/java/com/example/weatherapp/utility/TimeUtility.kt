package com.example.weatherapp.utility

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.weatherapp.R
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


fun getIcon(imageString: String): Int {
    val imageInInteger: Int
    when (imageString) {
        "01d" -> imageInInteger = R.drawable.icon_01d
        "01n" -> imageInInteger = R.drawable.icon_01n
        "02d" -> imageInInteger = R.drawable.icon_02d
        "02n" -> imageInInteger = R.drawable.icon_02n
        "03n" -> imageInInteger = R.drawable.icon_03n
        "03d" -> imageInInteger = R.drawable.icon_03d
        "04d" -> imageInInteger = R.drawable.icon_04d
        "04n" -> imageInInteger = R.drawable.icon_04n
        "09d" -> imageInInteger = R.drawable.icon_09d
        "09n" -> imageInInteger = R.drawable.icon_09n
        "10d" -> imageInInteger = R.drawable.icon_10d
        "10n" -> imageInInteger = R.drawable.icon_10n
        "11d" -> imageInInteger = R.drawable.icon_11d
        "11n" -> imageInInteger = R.drawable.icon_11n
        "13d" -> imageInInteger = R.drawable.icon_13d
        "13n" -> imageInInteger = R.drawable.icon_13n
        "50d" -> imageInInteger = R.drawable.icon_50d
        "50n" -> imageInInteger = R.drawable.icon_50n
        else -> imageInInteger = R.drawable.icon_50n
    }
    return imageInInteger
}

fun convertLongToTime(time: Long, language: String): String {
    val date = Date(TimeUnit.SECONDS.toMillis(time))
    val format = SimpleDateFormat("h:mm a", Locale(language))
    return format.format(date)
}

fun convertCalenderToDayString(calendar: Calendar, language: String): String {
    return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale(language))
}

fun convertLongToDayDate(time: Long, language: String): String {
    val date = Date(time)
    val format = SimpleDateFormat("d MMM, yyyy", Locale(language))
    return format.format(date)
}

fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(
        context.getString(R.string.shared_pref),
        Context.MODE_PRIVATE
    )
}

fun isSharedPreferencesLocationAndTimeZoneNull(context: Context): Boolean {
    val myPref = getSharedPreferences(context)
    val location = myPref.getString(context.getString(R.string.location), null)
    val timeZone = myPref.getString(context.getString(R.string.timeZone), null)
    return location.isNullOrEmpty() && timeZone.isNullOrEmpty()
}

fun isSharedPreferencesLatAndLongNull(context: Context): Boolean {
    val myPref = getSharedPreferences(context)
    val lat = myPref.getFloat(context.getString(R.string.lat), 0.0f)
    val long = myPref.getFloat(context.getString(R.string.lon), 0.0f)
    return lat == 0.0f && long == 0.0f
}

fun updateSharedPreferences(context: Context,lat: Double,long: Double,location: String, timeZone: String) {
    val editor = getSharedPreferences(context).edit()
    editor.putFloat(context.getString(R.string.lat), lat.toFloat())
    editor.putFloat(context.getString(R.string.lon), long.toFloat())
    editor.putString(context.getString(R.string.location), location)
    editor.putString(context.getString(R.string.timeZone), timeZone)
    editor.apply()
}

fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}

fun getCurrentLocale(context: Context): Locale? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales[0]
    } else {
        context.resources.configuration.locale
    }
}

fun getCityText(context: Context, lat: Double, lon: Double, language: String): String {
    var city = "Unknown!"
    val geocoder = Geocoder(context, Locale(language))
    try {
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        if (addresses.isNotEmpty()) {
            city = "${addresses[0].adminArea}, ${addresses[0].countryName}"
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return city
}


fun convertNumbersToArabic(value: Double): String {
    return (value.toString() + "")
        .replace("1".toRegex(), "١").replace("2".toRegex(), "٢")
        .replace("3".toRegex(), "٣").replace("4".toRegex(), "٤")
        .replace("5".toRegex(), "٥").replace("6".toRegex(), "٦")
        .replace("7".toRegex(), "٧").replace("8".toRegex(), "٨")
        .replace("9".toRegex(), "٩").replace("0".toRegex(), "٠")
}

fun convertNumbersToArabic(value: Int): String {
    return (value.toString() + "")
        .replace("1".toRegex(), "١").replace("2".toRegex(), "٢")
        .replace("3".toRegex(), "٣").replace("4".toRegex(), "٤")
        .replace("5".toRegex(), "٥").replace("6".toRegex(), "٦")
        .replace("7".toRegex(), "٧").replace("8".toRegex(), "٨")
        .replace("9".toRegex(), "٩").replace("0".toRegex(), "٠")
}


