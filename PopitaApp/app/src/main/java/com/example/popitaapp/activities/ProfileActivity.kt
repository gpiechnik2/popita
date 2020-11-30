package com.example.popitaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.popitaapp.R
import kotlinx.android.synthetic.main.activity_profile.*

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

        //set dynamically image based on user info
        val gender = getIntent().getStringExtra("gender")

        if (gender == "Male") {
            profileImage.setImageResource(R.drawable.ic_gender_male)
        } else {
            profileImage.setImageResource(R.drawable.ic_gender_female)
        }

        //change background color
        val background = getIntent().getStringExtra("background_color")

        if (background == "Orange") {
            root.setBackgroundColor(getResources().getColor(R.color.orange))
        } else if (background == "Blue") {
            root.setBackgroundColor(getResources().getColor(R.color.blue))
        } else if (background == "Green") {
            root.setBackgroundColor(getResources().getColor(R.color.green))
        } else if (background == "Yellow") {
            root.setBackgroundColor(getResources().getColor(R.color.yellow))
        } else if (background == "Pink") {
            root.setBackgroundColor(getResources().getColor(R.color.pink))
        }

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