package es.upm.btb.helloworldkt

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class SecondActivity : AppCompatActivity() {
    private val TAG = "btaSecondActivity"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        Log.d(TAG, "onCreate: The activity is being created.");

        val bundle = intent.getBundleExtra("locationBundle")
        val location: Location? = bundle?.getParcelable("location")

        val text: TextView = findViewById(R.id.mapsLocation)
        if (location != null) {
            text.text = "Latitude: ${location.latitude}, \nLongitude: ${location.longitude}"
        }

        val buttonNext: Button = findViewById(R.id.secondNextButton)
        buttonNext.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }

        val buttonPrevious: Button = findViewById(R.id.secondPreviousButton)
        buttonPrevious.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}