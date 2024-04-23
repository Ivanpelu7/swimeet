package com.example.swimeet.data.model

import com.google.firebase.Timestamp

data class Event(
    var eventId: String = "",
    var type: String = "",
    var name: String = "",
    var date: Timestamp? = null,
    var site: String = "",
    var participants: List<String> = emptyList()
)
