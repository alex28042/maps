package es.upm.btb.helloworldkt.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import androidx.core.app.ActivityCompat
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import es.upm.btb.helloworldkt.R
import es.upm.btb.helloworldkt.database.AppDatabase
import es.upm.btb.helloworldkt.entities.LocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity(), LocationListener {
    private val TAG = "btaMainActivity"
    private lateinit var locationManager: LocationManager
    var latestLocation: Location? = null
    private val locationPermissionCode = 2

    lateinit var database: AppDatabase

    companion object {
        private const val RC_SIGN_IN = 123
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "cordinates").build()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeMenu -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.mapMenu -> {
                    if (latestLocation != null) {
                        val intent = Intent(this, OpenStreetMapActivity::class.java)
                        val bundle = Bundle()
                        bundle.putParcelable("location", latestLocation)
                        intent.putExtra("locationBundle", bundle)
                        startActivity(intent)
                    }else{
                        Log.e(TAG, "Location not set yet.")
                    }
                    true
                }
                R.id.settingsMenu -> {
                    val intent = Intent(this, SecondActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.weatherMenu -> {
                    if (latestLocation != null) {
                        val intent = Intent(this, WeatherActivity::class.java)
                        val bundle = Bundle()
                        bundle.putParcelable("location", latestLocation)
                        intent.putExtra("locationBundle", bundle)
                        startActivity(intent)
                    }else{
                        Log.e(TAG, "Location not set yet.")
                    }
                    true
                }
                else -> false
            }
        }

        val userIdentifier = getUserIdentifier()
        if (userIdentifier == null) {
            askForUserIdentifier()
        } else {
            Toast.makeText(this, "User ID: $userIdentifier", Toast.LENGTH_LONG).show()
        }

        Log.d(TAG, "onCreate: The activity is being created.")
        println("Hello world to test System.out standar aoutput!")

        val buttonSecond: Button = findViewById(R.id.mainButton)
        buttonSecond.setOnClickListener {
            if (latestLocation != null) {
                val intent = Intent(this, SecondActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("location", latestLocation)
                intent.putExtra("locationBundle", bundle)
                startActivity(intent)
            }else{
                Log.e(TAG, "Location not set yet.")
            }
        }

        val userButton: Button = findViewById(R.id.userButton)
        userButton.setOnClickListener {
            askForUserIdentifier()
        }

        val buttonOsm: Button = findViewById(R.id.osmButton)
        buttonOsm.setOnClickListener {
            if (latestLocation != null) {
                val intent = Intent(this, OpenStreetMapActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("location", latestLocation)
                intent.putExtra("locationBundle", bundle)
                startActivity(intent)
            }else{
                Log.e(TAG, "Location not set yet.")
            }
        }
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                locationPermissionCode
            )
        } else {
            // The location is updated every 5000 milliseconds (or 5 seconds) and/or if the device moves more than 5 meters,
            // whichever happens first
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        latestLocation = location
        val textView: TextView = findViewById(R.id.mainTextView)
        textView.text = "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
        Toast.makeText(this, "Latitude: ${location.latitude}, Longitude: ${location.longitude}", Toast.LENGTH_LONG).show()
        saveCoordinatesToFile(location.latitude, location.longitude)
        var newLocation = LocationEntity(
            latitude = location.latitude,
            longitude = location.longitude,
            timestamp = System.currentTimeMillis()
        )
        println(newLocation)
        lifecycleScope.launch(Dispatchers.IO) {
            database.locationDao().insertLocation(newLocation)
            var list = database.locationDao().getAllLocations()
            println("holaaa")
            println(list)
        }
    }
    private fun saveCoordinatesToFile(latitude: Double, longitude: Double) {
        val fileName = "gps_coordinates.csv"
        val file = File(filesDir, fileName)
        val timestamp = System.currentTimeMillis()
        file.appendText("$timestamp;$latitude;$longitude\n")
    }

    private fun saveUserIdentifier(userIdentifier: String) {
        val sharedPreferences = this.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("userIdentifier", userIdentifier)
            apply()
        }
    }
    private fun getUserIdentifier(): String? {
        val sharedPreferences = this.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userIdentifier", null)
    }

    private fun askForUserIdentifier() {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Enter User Identifier")
            .setIcon(R.mipmap.ic_launcher)
            .setView(input)
            .setPositiveButton("Save") { dialog, which ->
                val userInput = input.text.toString()
                if (userInput.isNotBlank()) {
                    saveUserIdentifier(userInput)
                    Toast.makeText(this, "User ID saved: $userInput", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "User ID cannot be blank", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
}