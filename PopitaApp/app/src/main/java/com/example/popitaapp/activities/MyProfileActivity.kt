package com.example.popitaapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.popitaapp.R
import kotlinx.android.synthetic.main.activity_my_profile.*

class MyProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        //back button
        back_btn.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, RoomActivity::class.java)
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

        //edit profile button
        editButton.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, SettingsChangeProfileActivity::class.java)

            //needed info for EditProfileActivity
            val job = getIntent().getStringExtra("job")
            val first_name = getIntent().getStringExtra("first_name")
            val description = getIntent().getStringExtra("description")
            val preferred_drink = getIntent().getStringExtra("preferred_drink")
            val background_color = getIntent().getStringExtra("background_color")
            val gender = getIntent().getStringExtra("gender")

            intent.putExtra("job", job)
            intent.putExtra("first_name", first_name)
            intent.putExtra("description", description)
            intent.putExtra("preferred_drink", preferred_drink)
            intent.putExtra("background_color", background_color)
            intent.putExtra("gender", gender)

            startActivity(intent);
        }
    }
}

