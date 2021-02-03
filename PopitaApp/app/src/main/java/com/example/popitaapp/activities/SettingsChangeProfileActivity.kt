package com.example.popitaapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.popitaapp.R
import kotlinx.android.synthetic.main.activity_settings_change_password.back_btn
import kotlinx.android.synthetic.main.activity_settings_change_profile.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class SettingsChangeProfileActivity : AppCompatActivity() {

    companion object {
        var gender = String?: ""
        var background = String?: ""
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
        gender = getIntent().getStringExtra("gender").toString()

        if (gender == "Male") {
            genderImage.setImageResource(R.drawable.ic_gender_male)
        } else {
            genderImage.setImageResource(R.drawable.ic_gender_female)
        }

        //set gender spinner
        ArrayAdapter.createFromResource(
                this,
                R.array.gender,
                R.layout.gender_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            // Apply the adapter to the spinner
            genderSpinner.adapter = adapter
        }

        //get id of a gender in genders list
        val genders = getResources().getStringArray(R.array.gender)
        val idOfGender = genders.indexOf(gender)

        //and set a proper gender
        genderSpinner.setSelection(idOfGender)

        genderSpinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                //change color of a new background based on
                gender = genders.get(position)

                if (gender == "Male") {
                    genderImage.setImageResource(R.drawable.ic_gender_male)
                } else {
                    genderImage.setImageResource(R.drawable.ic_gender_female)
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        })

        //change background color
        background = getIntent().getStringExtra("background_color").toString()

        if (background == "Orange") {
            root.setBackgroundColor(getResources().getColor(R.color.orange))
        } else if (background == "Blue") {
            root.setBackgroundColor(getResources().getColor(R.color.blue))
        } else if (background == "Green") {
            root.setBackgroundColor(getResources().getColor(R.color.green))
        } else if (background == "Yellow") {
            root.setBackgroundColor(getResources().getColor(R.color.yellow))
        } else if (background == "Pink") {
            root.setBackgroundColor(getResources().getColor(R.color.pink))
        }

        //set background spinner
        ArrayAdapter.createFromResource(
                this,
                R.array.backgroundChoices,
                R.layout.background_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            // Apply the adapter to the spinner
            backgroundSpinner.adapter = adapter
        }

        //get id of a background in genders list
        val backgrounds = getResources().getStringArray(R.array.backgroundChoices)
        val idOfBackground = backgrounds.indexOf(background)

        //and set a proper background
        backgroundSpinner.setSelection(idOfBackground)

        backgroundSpinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                //change color of a new background based on
                background = backgrounds.get(position)

                if (background == "Orange") {
                    root.setBackgroundColor(getResources().getColor(R.color.orange))
                } else if (background == "Blue") {
                    root.setBackgroundColor(getResources().getColor(R.color.blue))
                } else if (background == "Green") {
                    root.setBackgroundColor(getResources().getColor(R.color.green))
                } else if (background == "Yellow") {
                    root.setBackgroundColor(getResources().getColor(R.color.yellow))
                } else if (background == "Pink") {
                    root.setBackgroundColor(getResources().getColor(R.color.pink))
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        })

        //set other info
        jobText.setText(getIntent().getStringExtra("job"))
        preferred_drinkText.setText(getIntent().getStringExtra("preferred_drink"))
        descriptionText.setText(getIntent().getStringExtra("description"))

        //save data button
        saveBtn.setOnClickListener {
            changeData()
        }

    }

    private fun changeData() {

        //get auth token
        val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
        val auth_token = sharedPreference.getString("auth_token", null)

        val ip = getString(R.string.server_ip)
        val user_id = getIntent().getIntExtra("user_id", 0)
        val url = "http://$ip/auth/profiles/$user_id/"

        val jsonObject = JSONObject()

        //get data from EditText
        val first_name = firstNameText.text.toString().trim()
        val job = jobText.text.toString().trim()
        val preferred_drink = preferred_drinkText.text.toString().trim()
        val description = descriptionText.text.toString().trim()

        jsonObject.put("first_name", first_name)
        jsonObject.put("gender", gender)
        jsonObject.put("background_color", background)
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