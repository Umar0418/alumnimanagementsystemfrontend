package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

data class MentorStatusResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("data")
    val data: MentorStatus?
)

data class MentorStatus(
    @SerializedName("status")
    val status: String,

    @SerializedName("mentorship_field")
    val field: String
)
