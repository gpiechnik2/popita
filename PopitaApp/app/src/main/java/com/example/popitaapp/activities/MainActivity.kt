package com.example.popitaapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.popitaapp.R
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //login button
        var login_btn = findViewById(R.id.login_btn) as Button
        login_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, Login_choices::class.java)
            startActivity(intent);
        }

        //register button
        var register_btn = findViewById(R.id.register_btn) as Button
        register_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, Register::class.java)
            startActivity(intent);
        }

        }
    }
