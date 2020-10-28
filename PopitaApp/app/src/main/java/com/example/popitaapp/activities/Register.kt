package com.example.popitaapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
                sign_up_btn.error = "Name required"
                sign_up_btn.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                email_input.error = "Email required"
                email_input.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                password_input.error = "Password required"
                password_input.requestFocus()
                return@setOnClickListener
            }

            println("XXXXXCccccc")
            fetchJson(name, email, password)
        }
    }

    fun fetchJson(name: String, email: String, password: String) {

        println(":)))")

        val url = "http://192.168.0.101:8000/auth/users/"
        val payload = "{\n" +
                "    \"first_name\": \"greg\",\n" +
                "    \"email\": \"grzegorz1.piechnik99@gmail.com\",\n" +
                "    \"password\": \"J493jirg\",\n" +
                "    \"re_password\": \"J493jirg\"\n" +
                "}"

        val jsonObject = JSONObject()
        jsonObject.put("first_name", "greg")
        jsonObject.put("email", "emailm2e@onet.pl")
        jsonObject.put("password", "J493jirg")
        jsonObject.put("re_password", "J493jirg")

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val okHttpClient = OkHttpClient()
        //val requestBody = payload.toRequestBody()
        val request = Request.Builder()
                //.method("POST", requestBody)
                .url(url)
                .post(body)
                .build()
        println("DODOD")
        println(request)
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("XXcccccc")
                println(e)
                println("DDDDDXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
                // Handle this
            }

            override fun onResponse(call: Call, response: Response) {
                println(response)
                println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
                // Handle this
            }
        })



    }

}