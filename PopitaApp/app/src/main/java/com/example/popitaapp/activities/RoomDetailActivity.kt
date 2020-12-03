package com.example.popitaapp.activities

import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.popitaapp.R
import com.example.popitaapp.activities.models.Message
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_room_detail.*
import kotlinx.android.synthetic.main.activity_room_detail.back_btn
import kotlinx.android.synthetic.main.message_item_layout.view.*
import kotlinx.android.synthetic.main.message_item_layout_02.view.*
import kotlinx.android.synthetic.main.message_item_layout_03.view.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class RoomDetailActivity : AppCompatActivity() {

    //globally declare rv
    private lateinit var rv: RecyclerView

    private lateinit var adapter: GroupAdapter<GroupieViewHolder>

    companion object {
        val messages = ArrayList<Message>()
        var userName = ""
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

        userName = getIntent().getStringExtra("receiver_name").toString()

        //create adapter
        adapter = GroupAdapter()

        rv = findViewById(R.id.recyclerView2)
        rv.layoutManager = LinearLayoutManager(this@RoomDetailActivity, LinearLayoutManager.VERTICAL, false)
        rv.adapter = adapter

        //get json and match messages to specified view
        //if room_id is not 0 means, that action is called from RoomActivity
        if (room_id != 0) {
            fetchJson(room_id)

            //get info based on receiver_id and update ui thread
            //val receiver_id = getIntent().getIntExtra("receiver_id", 0)
            //getParticipantInfo(receiver_id)
        }

        //if is, get room_id from specified endpoint
        else {
            val user_id = getIntent().getIntExtra("user_id", 0)
            getRoom(user_id)
        }

        //new message util
        SendButton.setOnClickListener {
            sendMessage()
        }

    }

    private fun sendMessage() {

        //get auth token
        val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
        val auth_token = sharedPreference.getString("auth_token", null)

        val receiver_id = getIntent().getIntExtra("receiver_id", 0)
        val message = new_message.text.toString()
        new_message.text.clear()

        val jsonObject = JSONObject()
        jsonObject.put("receiver", receiver_id)
        jsonObject.put("message", message)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val ip = getString(R.string.server_ip)
        val url = "http://$ip/chat/message/new/"

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .post(body)
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
                if (response.code == 201) {

                    //get from response room id
                    val body = response.body?.string()
                    val jsonObject = JSONObject(body)
                    val new_room_id = jsonObject.getJSONObject("room").getInt("id")

                    //and refresh current messages
                    fetchJson(new_room_id)

                } else if (response.code == 400) {
                    this@RoomDetailActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                                this@RoomDetailActivity,
                                "400 Unable to get messages with provided credentials. Please log in or register first.",
                                Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@RoomDetailActivity, MainActivity::class.java)
                        startActivity(intent);
                    })

                } else if (response.code == 401) {
                    this@RoomDetailActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                                this@RoomDetailActivity,
                                "401 Unable to get messages with provided credentials. Please log in or register first.",
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

        val ip = getString(R.string.server_ip)
        val url = "http://$ip/chat/rooms/$room_id/messages/"

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
                            "400 Unable to get messages with provided credentials. Please log in or register first.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@RoomDetailActivity, MainActivity::class.java)
                        startActivity(intent);
                    })

                } else if (response.code == 401) {
                    this@RoomDetailActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@RoomDetailActivity,
                            "401 Unable to get messages with provided credentials. Please log in or register first.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@RoomDetailActivity, MainActivity::class.java)
                        startActivity(intent);
                    })

                }
            }
        })
    }

    fun getRoom(user_id: Int) {
        //get auth token
        val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
        val auth_token = sharedPreference.getString("auth_token", null)

        val ip = getString(R.string.server_ip)
        val url = "http://$ip/chat/rooms/?receivers=$user_id"

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

                    //get receiver id from json response
                    val body = response.body?.string()
                    val jsonObject = JSONObject(body)

                    //and call fetchJson to update messages(because we know, that room with
                    //current user exists
                    val results = jsonObject.getJSONArray("results")
                    val room_id = results.getJSONObject(0).getInt("id")

                    fetchJson(room_id)

                } else if (response.code == 400) {
                    this@RoomDetailActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                                this@RoomDetailActivity,
                                "400 Unable to get messages with provided credentials. Please log in or register first.",
                                Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@RoomDetailActivity, MainActivity::class.java)
                        startActivity(intent);
                    })

                } else if (response.code == 401) {
                    this@RoomDetailActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                                this@RoomDetailActivity,
                                "401 Unable to get messages with provided credentials. Please log in or register first.",
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

        //update recyclerview
        rv.invalidate()

        //update adapter on ui thread
        this@RoomDetailActivity.runOnUiThread(Runnable {

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
                        adapter.add(ChatFromItem(i, userName))
                    }
                    //if there is more than 1 message
                    else if (messages.lastIndex >= 0) {

                        //get count of items
                        val adapterCount = adapter.groupCount

                        //check last item and his layout
                        //if last item layout is with icon, add without
                        if (adapter.getItem(adapterCount - 1).layout == R.layout.message_item_layout) {
                            adapter.add(ChatFromItem_02(i))
                        } else if (adapter.getItem(adapterCount - 1).layout == R.layout.message_item_layout_03) {
                            adapter.add(ChatFromItem(i, userName))
                        } else {
                            adapter.add(ChatFromItem(i, userName))
                        }
                    }
                }
            }

            adapter.notifyDataSetChanged()

        })


        //TODO if getMessages is empty, move to ANOTHER view

    }
}

class ChatFromItem(private val message: Message, private val name: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtMessage.text = message.message
        viewHolder.itemView.txtFirstLetter.text = name
        println("CHAT FROM ITEM INIT")

    }

    override fun getLayout() = R.layout.message_item_layout

}

class ChatFromItem_02(private val message: Message): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtMessage2.text = message.message
        println("CHAT FROM ITEM 2 INIT")
    }

    override fun getLayout() = R.layout.message_item_layout_02

}

class ChatItem(private val message: Message): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtMessage3.text = message.message
        println("CHAT FROM ITEM 3 INIT")

    }

    override fun getLayout() = R.layout.message_item_layout_03
    }
