package com.example.swimeet.data.model

import com.google.firebase.Timestamp

data class Message(
    val message: String = "",
    val idSender: String = "",
    val timestamp: Timestamp? = Timestamp.now()
)
