package com.example.swimeet.data.model

import com.google.firebase.Timestamp

data class Comment(
    var userId: String = "",
    var message: String = "",
    var timestamp: Timestamp = Timestamp.now(),
    var username: String = "",
    var userPhoto: String = ""
)
