package com.group4.taobaoclon

data class RegisterResponse(
    val message: String,
    val user: UserData
)

data class UserData(
    val id: String,
    val email: String,
    val role: String // <--- Catches "ADMIN" or "USER"
)