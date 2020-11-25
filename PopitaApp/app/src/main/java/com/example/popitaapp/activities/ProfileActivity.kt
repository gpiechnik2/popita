package com.example.popitaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.popitaapp.R
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.back_btn


class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        back_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, ExploreDetailActivity::class.java)

            //needed info for ExploreDetailActivity
            val first_name = getIntent().getStringExtra("first_name")
            val location = getIntent().getStringExtra("location")
            val distance = getIntent().getFloatExtra("distance", 0.toFloat()).toString()
            val user_id = getIntent().getIntExtra("user_id", 0)

            intent.putExtra("user_first_name", first_name)
            intent.putExtra("location", location)
            intent.putExtra("distance", distance)
            intent.putExtra("user_id", user_id)

            startActivity(intent);
        }

        //set profile info
        txtJob.text = getIntent().getStringExtra("job")

        var txt_name = findViewById(R.id.txtName) as TextView
        txt_name.text = getIntent().getStringExtra("first_name")

        txtDescription.text = getIntent().getStringExtra("description")
        txtAlcohol.text = getIntent().getStringExtra("preferred_drink")

        //Message button
        sendMessageBtn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, RoomDetailActivity::class.java)

            val user_id = getIntent().getIntExtra("user_id", 0)
            val receiver_name = getIntent().getStringExtra("first_name")

            intent.putExtra("user_id", user_id)
            intent.putExtra("receiver_name", receiver_name)

            startActivity(intent);
        }
    }
}