package com.example.popitaapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.popitaapp.R
import okhttp3.*
import java.io.IOException
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull


class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //back button
        var back_btn = findViewById(R.id.back_btn) as Button
        back_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent);
        }

        //register form
        val sign_up_btn = findViewById(R.id.sign_up_btn) as Button
        val email_input = findViewById(R.id.email) as EditText
        val password_input = findViewById(R.id.password) as EditText

        sign_up_btn.setOnClickListener {

            val name = sign_up_btn.text.toString().trim()
            val email = email_input.text.toString().trim()
            val password = password_input.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this@Register, "Name required.", Toast.LENGTH_SHORT).show()
                sign_up_btn.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                Toast.makeText(this@Register, "Email required.", Toast.LENGTH_SHORT).show()
                email_input.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this@Register, "Password required.", Toast.LENGTH_SHORT).show()
                password_input.requestFocus()
                return@setOnClickListener
            }

            fetchJson(name, email, password)
        }
    }

    fun fetchJson(name: String, email: String, password: String) {

        val ip = getString(R.string.server_ip)
        val url = "http://$ip/auth/users/"

        val jsonObject = JSONObject()
        jsonObject.put("first_name", name)
        jsonObject.put("email", email)
        jsonObject.put("password", password)
        jsonObject.put("re_password", password)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@Register.runOnUiThread(Runnable {
                    Toast.makeText(this@Register, "Unexpected error.", Toast.LENGTH_SHORT).show()
                })
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                this@Register.runOnUiThread(Runnable {
                    Toast.makeText(this@Register, "Your account has been created. Please log in.", Toast.LENGTH_SHORT).show()
                })
            }
        })
    }
}