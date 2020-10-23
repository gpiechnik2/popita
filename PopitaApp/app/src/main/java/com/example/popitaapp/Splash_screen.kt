package com.example.popitaapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.os.Handler
import android.os.Looper



fun static() {
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
}

class Splash_screen : AppCompatActivity() {

    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            // Delay and Start Activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 6000) // here we're delaying to startActivity after 3seconds
    }
}