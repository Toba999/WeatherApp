package com.example.weatherapp.favouriteScreen.view

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FavouriteRowBinding
import com.example.weatherapp.favouriteScreen.viewModel.FavouriteViewModel
import com.example.weatherapp.model.OpenWeatherApi
import com.example.weatherapp.utility.*


class FavouriteAdapter (private val context: Context, private val viewModel: FavouriteViewModel) :
    RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {
    var favoriteList: List<OpenWeatherApi> = emptyList()

    class ViewHolder(val binding: FavouriteRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FavouriteRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favorite = favoriteList[position]
        val language = getSharedPreferences(context).getString(
            context.getString(R.string.languageSetting), "en")!!
        holder.binding.textFavoriteCountry.text =
            getCityText(context, favorite.lat, favorite.lon, language)
        holder.binding.btnDelete.setOnClickListener {
            showDeleteDialog(favorite.id)
        }
        holder.binding.favoriteCardView.setOnClickListener {
            val intent=Intent(context,DisplayFavouriteActivity::class.java)
            intent.putExtra("id",favorite.id)
            intent.putExtra("lat",favorite.lat)
            intent.putExtra("lon",favorite.lon)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return favoriteList.size
    }

    private fun showDeleteDialog(favId:Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete this item")
        builder.setIcon(R.drawable.ic_baseline_error_24)
        builder.setPositiveButton("Yes") { dialog, id ->
            viewModel.deleteFavoriteWeather(favId)
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