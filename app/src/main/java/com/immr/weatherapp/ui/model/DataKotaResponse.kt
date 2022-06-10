package com.immr.weatherapp.ui.model

data class DataKotaResponse (
    val kota_kabupaten: List<Kota>
) {
    data class Kota (
        val id: Int,
        val id_provinsi: String,
        val nama: String
    )
}
