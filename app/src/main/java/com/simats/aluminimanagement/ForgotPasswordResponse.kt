package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

data class ForgotPasswordResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String
)
