package com.example.popitaapp.activities

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.popitaapp.R
import kotlinx.android.synthetic.main.activity_settings_change_password.*
import kotlinx.android.synthetic.main.activity_settings_change_password.back_btn
import kotlinx.android.synthetic.main.activity_settings_privacy.*

class SettingsPrivacyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_privacy)

        //back button
        back_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent);
        }

        //check if current user allowed fine location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            switchLocation.setChecked(true)
        }

        //if user want to, user can start settings activity
        switchLocation.setOnClickListener {
            this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

    }
}