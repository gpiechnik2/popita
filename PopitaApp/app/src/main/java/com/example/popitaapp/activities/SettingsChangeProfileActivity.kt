package com.example.popitaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.popitaapp.R
import kotlinx.android.synthetic.main.activity_settings_change_password.*

class SettingsChangeProfileActivity : AppCompatActivity() {
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



        //intent.putExtra("user_id", user_id)
        //                    intent.putExtra("first_name", first_name)
        //                    intent.putExtra("gender", gender)
        //                    intent.putExtra("background_color", background_color)
        //                    intent.putExtra("job", job)
        //                    intent.putExtra("preferred_drink", preferred_drink)
        //                    intent.putExtra("description", description)

    }
}