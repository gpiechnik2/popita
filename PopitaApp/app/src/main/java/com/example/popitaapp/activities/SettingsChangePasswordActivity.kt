package com.example.popitaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.popitaapp.R
import kotlinx.android.synthetic.main.activity_settings_change_password.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class SettingsChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_change_password)

        //back button
        back_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent);
        }

        //register form
        val saveBtn = findViewById(R.id.saveBtn) as Button
        val oldPassword = findViewById(R.id.oldPassword) as EditText
        val newPassword = findViewById(R.id.newPassword) as EditText
        val confirmPassword = findViewById(R.id.confirmPassword) as EditText

        saveBtn.setOnClickListener {

            val password1 = oldPassword.text.toString().trim()
            val password2 = newPassword.text.toString().trim()
            val password3 = confirmPassword.text.toString().trim()

            if (password1.isEmpty()) {
                Toast.makeText(this@SettingsChangePasswordActivity, "Password required.", Toast.LENGTH_SHORT).show()
                oldPassword.requestFocus()
                return@setOnClickListener
            }

            if (password2.isEmpty()) {
                Toast.makeText(this@SettingsChangePasswordActivity, "Password required.", Toast.LENGTH_SHORT).show()
                newPassword.requestFocus()
                return@setOnClickListener
            }

            if (password3.isEmpty()) {
                Toast.makeText(this@SettingsChangePasswordActivity, "Password required.", Toast.LENGTH_SHORT).show()
                confirmPassword.requestFocus()
                return@setOnClickListener
            }

            if (password1 == password2) {
                Toast.makeText(this@SettingsChangePasswordActivity, "New password can not be the same as current.", Toast.LENGTH_SHORT).show()
                newPassword.requestFocus()
                return@setOnClickListener
            }

            if (password2 != password3) {
                Toast.makeText(this@SettingsChangePasswordActivity, "New passwords are different.", Toast.LENGTH_SHORT).show()
                newPassword.requestFocus()
                confirmPassword.requestFocus()
                return@setOnClickListener
            }

            savePassword(password1, password2)

        }
    }

    fun savePassword(oldPassword: String, newPassword: String) {

        //val url = "http://192.168.0.5:8000/auth/users/"
        val url = "http://192.168.0.101:8000/auth/users/set_password/"

        val jsonObject = JSONObject()
        jsonObject.put("current_password", oldPassword)
        jsonObject.put("new_password", newPassword)

        val body = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@SettingsChangePasswordActivity.runOnUiThread(Runnable {
                    Toast.makeText(
                        this@SettingsChangePasswordActivity,
                        "Changing password failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                })
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 204) {

                    this@SettingsChangePasswordActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@SettingsChangePasswordActivity,
                            "Password has been changed!",
                            Toast.LENGTH_SHORT
                        ).show()
                    })

                } else if (response.code == 400) {
                    this@SettingsChangePasswordActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@SettingsChangePasswordActivity,
                            "400 Unable to set password. Please log in or register first.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@SettingsChangePasswordActivity, MainActivity::class.java)
                        startActivity(intent);
                    })

                } else if (response.code == 401) {
                    this@SettingsChangePasswordActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@SettingsChangePasswordActivity,
                            "401 Unable to set password. Please log in or register first.",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@SettingsChangePasswordActivity, MainActivity::class.java)
                        startActivity(intent);
                    })

                }
            }
        })
    }
}