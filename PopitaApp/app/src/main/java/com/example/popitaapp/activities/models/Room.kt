package com.example.popitaapp.activities.models

data class Room(
        val id: Int,
        val last_message: String,
        val last_sender: Int,
        val receiver_id: Int,
        val receiver_name: String,
        val last_message_timestamp: String
)