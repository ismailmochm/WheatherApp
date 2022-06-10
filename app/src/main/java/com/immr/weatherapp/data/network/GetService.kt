package com.immr.weatherapp.data.network

import com.immr.weatherapp.ui.model.DataKotaResponse
import com.immr.weatherapp.ui.model.DataProvinceResponse
import com.immr.weatherapp.ui.model.DataWeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GetService {

    @GET("api/daerahindonesia/provinsi")
    fun getDataProvince(): Call<DataProvinceResponse>

    @GET("api/daerahindonesia/kota")
    fun getDataCity(
        @Query("id_provinsi") id_provinsi: String
    ): Call<DataKotaResponse>

    @GET("data/2.5/forecast")
    fun getWeather(
        @Query("q") q: String,
        @Query("appid") appid: String
    ): Call<DataWeatherResponse>
}
