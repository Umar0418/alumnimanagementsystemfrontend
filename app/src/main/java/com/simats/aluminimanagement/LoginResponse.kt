package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

// This handles both login responses and simple status/message responses
data class LoginResponse(
    @SerializedName("status")
    val status: Boolean = false,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("user")
    val user: User? = null,

    @SerializedName("error")
    val error: String? = null
)
