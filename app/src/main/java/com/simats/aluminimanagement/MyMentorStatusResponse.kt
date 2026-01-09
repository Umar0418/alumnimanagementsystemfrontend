package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

// This is the main wrapper for the API response
data class MyMentorStatusResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("data")
    val data: MentorStatusData?
)

// CORRECTED: Added all the missing fields to match the activity's requirements.
data class MentorStatusData(
    @SerializedName("status")
    val status: String?,

    @SerializedName("mentorship_field")
    val field: String?,

    @SerializedName("working_hours")
    val workingHours: String?,

    @SerializedName("mentorship_style")
    val mentorshipStyle: String?
)
