package com.example.popitaapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.popitaapp.R
import com.example.popitaapp.activities.adapters.MessageAdapter
import com.example.popitaapp.activities.models.Message
import com.example.popitaapp.activities.models.Room
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class RoomDetailActivity : AppCompatActivity() {

    companion object {
        val messages = ArrayList<Message>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_detail)

        //back button
        var back_btn = findViewById(R.id.back_btn) as Button
        back_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, RoomActivity::class.java)
            startActivity(intent);
        }

        //change receiver name
        var receiver_name_text = findViewById(R.id.receiver_name_text) as TextView
        receiver_name_text.text = getIntent().getStringExtra("receiver_name")

        //get id of a room
        val room_id = getIntent().getIntExtra("id", 0)

        //connect with Message model
        val rv = findViewById<RecyclerView>(R.id.recyclerView2)
        rv.layoutManager = LinearLayoutManager(this@RoomDetailActivity, LinearLayoutManager.VERTICAL, false)

        //get json with chats info and append results to val messages
        fetchJson(room_id)

        //create adapter
        var adapter = MessageAdapter(messages)
        rv.adapter = adapter

        }

        //TODO connect to endpoint connected with room_id
        private fun fetchJson(room_id: Int) {

            //get auth token
            val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
            val auth_token = sharedPreference.getString("auth_token", null)

            val url = "http://192.168.31.19:8000/chat/rooms/"
            //val url = "http://192.168.0.101:8000/chat/rooms/"

            val okHttpClient = OkHttpClient()
            val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "Token " + auth_token.toString())
                    .build()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    this@RoomDetailActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                                this@RoomDetailActivity,
                                "Getting messages failed.",
                                Toast.LENGTH_SHORT
                        ).show()
                    })
                    println(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code == 200) {

                        val body = response.body?.string()
                        val jsonObject = JSONObject(body)
                        val results = jsonObject.get("results")

                        println(jsonObject)
                        println(results)

                        getMessages(jsonObject)

                    } else if (response.code == 400) {
                        this@RoomDetailActivity.runOnUiThread(Runnable {
                            Toast.makeText(
                                    this@RoomDetailActivity,
                                    "Unable to get messages with provided credentials. Please log in or register first.",
                                    Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this@RoomDetailActivity, MainActivity::class.java)
                            startActivity(intent);
                        })

                    } else if (response.code == 401) {
                        this@RoomDetailActivity.runOnUiThread(Runnable {
                            Toast.makeText(
                                    this@RoomDetailActivity,
                                    "Unable to get messages with provided credentials. Please log in or register first.",
                                    Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this@RoomDetailActivity, MainActivity::class.java)
                            startActivity(intent);
                        })

                    } else {
                        this@RoomDetailActivity.runOnUiThread(Runnable {
                            Toast.makeText(
                                    this@RoomDetailActivity,
                                    "Unable to log in.",
                                    Toast.LENGTH_SHORT
                            ).show()
                        })
                    }


                }
            })
        }

        fun getMessages(results: JSONObject) {

            println("CXCXCCX")
            println(results)
            println("XCCXZCXZXCZCXZCXZZ")

            //actions
            messages.add(Message("Paul", "Mdsa das dasd asr"))

            //TODO if getMessages is empty, move to ANOTHER view

        }
}