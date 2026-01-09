package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

data class MenteeModel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("student_id")
    val studentId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("department")
    val department: String,

    @SerializedName("year")
    val year: String,

    @SerializedName("topic")
    val topic: String,

    @SerializedName("status")
    val status: String,  // "pending", "active", "completed"

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("phone")
    val phone: String? = null
)

data class MenteeListResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("mentees")
    val mentees: List<MenteeModel>?
)

data class UpdateMenteeStatusResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String
)
