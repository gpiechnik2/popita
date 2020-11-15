package com.example.popitaapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.popitaapp.R
import com.example.popitaapp.activities.adapters.OnRoomItemClickListener
import com.example.popitaapp.activities.adapters.RoomAdapter
import com.example.popitaapp.activities.models.Room
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class RoomActivity : AppCompatActivity(), OnRoomItemClickListener {

    companion object {
        val users = ArrayList<Room>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        //connect with Room model
        val rv = findViewById<RecyclerView>(R.id.recyclerView1)
        rv.layoutManager = LinearLayoutManager(this@RoomActivity, LinearLayoutManager.VERTICAL, false)

        fetchJson()

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

                } else {
                    this@RoomActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@RoomActivity,
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
}