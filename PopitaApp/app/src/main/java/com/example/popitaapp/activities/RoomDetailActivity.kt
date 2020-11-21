package com.example.popitaapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.popitaapp.R
import com.example.popitaapp.activities.models.Message
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_room_detail.*
import kotlinx.android.synthetic.main.message_item_layout.view.*
import kotlinx.android.synthetic.main.message_item_layout_02.view.*
import kotlinx.android.synthetic.main.message_item_layout_03.view.*
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Runnable

class RoomDetailActivity : AppCompatActivity() {

    companion object {
        val messages = ArrayList<Message>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_detail)

        //back button
        back_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, RoomActivity::class.java)
            startActivity(intent);
        }

        //change receiver name
        receiver_name.text = getIntent().getStringExtra("receiver_name")

        //get id of a room
        val room_id = getIntent().getIntExtra("id", 0)

        //TODO update adapter messages in async requests to database for messages with specified id
        //create adapter
        val adapter = GroupAdapter<GroupieViewHolder>()
        recyclerView2.layoutManager = LinearLayoutManager(this@RoomDetailActivity, LinearLayoutManager.VERTICAL, false)

        //get json and match messages to specified view
        fetchJson(room_id)

        //2 seconds delay to process fetchJson
        runBlocking {     // but this expression blocks the main thread
            delay(2000L)  // ... while we delay for 2 seconds to keep JVM alive
        }

        for (i in messages) {
            println(i)
            //if you are sender, add new message
            if (i.receiver == 0) {
                adapter.add(ChatItem(i))
            }
            //i not, check previous message
            else {
                //if its first message,
                if (messages.lastIndex < 0) {
                    adapter.add(ChatFromItem(i))
                }
                //if there is only 1 message
                else if (messages.lastIndex >= 0) {
                    //and you are receiver(1)
                    if (messages.last().receiver == 1) {
                        adapter.add(ChatFromItem_02(i))
                    }
                }
            }
        }

        recyclerView2.adapter = adapter

        //new message util


    }

    private fun sendMessage(room_id: Int) {

        //get auth token
        val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
        val auth_token = sharedPreference.getString("auth_token", null)

        //val url = "http://192.168.0.5:8000/chat/rooms/$room_id/messages/"
        val url = "http://192.168.0.101:8000/chat/rooms/$room_id/messages/"

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
                            "Sending message failed.",
                            Toast.LENGTH_SHORT
                    ).show()
                })
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) {
                    //refresh activity

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
                }
            }
        })
    }

    private fun fetchJson(room_id: Int) {

        //get auth token
        val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
        val auth_token = sharedPreference.getString("auth_token", null)

        //val url = "http://192.168.0.5:8000/chat/rooms/$room_id/messages/"
        val url = "http://192.168.0.101:8000/chat/rooms/$room_id/messages/"

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

                }
            }
        })
    }

    fun getMessages(results: JSONObject) {

        //clear not to duplicate message records
        messages.clear()

        val message_results = results.getJSONArray("results")

        for (i in 0 until message_results.length()) {
            val id = message_results.getJSONObject(i).getInt("id")
            val receiver = message_results.getJSONObject(i).getInt("receiver")
            val message = message_results.getJSONObject(i).getString("message")
            val timestamp = message_results.getJSONObject(i).getString("timestamp")

            messages.add(Message(id, receiver, message, timestamp))
        }

        //TODO if getMessages is empty, move to ANOTHER view

    }
}

class ChatFromItem(private val message: Message): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtMessage.text = message.message
    }

    override fun getLayout() = R.layout.message_item_layout

}

class ChatFromItem_02(private val message: Message): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtMessage2.text = message.message
    }

    override fun getLayout() = R.layout.message_item_layout_02

}

class ChatItem(private val message: Message): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtMessage3.text = message.message
    }

    override fun getLayout() = R.layout.message_item_layout_03
    }
