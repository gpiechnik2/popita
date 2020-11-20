package com.example.popitaapp.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.popitaapp.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_explore_detail.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.*


class ExploreDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    // declare a global variable FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // globally declare LocationRequest
    private lateinit var locationRequest: LocationRequest

    // globally declare LocationCallback
    private lateinit var locationCallback: LocationCallback

    //globally declare user Marker
    private lateinit var markerCU: Marker

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

        //change receiver name
        txtName.text = getIntent().getStringExtra("user_first_name")

        //change receiver localization
        address.text = getIntent().getStringExtra("location")

        //change receiver distance
        txtDistance.text = getIntent().getFloatExtra("distance", 0.toFloat()).toString()

        //back button
        var back_btn = findViewById(R.id.back_btn) as Button
        back_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, ExploreActivity::class.java)
            startActivity(intent);
        }
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

        val ruser_location = LatLng(ruser_latitude.toDouble(), ruser_longitude.toDouble())
        mMap.addMarker(MarkerOptions()
                .position(ruser_location)
                .title(ruser_name))

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ruser_location, 16.toFloat()))

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

                    //get address
                    val address = getUserAddress(location)

                    //post send request to database to update user location
                    updateUserLocation(location, address)

                    //if map is actually loaded, mark current user
                    mMap.setOnMapLoadedCallback {
                        updateCurrentUserLocation(location)
                    }
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

    fun updateCurrentUserLocation(location: Location) {
        //remove current marker of a current user
        if (::markerCU.isInitialized) {
            markerCU.remove()
        }

        //and add new marker
        val currentUserLocation = LatLng(location.latitude, location.longitude)
        markerCU = mMap.addMarker(MarkerOptions()
            .position(currentUserLocation)
            .title("You"))
            //TODO create layout to icon
    }

    fun getUserAddress(location: Location): String {
        val latitude = location.latitude
        val longitude = location.longitude

        val addresses: List<Address>
        val geocoder = Geocoder(this, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


        val address: String = addresses[0]
            .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        val city: String = addresses[0].getLocality()
        val state: String = addresses[0].getAdminArea()
        val country: String = addresses[0].getCountryName()
        val postalCode: String = addresses[0].getPostalCode()
        val knownName: String =
            addresses[0].getFeatureName() // Only if available else return NULL

        return address
    }

    private fun updateUserLocation(location: Location, address: String) {

        //get auth token
        val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
        val auth_token = sharedPreference.getString("auth_token", null)

        //val url = "http://192.168.0.5:8000/localization/localization/"
        val url = "http://192.168.0.101:8000/localization/localization/"

        val latitude = location.latitude
        val longitude = location.longitude

        val jsonObject = JSONObject()
        jsonObject.put("latitude", latitude)
        jsonObject.put("longitude", longitude)
        jsonObject.put("attitude", 1.000000)
        jsonObject.put("location", address)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization", "Token " + auth_token.toString())
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@ExploreDetailActivity.runOnUiThread(Runnable {
                    Toast.makeText(
                        this@ExploreDetailActivity,
                        "Unexpected problem.",
                        Toast.LENGTH_SHORT
                    ).show()
                })
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) {

                } else if (response.code == 400) {
                    this@ExploreDetailActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@ExploreDetailActivity,
                            "Unexpected problem. Please log in again.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@ExploreDetailActivity, MainActivity::class.java)
                        startActivity(intent);
                    })

                } else if (response.code == 401) {
                    this@ExploreDetailActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@ExploreDetailActivity,
                            "Unexpected problem. Please log in again.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@ExploreDetailActivity, MainActivity::class.java)
                        startActivity(intent);
                    })
                }
            }
        })
    }

}
