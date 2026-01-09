package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

data class MentorModel(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("roll_no")
    val roll_no: String = "",

    @SerializedName("name")
    val name: String = "",

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("mentorship_field")
    val mentorship_field: String? = null,

    @SerializedName("working_hours")
    val working_hours: String? = null,

    @SerializedName("mentorship_style")
    val mentorship_style: String? = null,

    @SerializedName("status")
    val status: String = "pending"
)
