package com.immr.weatherapp.ui.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.immr.weatherapp.ConnectionType
import com.immr.weatherapp.NetworkUtil
import com.immr.weatherapp.R
import com.immr.weatherapp.data.network.ApiClient.retrofitInstance
import com.immr.weatherapp.data.network.GetService
import com.immr.weatherapp.databinding.ActivityMainBinding
import com.immr.weatherapp.ui.model.DataKotaResponse
import com.immr.weatherapp.ui.model.DataProvinceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mDataProvince = arrayListOf<String>()
    private val mDataProvinceAll = mutableListOf<DataProvinceResponse.Provinsi>()
    private val mDataCity = arrayListOf<String>()

    private val networkMonitor = NetworkUtil(this)

    private var parName = ""
    private var parProvince = ""
    private var parCity = ""


    companion object {
        lateinit var mActivity: MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mActivity = this

        initUi()
        initData()
        initAction()
    }

    private fun initUi() {
        checkConnection()
        binding.inputCity.apply {
            isEnabled = false
        }
    }

    private fun initData() {
        getDataProvince()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initAction() {

        binding.btnSubmit.setOnClickListener {
            val bundle = Bundle()
            val intent = Intent(mActivity, WeatherActivity::class.java)
            bundle.putString("city", parCity)
            bundle.putString("name", parName)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        binding.inputName.doOnTextChanged { text, _, _, _ ->
            if(!text.isNullOrEmpty()){
                binding.inputNameLayout.apply {
                    isErrorEnabled = false
                    error = null
                }
                parName = text.toString()
                validateForm()
            }else{
                binding.inputNameLayout.apply {
                    isErrorEnabled = true
                    error = "Nama tidak boleh kosong"
                }
                validateForm()
            }
        }

        binding.inputProvince.doOnTextChanged { text, _, _, _ ->
            if(!text.isNullOrEmpty()){
                binding.inputProvinceLayout.apply {
                    isErrorEnabled = false
                    error = null
                }
                validateForm()
            }else{
                binding.inputProvinceLayout.apply {
                    isErrorEnabled = true
                    error = "Provinsi tidak boleh kosong"
                }
                validateForm()
            }
        }


        binding.inputCity.doOnTextChanged { text, _, _, _ ->
            if(!text.isNullOrEmpty()){
                binding.inputCityLayout.apply {
                    isErrorEnabled = false
                    error = null
                }
                validateForm()
            }else{
                binding.inputCityLayout.apply {
                    isErrorEnabled = true
                    error = "Kota harus di isi"
                }
                validateForm()
            }
        }

        binding.inputProvince.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parProvince = p0.toString()
            }
            override fun afterTextChanged(p0: Editable?) {
                filterProvince(p0.toString())
                if(!p0.isNullOrEmpty()){
                    findDataCityById(p0.toString())
                }
            }
        })

        binding.inputCity.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                filterCity(p0.toString())
            }
        })
    }

    private fun findDataCityById(text: String){
        val data = mDataProvinceAll.find { it.nama == text }
        if(data != null){
            getDataCity(data.id.toString())
        }
    }

    private fun filterProvince(text: String){
        val data = mDataProvince.filter {
            it.trim().lowercase(Locale.getDefault()).contains(text.trim().lowercase(Locale.getDefault()))
        }

        if(data.isEmpty()){
           binding.inputProvinceLayout.apply {
               isErrorEnabled = true
               error = "Data Provinsi Tidak Ditemukan"
           }
            validateForm()
        }else{
            binding.inputProvinceLayout.apply {
                isErrorEnabled = false
                error = null
            }
            mDataProvinceAll.forEach {
                val textProv = binding.inputProvince.text.toString().trim().lowercase(Locale.getDefault())
                if(textProv == it.nama.trim().lowercase(Locale.getDefault())){
                    binding.inputCity.isEnabled = true
                    getDataCity(it.id.toString())
                }
            }
            validateForm()
        }
    }

    private fun filterCity(text: String){
        val data = mDataCity.filter {
            it.trim().lowercase(Locale.getDefault()).contains(text.trim().lowercase(Locale.getDefault()))
        }

        if(data.isEmpty()){
            binding.inputCityLayout.apply {
                isErrorEnabled = true
                error = "Data Kota Tidak Ditemukan"
            }
            validateForm()
        }else{
            binding.inputCityLayout.apply {
                isErrorEnabled = false
                error = null
            }
            validateForm()

            mDataCity.forEach {
                val textcity = binding.inputCity.text.toString().trim().lowercase(Locale.getDefault())
                if(textcity == it.trim().lowercase(Locale.getDefault())){
                    parCity = it
                }
            }
        }
    }

    private fun getDataProvince(){
        val service = retrofitInstance!!.create(GetService::class.java)
        val apiCall: Call<DataProvinceResponse> = service.getDataProvince()
        apiCall.enqueue(object : Callback<DataProvinceResponse> {
            override fun onResponse(call: Call<DataProvinceResponse>, response: Response<DataProvinceResponse>) {
                if(response.code() == 200){
                    val listProvince: List<DataProvinceResponse.Provinsi> = response.body()!!.provinsi
                    if(listProvince.isNotEmpty()){
                        mDataProvince.clear()
                        mDataProvinceAll.clear()
                        mDataProvinceAll.addAll(listProvince)
                        listProvince.forEach{
                            mDataProvince.add(it.nama)
                        }
                        adapterInputProvince()
                    }else{
                        showToast(response.message())
                    }
                }else{
                    showToast(response.message())
                }
            }
            override fun onFailure(call: Call<DataProvinceResponse>, t: Throwable) {
                showToast(t.message!!)
            }
        })
    }

    private fun getDataCity(provinceId: String){
        val service = retrofitInstance!!.create(GetService::class.java)
        val apiCall: Call<DataKotaResponse> = service.getDataCity(provinceId)
        apiCall.enqueue(object : Callback<DataKotaResponse> {
            override fun onResponse(call: Call<DataKotaResponse>, response: Response<DataKotaResponse>) {
                val listCity: List<DataKotaResponse.Kota> = response.body()!!.kota_kabupaten
                if (listCity.isNotEmpty()){
                    mDataCity.clear()
                    listCity.forEach {
                        mDataCity.add(it.nama)
                    }
                    adapterInputCity()
                }else{
                    showToast(response.message())
                }
            }

            override fun onFailure(call: Call<DataKotaResponse>, t: Throwable) {
                showToast(t.message!!)
            }
        })
    }

    private fun adapterInputProvince(){
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, mDataProvince)
        val actv = binding.inputProvince
        actv.threshold = 1
        actv.setAdapter(adapter)
    }

    private fun adapterInputCity(){
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, mDataCity)
        val actv = binding.inputCity
        actv.threshold = 1
        actv.setAdapter(adapter)
    }


    private fun validateForm(){
        val inputName = binding.inputName.text.toString()
        val inputProvince = binding.inputProvince.text.toString()
        val inputCity = binding.inputCity.text.toString()

        if(inputName.isNotEmpty() && inputProvince.isNotEmpty() && inputCity.isNotEmpty()){
            setButtonSubmit(true)
        }else{
            setButtonSubmit(false)
        }
    }

    private fun setButtonSubmit(value:Boolean){
        if(value){
            binding.btnSubmit.apply {
                isEnabled = true
                setBackgroundResource(R.drawable.btn_active)
            }
        }else{
            binding.btnSubmit.apply {
                isEnabled = false
                setBackgroundResource(R.drawable.btn_disable)
            }
        }
    }

    private fun showToast(message:String) {
        val t = Toast.makeText(
            applicationContext,
            message,
            Toast.LENGTH_LONG
        )
        t.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
        t.show()
    }

    private fun checkConnection(){
        networkMonitor.result = { isAvailable, type ->
            runOnUiThread {
                when (isAvailable) {
                    true -> {
                        when (type) {
                            ConnectionType.Wifi -> {
                                Log.i("NETWORK_MONITOR_STATUS", "Wifi Connection")
                            }
                            ConnectionType.Cellular -> {
                                Log.i("NETWORK_MONITOR_STATUS", "Cellular Connection")
                            }
                            else -> {
                            }
                        }
                    }
                    false -> {
                        showDialogConnection()
                    }
                }
            }
        }
    }

    private fun showDialogConnection() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Tidak Ada Jaringan")
        alertDialogBuilder
            .setMessage("Sambungkan dengan internet ya")
            .setIcon(R.mipmap.ic_launcher)
            .setCancelable(false)
            .setPositiveButton("Reload") { dialog, _ ->
                dialog.dismiss()
                dialog.cancel()
                finish()
                startActivity(intent)
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
                dialog.cancel()
            }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        networkMonitor.register()
    }

    override fun onStop() {
        super.onStop()
        networkMonitor.unregister()
    }
}