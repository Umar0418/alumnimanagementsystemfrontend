package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

data class MyMentorRequest(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("mentor_roll_no")
    val mentorRollNo: String = "",

    @SerializedName("mentor_name")
    val mentorName: String = "",

    @SerializedName("mentor_field")
    val mentorField: String? = null,

    @SerializedName("topic")
    val topic: String? = null,

    @SerializedName("status")
    val status: String = "pending",

    @SerializedName("requested_at")
    val requestedAt: String? = null
)

data class MyMentorRequestsResponse(
    @SerializedName("status")
    val status: Boolean = false,
    
    @SerializedName("requests")
    val requests: List<MyMentorRequest>? = null
)
