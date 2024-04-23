package com.example.swimeet.data.model

data class User(
    var userId: String? = "",
    var admin: Boolean = false,
    var username: String? = "",
    var email: String = "",
    var name: String = "",
    var category: String = "",
)
