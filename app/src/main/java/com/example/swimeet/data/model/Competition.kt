package com.example.swimeet.data.model

import com.google.firebase.Timestamp

data class Competition(
    var eventId: String = "",
    var type: String = "",
    var name: String = "",
    var site: String = "",
    var date: Timestamp? = null,
    var distance: Double? = null,
    var participants: List<String> = emptyList()
)
