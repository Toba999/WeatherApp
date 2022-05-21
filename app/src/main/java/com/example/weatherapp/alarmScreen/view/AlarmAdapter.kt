package com.example.weatherapp.alarmScreen.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.example.weatherapp.R
import com.example.weatherapp.alarmScreen.viewModel.AlarmViewModel
import com.example.weatherapp.databinding.AlarmRowBinding
import com.example.weatherapp.model.WeatherAlert
import com.example.weatherapp.utility.*

class AlarmAdapter(private val context: Context, private val viewModel: AlarmViewModel) :
    RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {

    var alertsList: List<WeatherAlert> = emptyList()

    class ViewHolder(val binding: AlarmRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return AlarmAdapter.ViewHolder(
            AlarmRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val language: String = getSharedPreferences(context).getString(
            context.getString(R.string.languageSetting), getCurrentLocale(context)?.language)!!
        val alert = alertsList[position]

        holder.binding.btnDelete.setOnClickListener {
            showDeleteDialog(alert.id!!)
        }
        holder.binding.textFrom.text = convertLongToDayDate(alert.startDate, language)

        holder.binding.textTo.text = convertLongToDayDate(alert.endDate, language)

        holder.binding.textTime.text = convertLongToTimePicker(alert.time, language)
    }

    override fun getItemCount(): Int {
        return alertsList.size
    }
    private fun showDeleteDialog(alertId:Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete this item")
        builder.setIcon(R.drawable.ic_baseline_error_24)
        builder.setPositiveButton("Yes") { dialog, id ->
            viewModel.deleteAlert(alertId)
            WorkManager.getInstance().cancelAllWorkByTag("${alertId}")
            dialog.cancel()
        }
        builder.setNeutralButton("Cancel") { dialog, id ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}