package com.example.popitaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.popitaapp.R

class Login_with_email : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_with_email)

        //login form
        val sign_in_btn = findViewById(R.id.sign_in_btn) as Button
        val email_input = findViewById(R.id.email_address) as EditText
        val password_input = findViewById(R.id.password) as EditText

        sign_in_btn.setOnClickListener {

            val email_address = email_input.text.toString().trim()
            val password = password_input.text.toString().trim()

            if (email_address.isEmpty()){
                email_input.error = "Email required"
                email_input.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()){
                password_input.error = "Password required"
                password_input.requestFocus()
                return@setOnClickListener
            }



        }

        //back button
        var back_btn = findViewById(R.id.back_btn) as Button
        back_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, Login_choices::class.java)
            startActivity(intent);
        }

    }
}