package com.example.swimeet.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Event(
    var eventId: String = "",
    var type: String = "",
    var name: String = "",
    var date: Timestamp? = null,
    var location: GeoPoint? = null,
    var participants: List<String> = emptyList()
)
