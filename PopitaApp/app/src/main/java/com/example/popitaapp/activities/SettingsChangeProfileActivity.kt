package com.example.popitaapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.popitaapp.R
import com.example.popitaapp.activities.models.Room
import kotlinx.android.synthetic.main.activity_settings_change_password.back_btn
import kotlinx.android.synthetic.main.activity_settings_change_profile.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class SettingsChangeProfileActivity : AppCompatActivity() {

    companion object {
        val newGender = String
        val newBackground = String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_change_profile)

        //back button
        back_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent);
        }

        //change EditText info
        firstNameText.setText(getIntent().getStringExtra("first_name"))

        //set dynamically image based on user info
        val gender = getIntent().getStringExtra("gender")

        if (gender == "M") {
            genderImage.setImageResource(R.drawable.ic_gender_male)
        } else {
            genderImage.setImageResource(R.drawable.ic_gender_female)
        }

        //set gender spinner
        ArrayAdapter.createFromResource(
                this,
                R.array.gender,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            genderSpinner.adapter = adapter
        }

        //set gender spinner
        ArrayAdapter.createFromResource(
                this,
                R.array.backgroundChoices,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            backgroundSpinner.adapter = adapter
        }

        //set other info
        jobText.setText(getIntent().getStringExtra("job"))
        preferred_drinkText.setText(getIntent().getStringExtra("preferred_drink"))
        descriptionText.setText(getIntent().getStringExtra("description"))

        saveBtn.setOnClickListener {
            changeData()
        }

    }

    private fun changeData() {

        //get auth token
        val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
        val auth_token = sharedPreference.getString("auth_token", null)

        //val url = "http://192.168.0.5:8000/chat/rooms/"
        val url = "http://192.168.0.101:8000/chat/rooms/"

        val jsonObject = JSONObject()

        //get data from EditText
        val first_name = firstNameText.text.toString().trim()
        val job = jobText.text.toString().trim()
        val preferred_drink = preferred_drinkText.text.toString().trim()
        val description = descriptionText.text.toString().trim()

        jsonObject.put("first_name", first_name)
        jsonObject.put("gender", newGender)
        jsonObject.put("background_color", newBackground)
        jsonObject.put("job", job)
        jsonObject.put("preferred_drink", preferred_drink)
        jsonObject.put("description", description)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .patch(body)
                .header("Authorization", "Token " + auth_token.toString())
                .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@SettingsChangeProfileActivity.runOnUiThread(Runnable {
                    Toast.makeText(
                            this@SettingsChangeProfileActivity,
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


                } else if (response.code == 404) {
                    this@SettingsChangeProfileActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                                this@SettingsChangeProfileActivity,
                                "Unable to change your data with provided credentials. Please log in or register first.",
                                Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@SettingsChangeProfileActivity, MainActivity::class.java)
                        startActivity(intent);
                    })

                } else if (response.code == 401) {
                    this@SettingsChangeProfileActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                                this@SettingsChangeProfileActivity,
                                "Unable to change your data with provided credentials. Please log in or register first.",
                                Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@SettingsChangeProfileActivity, MainActivity::class.java)
                        startActivity(intent);
                    })
                }
            }
        })
    }

}