package com.example.popitaapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.popitaapp.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class ExploreDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    // declare a global variable FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // globally declare LocationRequest
    private lateinit var locationRequest: LocationRequest

    // globally declare LocationCallback
    private lateinit var locationCallback: LocationCallback



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_detail)

        //check your API key
        if (getString(R.string.maps_api_key).isEmpty()) {
            this@ExploreDetailActivity.runOnUiThread(Runnable {
                Toast.makeText(
                    this@ExploreDetailActivity,
                    "Add your own API key in MapWithMarker/app/secure.properties as MAPS_API_KEY=YOUR_API_KEY",
                    Toast.LENGTH_SHORT
                ).show()
            })
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationUpdates()

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

    }


    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        // Customise the styling of the base map using a JSON object defined
        // in a raw resource file.
        val success: Boolean = googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle)
        )
        if (!success) {
            this@ExploreDetailActivity.runOnUiThread(Runnable {
                Toast.makeText(
                    this@ExploreDetailActivity,
                    "Styling map failed.",
                    Toast.LENGTH_SHORT
                ).show()
            })
        }

        // Add a marker of requested user
        val ruser_longitude = getIntent().getFloatExtra("longitude", 0.toFloat())
        val ruser_latitude = getIntent().getFloatExtra("latitude", 0.toFloat())
        val ruser_name = getIntent().getStringExtra("user_first_name")

        val ruser_location = LatLng(ruser_longitude.toDouble(), ruser_latitude.toDouble())
        mMap.addMarker(MarkerOptions()
                .position(ruser_location)
                .title(ruser_name))



    }

    private fun getLocationUpdates()
    {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest()
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 170f // 170 m = 0.1 mile
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                if (locationResult.locations.isNotEmpty()) {
                    // get latest location
                    val location =
                            locationResult.lastLocation

                    println(" DZIALA")
                    println(location)
                    println("XDDDXXDDXXDDXDXDXDDDXDDX")
                    // use your location object
                    // get latitude , longitude and other info from this
                }


            }
        }
    }

    //start location updates
    private fun startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show()
            this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null /* Looper */
        )
    }

    // stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // stop receiving location update when activity not visible/foreground
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    // start receiving location update when activity  visible/foreground
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

}
