package com.example.popitaapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.popitaapp.R
import com.example.popitaapp.activities.RoomActivity.Companion.users
import com.example.popitaapp.activities.adapters.ExploreAdapter
import com.example.popitaapp.activities.adapters.OnExploreItemClickListener
import com.example.popitaapp.activities.adapters.RoomAdapter
import com.example.popitaapp.activities.models.Explore
import com.example.popitaapp.activities.models.Room
import kotlinx.android.synthetic.main.activity_room.*
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

                    getRooms(jsonObject)

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

            users.add(Explore(id, last_message, last_sender, receiver_id, receiver_name, last_message_timestamp))
        }

        //TODO if getRooms is empty, move to ANOTHER view

    }

    override fun onItemClick(item: Explore, position: Int) {
        val intent = Intent(this, ExploreDetailActivity::class.java)
        intent.putExtra("receiver_name", item.receiver_name)
        intent.putExtra("id", item.id)
        startActivity(intent)
    }

}