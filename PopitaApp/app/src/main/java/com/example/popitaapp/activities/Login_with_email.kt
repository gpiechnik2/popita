package com.example.popitaapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.popitaapp.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class Login_with_email : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_with_email)

        //back button
        var back_btn = findViewById(R.id.back_btn) as Button
        back_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, Login_choices::class.java)
            startActivity(intent);
        }

        //login form
        val sign_in_btn = findViewById(R.id.sign_in_btn) as Button
        val email_input = findViewById(R.id.email_address) as EditText
        val password_input = findViewById(R.id.password) as EditText

        sign_in_btn.setOnClickListener {

            val email_address = email_input.text.toString().trim()
            val password = password_input.text.toString().trim()

            if (email_address.isEmpty()){
                this@Login_with_email.runOnUiThread(Runnable {
                    Toast.makeText(this@Login_with_email, "Email required.", Toast.LENGTH_SHORT).show()
                })
                email_input.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()){
                this@Login_with_email.runOnUiThread(Runnable {
                    Toast.makeText(this@Login_with_email, "Password required.", Toast.LENGTH_SHORT).show()
                })
                password_input.requestFocus()
                return@setOnClickListener
            }

            fetchJson(email_address, password)

        }

    }

    fun fetchJson(email: String, password: String) {

        val url = "http://192.168.31.19:8000/auth/token/login/"
        //val url = "http://192.168.0.101:8000/auth/token/login/"

        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password", password)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@Login_with_email.runOnUiThread(Runnable {
                    Toast.makeText(this@Login_with_email, "Creating account failed.", Toast.LENGTH_SHORT).show()
                })
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 200) {

                    val body = response.body?.string()
                    val jsonObject = JSONObject(body)
                    val auth_token = jsonObject.get("auth_token")

                    //instance and save method
                    val sharedPreference =  getSharedPreferences("AUTH_TOKEN", Context.MODE_PRIVATE)
                    var editor = sharedPreference.edit()
                    editor.putString("auth_token", auth_token.toString())
                    editor.commit()

                    //on-production only
                    //val authorization_token = sharedPreference.getString("auth_token", null)
                    //println(authorization_token)

                    this@Login_with_email.runOnUiThread(Runnable {
                        Toast.makeText(this@Login_with_email, "Successful log in.", Toast.LENGTH_SHORT).show()
                    })

                    //room activity
                    val intent = Intent(this@Login_with_email, RoomActivity::class.java)
                    startActivity(intent);

                }

                else if (response.code == 400) {
                    this@Login_with_email.runOnUiThread(Runnable {
                        Toast.makeText(this@Login_with_email, "Unable to log in with provided credentials.", Toast.LENGTH_SHORT).show()
                    })
                }

                else {
                    this@Login_with_email.runOnUiThread(Runnable {
                        Toast.makeText(this@Login_with_email, "Unable to log in.", Toast.LENGTH_SHORT).show()
                    })
                }


            }
        })
    }


}