package com.immr.weatherapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.immr.weatherapp.databinding.ItemsListWeatherBinding
import com.immr.weatherapp.ui.model.DataWeatherResponse
import java.text.SimpleDateFormat
import java.util.*

class ListWeatherAdapter(data: List<DataWeatherResponse.ListElement>, context: Context) :
    ListAdapter<DataWeatherResponse.ListElement, WeatherViewHolder>(ItemDiffCallback) {
    private val mData: List<DataWeatherResponse.ListElement> = data
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private val mContext: Context = context
    var onClickData: ((DataWeatherResponse.ListElement) -> Unit)? = null

    companion object ItemDiffCallback : DiffUtil.ItemCallback<DataWeatherResponse.ListElement>() {
        override fun areItemsTheSame(
            oldItem: DataWeatherResponse.ListElement,
            newItem: DataWeatherResponse.ListElement
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: DataWeatherResponse.ListElement,
            newItem: DataWeatherResponse.ListElement
        ): Boolean {
            return oldItem.dt == newItem.dt
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder(ItemsListWeatherBinding.inflate(mInflater, parent, false))
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(mData[position], mContext)
        holder.itemView.setOnClickListener {
            onClickData?.invoke(mData[position])
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}

class WeatherViewHolder(private val binding: ItemsListWeatherBinding) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(item: DataWeatherResponse.ListElement, context: Context) {
        binding.title.text = reformatDate(item.dt_txt)
        binding.subTitle.text = "${item.main.temp} \u2103"

        val icon = item.weather[0].icon
        val iconUrl = "http://openweathermap.org/img/w/$icon.png";
        Glide.with(context).load(iconUrl).into(binding.icon);
    }

    private fun reformatDate(string: String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("id", "ID"))
        val formatter = SimpleDateFormat("dd-MM", Locale("id", "ID"))
        return formatter.format(parser.parse(string)!!)
    }
}