package com.example.weatherapp.homeScreen.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.HorizRowBinding
import com.example.weatherapp.model.Hourly
import com.example.weatherapp.utility.*

class TempPerTimeAdapter(private val context: Context) :
    RecyclerView.Adapter<TempPerTimeAdapter.ViewHolder>() {

    var hourly: List<Hourly> = emptyList()
    var temperatureUnit: String = ""
    private lateinit var language: String


    class ViewHolder(val binding: HorizRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HorizRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        language = getSharedPreferences(context).getString(context.getString(
                R.string.languageSetting
            ), "en")!!
        if(getCurrentTime() !in (morningTime + 1) until nightTime){
            holder.binding.parentHoriz.setBackgroundResource(R.color.cardColor)
        }
        val hour = hourly[position + 1]
        holder.binding.ivHorizImage.setImageResource(getIcon(hour.weather[0].icon))
        if (language == "ar") {
            holder.binding.tvHorizTemp.text =
                convertNumbersToArabic(hour.temp.toInt()).plus(temperatureUnit)
        } else {
            holder.binding.tvHorizTemp.text = "${hour.temp.toInt()}".plus(temperatureUnit)
        }
        holder.binding.tvHorizTime.text = convertLongToTime(hour.dt, language).lowercase()
    }

    override fun getItemCount(): Int {
        var size = 0
        if (hourly.isNotEmpty()) {
            size = 24
        }
        return size
    }

}