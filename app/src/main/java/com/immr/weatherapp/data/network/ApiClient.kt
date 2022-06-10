package com.immr.weatherapp.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory;

object ApiClient {
    var retrofit: Retrofit? = null
    const val BASE_URL = "https://dev.farizdotid.com/"
    val retrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
}