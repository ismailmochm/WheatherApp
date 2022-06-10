package com.immr.weatherapp.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.immr.weatherapp.data.network.ApiClientWeather
import com.immr.weatherapp.data.network.GetService
import com.immr.weatherapp.databinding.ActivityWeatherBinding
import com.immr.weatherapp.ui.adapter.ListDateAdapter
import com.immr.weatherapp.ui.model.DataWeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class WeatherActivity: AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding

    private val APPID = "36d4892f22f7e52fc49d9ee6fea79927"

    private lateinit var adaptersWeather: ListDateAdapter
    private val mDataWeather = mutableListOf<DataWeatherResponse.ListElement>()

    companion object {
        lateinit var mActivity: WeatherActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mActivity = this

        initParam()
        initUi()
        initData()
        initAction()
    }

    private fun initParam() {
        if(intent.hasExtra("city") && intent.hasExtra("name")) {
            intent.getStringExtra("city").let {
                if(it != null){
                    getDataWeather(it)
                }
            }

            intent.getStringExtra("name").let {
                if (it != null){
                    binding.titleName.text = "${getGreetingMessage()} $it"
                }
            }
        }
    }

    private fun initAction() {

    }

    private fun initData() {
        adapterListWeather()
    }

    private fun initUi() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun getDataWeather(q: String){
        val service = ApiClientWeather.retrofitInstance!!.create(GetService::class.java)
        val apiCall: Call<DataWeatherResponse> = service.getWeather(q, APPID)
        apiCall.enqueue(object : Callback<DataWeatherResponse> {
            override fun onResponse(call: Call<DataWeatherResponse>, response: Response<DataWeatherResponse>) {
               if (response.code() == 200){
                   val listWeather: List<DataWeatherResponse.ListElement> = response.body()!!.list
                   val city = response.body()!!.city
                   if(listWeather.isNotEmpty()){
                       mDataWeather.clear()
                       mDataWeather.addAll(listWeather.distinctBy { reformatDate(it.dt_txt) })
                       adaptersWeather.notifyDataSetChanged()

                       binding.titleCity.text = city.name
                       binding.temperature.text = "Temperature : ${listWeather[0].main.temp} \u2103"
                       binding.weather.text = listWeather[0].weather[0].main
                       val icon = listWeather[0].weather[0].icon
                       val iconUrl = "http://openweathermap.org/img/w/$icon.png"
                       Glide.with(mActivity).load(iconUrl).into(binding.icons)

                       val dataWeather = listWeather.first()
                       binding.numHum.text = "${dataWeather.main.humidity} %"
                       binding.numPresure.text = "${dataWeather.main.pressure} hpa"
                       binding.numCloud.text = "${dataWeather.clouds.all} %"
                       binding.numWin.text = "${dataWeather.wind.speed} m/s"
                   }
               }else{
                   showToast(response.message())
               }
            }
            override fun onFailure(call: Call<DataWeatherResponse>, t: Throwable) {
                Log.e("errorWeather", "$t")
            }
        })
    }

    private fun adapterListWeather(){
        adaptersWeather = ListDateAdapter(mDataWeather, this)

        binding.rvWeather.apply {
            adapter = adaptersWeather
            layoutManager =  LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun getGreetingMessage():String{
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)

        return when (timeOfDay) {
            in 0..11 -> "Hallo, Selamat Pagi"
            in 12..15 -> "Hallo, Selamat Siang"
            in 16..20 -> "Hallo, Selamat Sore"
            in 21..23 -> "Hallo, Selamat Malam"
            else -> "Hello"
        }
    }

    private fun showToast(message:String) {
        val t = Toast.makeText(
            applicationContext,
            message,
            Toast.LENGTH_LONG
        )
        t.show()
    }

    private fun reformatDate(string: String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("id", "ID"))
        val formatter = SimpleDateFormat("dd-MMMM-yyyy", Locale("id", "ID"))
        return formatter.format(parser.parse(string)!!)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}