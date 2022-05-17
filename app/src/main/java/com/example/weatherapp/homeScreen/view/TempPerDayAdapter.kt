package com.example.weatherapp.homeScreen.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.VertRowBinding
import com.example.weatherapp.model.Daily
import com.example.weatherapp.utility.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TempPerDayAdapter(private val context: Context) : RecyclerView.Adapter<TempPerDayAdapter.ViewHolder>() {

    var daily: List<Daily> = emptyList()
    var temperatureUnit: String = ""
    private lateinit var language: String

    class ViewHolder(val binding: VertRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            VertRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = daily[position + 1]
        language = getSharedPreferences(context).getString(context.getString(R.string.languageSetting), "en")!!
        if(getCurrentTime() !in (morningTime + 1) until nightTime){
            holder.binding.parentVert.setBackgroundResource(R.color.cardColor)
        }
        holder.binding.ivVertImage.setImageResource(getIcon(day.weather[0].icon))
        holder.binding.tvVertDate.text = convertLongToDay(day.dt)
        holder.binding.tvVertState.text = day.weather[0].description.toString()
        if (language == "ar") {
            holder.binding.tvVertTemp.text =
                convertNumbersToArabic(day.temp.day.toInt()).toString().plus(temperatureUnit)
        } else {
            holder.binding.tvVertTemp.text = day.temp.day.toInt().toString().plus(temperatureUnit)
        }
    }

    override fun getItemCount(): Int {
        return daily.size - 1
    }

    private fun convertLongToDay(time: Long): String {
        val date = Date(TimeUnit.SECONDS.toMillis(time))
        val format = SimpleDateFormat("EEE, d MMM yyyy", Locale(language))
        return format.format(date)
    }
}