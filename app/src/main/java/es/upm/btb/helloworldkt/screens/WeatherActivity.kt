package es.upm.btb.helloworldkt.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.upm.btb.helloworldkt.R
import es.upm.btb.helloworldkt.apirest.WeatherAdapter
import es.upm.btb.helloworldkt.database.AppDatabase
import es.upm.btb.helloworldkt.entities.TempertureEntity
import es.upm.btb.helloworldkt.entities.WeatherData
import es.upm.btb.helloworldkt.repositories.IOpenWeather
import retrofit2.Call
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class WeatherActivity : AppCompatActivity() {
    private val TAG = "btaThirdActivity"
    private lateinit var weatherService: IOpenWeather
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var database: AppDatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        val bundle = intent.getBundleExtra("locationBundle")
        val location: Location? = bundle?.getParcelable("location")


        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.weatherMenu
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeMenu -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.mapMenu -> {
                    if (location != null) {
                        val intent = Intent(this, OpenStreetMapActivity::class.java)
                        val bundle = Bundle()
                        bundle.putParcelable("location", location)
                        intent.putExtra("locationBundle", bundle)
                        startActivity(intent)
                    } else {
                        Log.d("D","Location not set yet.")
                    }
                    true
                }

                R.id.settingsMenu -> {
                    val intent = Intent(this, SecondActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.weatherMenu -> {
                    val intent = Intent(this, WeatherActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        if (location != null) {
            initRetrofit()
            val recyclerView: RecyclerView = findViewById(R.id.itemsWeather)
            recyclerView.layoutManager = LinearLayoutManager(this)
            weatherAdapter = WeatherAdapter(emptyList())
            recyclerView.adapter = weatherAdapter
            requestWeatherData(location.latitude, location.longitude, "ae054c56b006a82fa7280121b0aad26a")
        }

    }

    private fun initRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        weatherService = retrofit.create(IOpenWeather::class.java)
    }
    private fun requestWeatherData(latitude: Double, longitude: Double, apiKey: String) {
        val weatherDataCall = weatherService.getWeatherData(
            latitude = latitude,
            longitude = longitude,
            count = 20,
            apiKey = apiKey
        )
        weatherDataCall.enqueue(object : Callback<WeatherData> {
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if (response.isSuccessful) {
                    response.body()?.let { weatherResponse ->
                            weatherAdapter.updateWeatherData(weatherResponse.list)
                        weatherAdapter.notifyDataSetChanged()
                        Toast.makeText(this@WeatherActivity, "Weather Data Retrieved", Toast.LENGTH_SHORT).show()
                    } ?: run {
                        Toast.makeText(this@WeatherActivity, "Response is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("MainActivity", "Error fetching weather data: ${response.errorBody()?.string()}")
                    Toast.makeText(this@WeatherActivity, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                // Handle error case
                Log.e("MainActivity", "Failure: ${t.message}")
                Toast.makeText(this@WeatherActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}