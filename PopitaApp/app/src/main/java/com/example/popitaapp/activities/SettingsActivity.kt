package com.example.popitaapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.popitaapp.R
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.menuExplore
import kotlinx.android.synthetic.main.activity_settings.menuMessages
import kotlinx.android.synthetic.main.activity_settings.menuProfile
import kotlinx.android.synthetic.main.activity_settings.menuSettings
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //profile activity on click
        profile.setOnClickListener {
            val intent = Intent(this, SettingsChangeProfileActivity::class.java)
            startActivity(intent);
        }

        //password activity on click
        password.setOnClickListener {
            val intent = Intent(this, SettingsChangePasswordActivity::class.java)
            startActivity(intent);
        }

        //privacy activity on click
        privacy.setOnClickListener {
            val intent = Intent(this, SettingsPrivacyActivity::class.java)
            startActivity(intent);
        }

        //menu

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

        menuSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

    }

    fun getMyProfileId() {

        //get auth token
        val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
        val auth_token = sharedPreference.getString("auth_token", null)

        //val url = "http://192.168.0.5:8000/localization/localization/"
        val url = "http://192.168.0.101:8000/auth/users/me/"

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Token " + auth_token.toString())
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@SettingsActivity.runOnUiThread(Runnable {
                    Toast.makeText(
                        this@SettingsActivity,
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
                    this@SettingsActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@SettingsActivity,
                            "Unexpected problem. Please log in again.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                        startActivity(intent);
                    })

                } else if (response.code == 401) {
                    this@SettingsActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@SettingsActivity,
                            "Unexpected problem. Please log in again.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@SettingsActivity, MainActivity::class.java)
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

        //val url = "http://192.168.0.5:8000/localization/localization/"
        val url = "http://192.168.0.101:8000/auth/profiles/$user_id/"

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Token " + auth_token.toString())
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@SettingsActivity.runOnUiThread(Runnable {
                    Toast.makeText(
                        this@SettingsActivity,
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
                    val intent = Intent(this@SettingsActivity, MyProfileActivity::class.java)
                    intent.putExtra("user_id", user_id)
                    intent.putExtra("first_name", first_name)
                    intent.putExtra("gender", gender)
                    intent.putExtra("background_color", background_color)
                    intent.putExtra("job", job)
                    intent.putExtra("preferred_drink", preferred_drink)
                    intent.putExtra("description", description)

                    startActivity(intent)

                } else if (response.code == 400) {
                    this@SettingsActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@SettingsActivity,
                            "Unexpected problem. Please log in again.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                        startActivity(intent);
                    })

                } else if (response.code == 401) {
                    this@SettingsActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@SettingsActivity,
                            "Unexpected problem. Please log in again.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                        startActivity(intent);
                    })
                }
            }
        })
    }

}