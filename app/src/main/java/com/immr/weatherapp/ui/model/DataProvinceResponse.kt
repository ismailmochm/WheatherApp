package com.immr.weatherapp.ui.model

data class DataProvinceResponse (
    val provinsi: List<Provinsi>
) {
    data class Provinsi (
        val id: Int,
        val nama: String
    )
}
