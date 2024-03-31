package es.upm.btb.helloworldkt.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.upm.btb.helloworldkt.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline


class OpenStreetMapActivity : AppCompatActivity() {
    private val TAG = "btaOpenStreetMapActivity"
    private lateinit var map: MapView

    val gymkhanaCoords = listOf(
        GeoPoint(40.416775, -3.70379),  // Puerta del Sol
        GeoPoint(40.415363, -3.707398), // Plaza Mayor
        GeoPoint(40.413816, -3.701823), // Mercado de San Miguel
        GeoPoint(40.415347, -3.693162), // Palacio Real
        GeoPoint(40.416775, -3.70379)   // Vuelve a Puerta del Sol para finalizar
    )

    val gymkhanaNames = listOf(
        "Puerta del Sol",
        "Plaza Mayor",
        "Mercado de San Miguel",
        "Palacio Real",
        "Puerta del Sol (Fin del recorrido)"
    )


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_street_map)

        Log.d(TAG, "onCreate: The activity is being created.");

        val bundle = intent.getBundleExtra("locationBundle")
        val location: Location? = bundle?.getParcelable("location")

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.mapMenu
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
                    val intent = Intent(this, WeatherActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        if (location != null) {
            Log.i(TAG, "onCreate: Location["+location.altitude+"]["+location.latitude+"]["+location.longitude+"][")

            Configuration.getInstance().load(applicationContext, getSharedPreferences("osm", MODE_PRIVATE))

            map = findViewById(R.id.map)
            map.setTileSource(TileSourceFactory.MAPNIK)
            map.controller.setZoom(18.0)

            val startPoint = GeoPoint(location.latitude, location.longitude)
            map.controller.setCenter(startPoint)

            addMarker(startPoint, "My current location")
            //addMarkers(map, gymkhanaCoords, gymkhanaNames)

            addMarkersAndRoute(map, gymkhanaCoords, gymkhanaNames)
        };
    }

    private fun addMarker(point: GeoPoint, title: String) {
        val marker = Marker(map)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = title
        map.overlays.add(marker)
        map.invalidate() // Reload map
    }

    fun addMarkers(mapView: MapView, locationsCoords: List<GeoPoint>, locationsNames: List<String>) {

        for (location in locationsCoords) {
            val marker = Marker(mapView)
            marker.position = location
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = "Marker at ${locationsNames.get(locationsCoords.indexOf(location))} ${location.latitude}, ${location.longitude}"
            marker.icon = ContextCompat.getDrawable(this, com.google.android.material.R.drawable.ic_m3_chip_checked_circle)
            mapView.overlays.add(marker)
        }
        mapView.invalidate() // Refresh the map to display the new markers
    }

    fun addMarkersAndRoute(mapView: MapView, locationsCoords: List<GeoPoint>, locationsNames: List<String>) {
        if (locationsCoords.size != locationsNames.size) {
            Log.e("addMarkersAndRoute", "Locations and names lists must have the same number of items.")
            return
        }

        val route = Polyline()
        route.setPoints(locationsCoords)
        route.color = ContextCompat.getColor(this, R.color.teal_700)
        mapView.overlays.add(route)

        for (location in locationsCoords) {
            val marker = Marker(mapView)
            marker.position = location
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            val locationIndex = locationsCoords.indexOf(location)
            marker.title = "Marker at ${locationsNames[locationIndex]} ${location.latitude}, ${location.longitude}"
            marker.icon = ContextCompat.getDrawable(this, org.osmdroid.library.R.drawable.ic_menu_compass)
            mapView.overlays.add(marker)
        }

        mapView.invalidate()
    }



    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
