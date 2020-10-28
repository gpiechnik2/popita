package com.example.popitaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import com.example.popitaapp.R

class Login_choices : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_choices)

        //back button
        var back_btn = findViewById(R.id.back_btn) as Button
        back_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent);
        }

        //email choice button
        var email_btn = findViewById(R.id.email_btn) as Button
        email_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, Login_with_email::class.java)
            startActivity(intent);
        }

        //facebook provider choice button

        //google provider choice button

    }
}