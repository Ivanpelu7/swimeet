package com.example.swimeet.data.model

data class Record(
    var userId: String = "",
    var username: String = "",
    var time: Long = 0,
    var swimEvent: String = "",
    var category: String = "",
    var genre: String = ""
)