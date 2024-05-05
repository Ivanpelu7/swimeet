package com.example.swimeet.data.model

import com.google.firebase.Timestamp

data class Advertisement(
    var advertisementId: String = "",
    val authorId: String = "",
    val authorUsername: String = "",
    val message: String = "",
    val title: String = "",
    val date: Timestamp = Timestamp.now()
)
