package com.immr.weatherapp.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.immr.weatherapp.databinding.ItemsListDateBinding
import com.immr.weatherapp.ui.model.DataWeatherResponse
import java.text.SimpleDateFormat
import java.util.*

class ListDateAdapter(data: List<DataWeatherResponse.ListElement>, context: Context) :
    ListAdapter<DataWeatherResponse.ListElement, DateViewHolder>(ItemDiffCallback) {
    private val mData: List<DataWeatherResponse.ListElement> = data
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private val mContext: Context = context
    var onClickData: ((DataWeatherResponse.ListElement) -> Unit)? = null

    private lateinit var adaptersWeather: ListWeatherAdapter

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        return DateViewHolder(ItemsListDateBinding.inflate(mInflater, parent, false))
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(mData[position], mContext)
        try {
            mData.groupBy { reformatDate(it.dt_txt) == reformatDate(mData[position].dt_txt) }
            adaptersWeather = ListWeatherAdapter(mData.slice(1..5), mContext)

            holder.rvList.apply {
                adapter = adaptersWeather
                layoutManager = GridLayoutManager(mContext, 5)
            }

            adaptersWeather.notifyDataSetChanged()
        } catch (e: Exception){
            Log.e("mDate",e.toString())
        }
    }

    private fun reformatDate(string: String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("id", "ID"))
        val formatter = SimpleDateFormat("dd-MMMM-yyyy", Locale("id", "ID"))
        return formatter.format(parser.parse(string)!!)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}

class DateViewHolder(private val binding: ItemsListDateBinding) : RecyclerView.ViewHolder(binding.root) {
    val rvList = binding.rvWeather
    fun bind(item: DataWeatherResponse.ListElement, context: Context) {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("id", "ID"))
        val formatter = SimpleDateFormat("EEE,MM/yy", Locale("id", "ID"))
        val printDate = formatter.format(parser.parse(item.dt_txt)!!)
        binding.title.text = printDate
    }
}