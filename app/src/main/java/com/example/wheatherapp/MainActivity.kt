package com.example.wheatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.wheatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


//069f1465b23f930afd287cb4158a07dd
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Bangalore")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retroFit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response =
            retroFit.getWeatherData(cityName, "069f1465b23f930afd287cb4158a07dd", "Metric")
        response.enqueue(object : Callback<WeatherApp> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                Log.d("tag", "onResponse: Response: $response")
                if(responseBody==null) {
                    if(response.message().equals("Not Found")){
                            val alert = AlertDialog.Builder(this@MainActivity)
                            alert.setTitle("warning")
                                .setMessage("please check city name")
                                .setNeutralButton("Ok") { dialog, _ ->
                                    dialog.cancel()
                                }
                                .show()
                    }
                }
              if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity.toString()
                    val windSpeed = responseBody.wind.speed.toString()
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure.toString()
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max.toString()
                    val minTemp = responseBody.main.temp_min.toString()
//                    Log.d("tag","on-response:$temperature")
                    binding.Temp.text = "$temperature c"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp: $maxTemp c"
                    binding.minTemp.text = "Min Temp: $minTemp c"
                    binding.humidityy.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunrise.text = time(sunRise)
                    binding.sunset.text = time(sunset)
                    binding.sea.text = "$seaLevel hPa"
                    binding.sunny.text = condition
                    binding.day.text = dayName()
                    binding.date.text = date()
                    binding.cityName.text = " $cityName"

                    changeImagesAccordingToWeatherCondition(condition)
                }

            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
            }
        })
    }

    private fun changeImagesAccordingToWeatherCondition(conditions:String) {
       when(conditions){
           "Clear Sky","Sunny","Clear" -> {
               binding.root.setBackgroundResource(R.drawable.sunny_background)
               binding.lottieImg.setAnimation(R.raw.sun)
           }
           "Partly Clouds","Clouds","Overcast","Mist","Foggy" -> {
               binding.root.setBackgroundResource(R.drawable.colud_background)
               binding.lottieImg.setAnimation(R.raw.cloud)
           }
           "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" -> {
               binding.root.setBackgroundResource(R.drawable.rain_background)
               binding.lottieImg.setAnimation(R.raw.rain)
           }
           "Light Snow","Moderate Snow","Heavy Snow","Blizzard" -> {
               binding.root.setBackgroundResource(R.drawable.snow_background)
               binding.lottieImg.setAnimation(R.raw.snow)
           }
           else -> {
               binding.root.setBackgroundResource(R.drawable.sunny_background)
               binding.lottieImg.setAnimation(R.raw.sun)
           }
       }
        binding.lottieImg.playAnimation()
    }

    @SuppressLint("WeekBasedYear")
    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timeStamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timeStamp*1000)))
    }
    fun dayName(): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}