package com.example.swimeet.data.model

data class Record(
    var recordId: String = "",
    var userId: String = "",
    var username: String = "",
    var time: Long = 0,
    var swimEvent: String = "",
    var isRecord: Boolean = false,
    var category: String = "",
    var genre: String = ""
)