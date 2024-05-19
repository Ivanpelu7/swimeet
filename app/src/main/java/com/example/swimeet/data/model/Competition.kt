package com.example.swimeet.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Competition(
    var eventId: String = "",
    var type: String = "",
    var name: String = "",
    var date: Timestamp? = null,
    var distance: Int? = null,
    var location: GeoPoint? = null,
    var participants: List<String> = emptyList(),
    var finished: Boolean = false,
    var link: String = ""
)
