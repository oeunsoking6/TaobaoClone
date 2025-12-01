package com.group4.taobaoclon

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val token: String,

    // Add @SerializedName to force Gson to find the right JSON field
    @SerializedName("userId")
    val userId: String,

    @SerializedName("role")
    val role: String
)