package com.example.popitaapp.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.popitaapp.R
import com.example.popitaapp.activities.adapters.OnRoomItemClickListener
import com.example.popitaapp.activities.adapters.RoomAdapter
import com.example.popitaapp.activities.models.Room
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class RoomActivity : AppCompatActivity(), OnRoomItemClickListener {

    // declare a global variable FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // globally declare LocationRequest
    private lateinit var locationRequest: LocationRequest

    // globally declare LocationCallback
    private lateinit var locationCallback: LocationCallback

    companion object {
        val users = ArrayList<Room>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        //messages button
        menuMessages.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, RoomActivity::class.java)
            startActivity(intent);
        }

        //messages button
        menuExplore.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, ExploreActivity::class.java)
            startActivity(intent);
        }







        //connect with Room model
        val rv = findViewById<RecyclerView>(R.id.recyclerView1)
        rv.layoutManager = LinearLayoutManager(this@RoomActivity, LinearLayoutManager.VERTICAL, false)

        //get post request
        //TODO Change async request to sync
        fetchJson()

        //updates of your location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationUpdates()

        //2 seconds delay to process fetchJson
        runBlocking {     // but this expression blocks the main thread
            delay(2000L)  // ... while we delay for 2 seconds to keep JVM alive
        }

        var adapter = RoomAdapter(users, this@RoomActivity)
        rv.adapter = adapter

        //search util
        search_buddy.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val new_users = ArrayList<Room>()

                for (user in users) {
                    if (user.receiver_name.toLowerCase().contains(p0.toString().toLowerCase())) {
                        //adding the element to filtered list
                        new_users.add(user)
                    }
                }

                val new_adapter = RoomAdapter(new_users, this@RoomActivity)
                rv.adapter = new_adapter

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    private fun fetchJson() {

        //get auth token
        val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
        val auth_token = sharedPreference.getString("auth_token", null)

        //val url = "http://192.168.0.5:8000/chat/rooms/"
        val url = "http://192.168.0.101:8000/chat/rooms/"

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Token " + auth_token.toString())
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@RoomActivity.runOnUiThread(Runnable {
                    Toast.makeText(
                        this@RoomActivity,
                        "Unexpected problem.",
                        Toast.LENGTH_SHORT
                    ).show()
                })
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) {

                    val body = response.body?.string()
                    val jsonObject = JSONObject(body)

                    getRooms(jsonObject)

                } else if (response.code == 400) {
                    this@RoomActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@RoomActivity,
                            "Unable to get chat rooms with provided credentials. Please log in or register first.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@RoomActivity, MainActivity::class.java)
                        startActivity(intent);
                    })

                } else if (response.code == 401) {
                    this@RoomActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@RoomActivity,
                            "Unable to get chat rooms with provided credentials. Please log in or register first.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@RoomActivity, MainActivity::class.java)
                        startActivity(intent);
                    })
                }
            }
        })
    }

    fun getRooms(results: JSONObject) {

        //clear to not to duplicate records
        users.clear()

        //iterate over results and append them to array
        val room_results = results.getJSONArray("results")

        for (i in 0 until room_results.length()) {
            val id = room_results.getJSONObject(i).getInt("id")
            val last_message = room_results.getJSONObject(i).getString("last_message")
            val last_sender = room_results.getJSONObject(i).getInt("last_sender")
            val receiver_id = room_results.getJSONObject(i).getInt("receiver_id")
            val receiver_name = room_results.getJSONObject(i).getString("receiver_name")
            val last_message_timestamp = room_results.getJSONObject(i).getString("last_message_timestamp")

            users.add(Room(id, last_message, last_sender, receiver_id, receiver_name, last_message_timestamp))
        }

        //TODO if getRooms is empty, move to ANOTHER view

    }

    override fun onItemClick(item: Room, position: Int) {
        val intent = Intent(this, RoomDetailActivity::class.java)
        intent.putExtra("receiver_name", item.receiver_name)
        intent.putExtra("id", item.id)
        startActivity(intent)
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
                }
            }
        }
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
                this@RoomActivity.runOnUiThread(Runnable {
                    Toast.makeText(
                        this@RoomActivity,
                        "Unexpected problem.",
                        Toast.LENGTH_SHORT
                    ).show()
                })
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) {

                } else if (response.code == 400) {
                    this@RoomActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@RoomActivity,
                            "Unexpected problem. Please log in again.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@RoomActivity, MainActivity::class.java)
                        startActivity(intent);
                    })

                } else if (response.code == 401) {
                    this@RoomActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@RoomActivity,
                            "Unexpected problem. Please log in again.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@RoomActivity, MainActivity::class.java)
                        startActivity(intent);
                    })
                }
            }
        })
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

}