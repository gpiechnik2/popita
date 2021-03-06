package com.example.popitaapp.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.popitaapp.R
import com.example.popitaapp.activities.adapters.ExploreAdapter
import com.example.popitaapp.activities.adapters.OnExploreItemClickListener
import com.example.popitaapp.activities.adapters.RoomAdapter
import com.example.popitaapp.activities.models.Explore
import com.example.popitaapp.activities.models.User
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_explore_detail.*
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.explore_item_layout.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class ExploreActivity : AppCompatActivity(), OnExploreItemClickListener {

    // declare a global variable FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // globally declare LocationRequest
    private lateinit var locationRequest: LocationRequest

    // globally declare LocationCallback
    private lateinit var locationCallback: LocationCallback

    // globally declare RecyclerView
    private lateinit var rv: RecyclerView

    //globally declare adapter
    private lateinit var adapter: ExploreAdapter

    // globally declare main handler
    lateinit var mainHandler: Handler

    //update rooms every 120 seconds
    private val updatePeopleNear = object : Runnable {
        override fun run() {
            getPeopleNearJson()
            mainHandler.postDelayed(this, 120000)
        }
    }

    companion object {
        val users = ArrayList<Explore>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

        //messages button
        menuMessages.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, RoomActivity::class.java)
            startActivity(intent);
        }

        //explore button
        menuExplore.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, ExploreActivity::class.java)
            startActivity(intent);
        }

        //profile button
        menuProfile.setOnClickListener {
            getMyProfileInfo()
        }

        //settings
        menuSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        //connect with Room model
        rv = findViewById(R.id.recyclerView1)
        rv.layoutManager = LinearLayoutManager(this@ExploreActivity, LinearLayoutManager.VERTICAL, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //init updating user location
        getLocationUpdates()

        //create adapter
        adapter = ExploreAdapter(users, this@ExploreActivity)
        rv.adapter = adapter

        //init Main handler to get updates in Explore activity
        mainHandler = Handler(Looper.getMainLooper())
    }

    private fun getPeopleNearJson() {

        //get auth token
        val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
        val auth_token = sharedPreference.getString("auth_token", null)

        val ip = getString(R.string.server_ip)
        val url = "http://$ip/localizations/"

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Token " + auth_token.toString())
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@ExploreActivity.runOnUiThread(Runnable {
                    Toast.makeText(
                        this@ExploreActivity,
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

                    getLocalUsers(jsonObject)

                    //TODO https://github.com/square/okhttp/issues/2311

                } else if (response.code == 400) {
                    this@ExploreActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@ExploreActivity,
                            "Unexpected problem. Please log in again.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@ExploreActivity, MainActivity::class.java)
                        startActivity(intent);
                    })

                } else if (response.code == 401) {
                    this@ExploreActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@ExploreActivity,
                            "Unexpected problem. Please log in again.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@ExploreActivity, MainActivity::class.java)
                        startActivity(intent);
                    })
                }
            }
        })
    }

    private fun updateUserLocation(location: Location, address: String) {

        //get auth token
        val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
        val auth_token = sharedPreference.getString("auth_token", null)

        val ip = getString(R.string.server_ip)
        val url = "http://$ip/localizations/"

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
                this@ExploreActivity.runOnUiThread(Runnable {
                    Toast.makeText(
                        this@ExploreActivity,
                        "Unexpected problem.",
                        Toast.LENGTH_SHORT
                    ).show()
                })
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) {

                } else if (response.code == 400) {
                    this@ExploreActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@ExploreActivity,
                            "Unable to get chat rooms with provided credentials. Please log in or register first.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@ExploreActivity, MainActivity::class.java)
                        startActivity(intent);
                    })

                } else if (response.code == 401) {
                    this@ExploreActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@ExploreActivity,
                            "Unable to get chat rooms with provided credentials. Please log in or register first.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@ExploreActivity, MainActivity::class.java)
                        startActivity(intent);
                    })
                }
            }
        })
    }

    fun getLocalUsers(results: JSONObject) {

        //clear to not to duplicate records
        users.clear()

        //iterate over results and append them to array
        val roomResults = results.getJSONArray("results")

        for (i in 0 until roomResults.length()) {

            val id = roomResults.getJSONObject(i).getInt("id")

            val user_id = roomResults.getJSONObject(i).getJSONObject("user").getInt("id")
            val user_name = roomResults.getJSONObject(i).getJSONObject("user").getString("first_name")
            val user = User(user_id, user_name)

            val longitude = roomResults.getJSONObject(i).getDouble("longitude").toFloat()
            val latitude = roomResults.getJSONObject(i).getDouble("latitude").toFloat()
            val attitude = roomResults.getJSONObject(i).getDouble("attitude").toFloat()
            val location = roomResults.getJSONObject(i).getString("location")
            val timestamp = roomResults.getJSONObject(i).getString("timestamp")
            val distance = roomResults.getJSONObject(i).getDouble("distance").toFloat()

            users.add(Explore(id, user, longitude, latitude, attitude, location, timestamp, distance))
        }

        //update users
        rv.invalidate()

        //update adapter on ui thread
        this@ExploreActivity.runOnUiThread(Runnable {
            adapter.notifyDataSetChanged()
        })

        //TODO if getRooms is empty, move to ANOTHER view

    }

    override fun onItemClick(item: Explore, position: Int) {
        val intent = Intent(this, ExploreDetailActivity::class.java)
        intent.putExtra("id", item.id)
        intent.putExtra("user_id", item.user.id)
        intent.putExtra("user_first_name", item.user.first_name)
        intent.putExtra("longitude", item.longitude)
        intent.putExtra("latitude", item.latitude)
        intent.putExtra("distance", item.distance)
        intent.putExtra("location", item.location)
        intent.putExtra("timestamp", item.timestamp)

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

                    //update users near
                    getPeopleNearJson()
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
        mainHandler.removeCallbacks(updatePeopleNear)
    }

    // start receiving location update when activity  visible/foreground
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
        mainHandler.post(updatePeopleNear)
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

        return address
    }

    fun getMyProfileInfo() {

        //get auth token
        val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
        val auth_token = sharedPreference.getString("auth_token", null)

        val ip = getString(R.string.server_ip)
        val url = "http://$ip/auth/profiles/me/"

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .header("Authorization", "Token " + auth_token.toString())
                .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@ExploreActivity.runOnUiThread(Runnable {
                    Toast.makeText(
                            this@ExploreActivity,
                            "Unexpected problem.",
                            Toast.LENGTH_SHORT
                    ).show()
                })
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) {

                    //get user id from response
                    val body = response.body?.string()
                    val jsonObject = JSONObject(body)

                    //val user_id = jsonObject.getInt("id")
                    val user_id = jsonObject.getString("user_id")
                    val first_name = jsonObject.getString("first_name")
                    val gender = jsonObject.getString("gender")
                    val background_color = jsonObject.getString("background_color")
                    val job = jsonObject.getString("job")
                    val preferred_drink = jsonObject.getString("preferred_drink")
                    val description = jsonObject.getString("description")

                    // Handler code here.
                    val intent = Intent(this@ExploreActivity, MyProfileActivity::class.java)
                    intent.putExtra("user_id", user_id)
                    intent.putExtra("first_name", first_name)
                    intent.putExtra("gender", gender)
                    intent.putExtra("background_color", background_color)
                    intent.putExtra("job", job)
                    intent.putExtra("preferred_drink", preferred_drink)
                    intent.putExtra("description", description)

                    startActivity(intent)

                } else if (response.code == 400) {
                    this@ExploreActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                                this@ExploreActivity,
                                "Unexpected problem. Please log in again.",
                                Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@ExploreActivity, MainActivity::class.java)
                        startActivity(intent);
                    })

                } else if (response.code == 401) {
                    this@ExploreActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                                this@ExploreActivity,
                                "Unexpected problem. Please log in again.",
                                Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@ExploreActivity, MainActivity::class.java)
                        startActivity(intent);
                    })
                }
            }
        })
    }

}