package com.example.popitaapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.popitaapp.R
import com.example.popitaapp.activities.adapters.ExploreAdapter
import com.example.popitaapp.activities.adapters.OnExploreItemClickListener
import com.example.popitaapp.activities.models.Explore
import com.example.popitaapp.activities.models.User
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.explore_item_layout.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class ExploreActivity : AppCompatActivity(), OnExploreItemClickListener {

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

        //messages button
        menuExplore.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, ExploreActivity::class.java)
            startActivity(intent);
        }






        //connect with Room model
        val rv = findViewById<RecyclerView>(R.id.recyclerView1)
        rv.layoutManager = LinearLayoutManager(this@ExploreActivity, LinearLayoutManager.VERTICAL, false)

        fetchJson()

        //2 seconds delay to process fetchJson
        runBlocking {     // but this expression blocks the main thread
            delay(2000L)  // ... while we delay for 2 seconds to keep JVM alive
        }

        var adapter = ExploreAdapter(users, this@ExploreActivity)
        rv.adapter = adapter

    }

    private fun fetchJson() {

        //get auth token
        val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
        val auth_token = sharedPreference.getString("auth_token", null)

        //val url = "http://192.168.0.5:8000/localization/localization/"
        val url = "http://192.168.0.101:8000/localization/localization/"

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

                } else {
                    this@ExploreActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@ExploreActivity,
                            "Unable to log in.",
                            Toast.LENGTH_SHORT
                        ).show()
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

}