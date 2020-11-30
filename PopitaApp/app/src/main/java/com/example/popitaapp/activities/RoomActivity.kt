package com.example.popitaapp.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.Adapter
import android.widget.AdapterView
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
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
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

    //globally declare adapter
    private lateinit var adapter: RoomAdapter

    // globally declare main handler
    lateinit var mainHandler: Handler

    //update rooms every 40 seconds
    private val updateRooms = object : Runnable {
        override fun run() {
            getRoomsFromJson()
            mainHandler.postDelayed(this, 40000)
        }
    }

    //globally declare rv
    private lateinit var rv: RecyclerView

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

        //profile button
        menuProfile.setOnClickListener {
            getMyProfileId()
        }

        //settings button
        menuSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        //connect with Room model
        rv = findViewById(R.id.recyclerView1)
        rv.layoutManager = LinearLayoutManager(this@RoomActivity, LinearLayoutManager.VERTICAL, false)

        //updates of your location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationUpdates()

        //create adapter
        adapter = RoomAdapter(users, this@RoomActivity)
        rv.adapter = adapter

        //init Main handler to get updates in room activity
        mainHandler = Handler(Looper.getMainLooper())

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

    private fun getRoomsFromJson() {

        //get auth token
        val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
        val auth_token = sharedPreference.getString("auth_token", null)

        val ip = getString(R.string.server_ip)
        val url = "http://$ip/chat/rooms/"

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

        //update recyclerview
        rv.invalidate()

        //update adapter by calling him in ui thread
        this@RoomActivity.runOnUiThread(Runnable {
            adapter.notifyDataSetChanged()
        })

        //TODO if getRooms is empty, move to ANOTHER view

    }

    override fun onItemClick(item: Room, position: Int) {
        val intent = Intent(this, RoomDetailActivity::class.java)
        intent.putExtra("receiver_name", item.receiver_name)
        intent.putExtra("receiver_id", item.receiver_id)
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

        val ip = getString(R.string.server_ip)
        val url = "http://$ip/localization/localization/"

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
        mainHandler.removeCallbacks(updateRooms)
    }

    // start receiving location update when activity  visible/foreground
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
        mainHandler.post(updateRooms)
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

    private val updateRoomsList = object : Runnable {
        override fun run() {
            val newRooms = getRoomsFromJson()

            mainHandler.postDelayed(this, 50000)
        }
    }

    fun getMyProfileId() {

        //get auth token
        val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
        val auth_token = sharedPreference.getString("auth_token", null)

        val ip = getString(R.string.server_ip)
        val url = "http://$ip/auth/users/me/"

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

                    //get user id from response
                    val body = response.body?.string()
                    val jsonObject = JSONObject(body)
                    val user_id = jsonObject.getInt("id")

                    getMyProfileInfo(user_id)

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

    fun getMyProfileInfo(user_id: Int) {

        //get auth token
        val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
        val auth_token = sharedPreference.getString("auth_token", null)

        val ip = getString(R.string.server_ip)
        val url = "http://$ip/auth/profiles/$user_id/"

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

                    //get user id from response
                    val body = response.body?.string()
                    val jsonObject = JSONObject(body)

                    //val user_id = jsonObject.getInt("id")
                    val first_name = jsonObject.getString("first_name")
                    val gender = jsonObject.getString("gender")
                    val background_color = jsonObject.getString("background_color")
                    val job = jsonObject.getString("job")
                    val preferred_drink = jsonObject.getString("preferred_drink")
                    val description = jsonObject.getString("description")

                    // Handler code here.
                    val intent = Intent(this@RoomActivity, MyProfileActivity::class.java)
                    intent.putExtra("user_id", user_id)
                    intent.putExtra("first_name", first_name)
                    intent.putExtra("gender", gender)
                    intent.putExtra("background_color", background_color)
                    intent.putExtra("job", job)
                    intent.putExtra("preferred_drink", preferred_drink)
                    intent.putExtra("description", description)

                    startActivity(intent)

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
}