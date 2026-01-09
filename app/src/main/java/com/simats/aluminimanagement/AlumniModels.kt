package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

// Defines the data structure for the API response
data class AlumniListResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("alumni")
    val alumni: List<Alumni>?
)

// Defines the data structure for a single alumnus
data class Alumni(
    @SerializedName("id")
    val id: String = "",  // Changed to String since roll_no is a String

    @SerializedName("name")
    val name: String = "",

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("company")
    val company: String? = null,

    @SerializedName("designation")
    val designation: String? = null,

    @SerializedName("batch_year")
    val batchYear: String? = null,

    @SerializedName("department")
    val department: String? = null,

    @SerializedName("linkedin")
    val linkedin: String? = null,

    @SerializedName("skills")
    val skills: String? = null
)
