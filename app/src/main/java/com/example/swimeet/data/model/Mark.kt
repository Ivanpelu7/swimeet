package com.example.swimeet.data.model

import com.google.firebase.Timestamp

data class Mark(
    var idMark: String = "",
    var competition: String = "",
    var date: Timestamp? = Timestamp.now(),
    var mark: Long = 0,
    var swimEvent: String = ""
)
