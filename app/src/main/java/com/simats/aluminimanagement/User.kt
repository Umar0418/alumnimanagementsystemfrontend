package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

// CORRECTED: Added student-specific fields to the general User model
data class User(
    @SerializedName("id")
    val id: String?,

    @SerializedName("roll_no")
    val rollNo: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("usertype")
    val userType: String?,

    // Added nullable fields for student-specific data
    @SerializedName("department")
    val department: String?,

    @SerializedName("year")
    val year: String?,

    // Additional profile fields
    @SerializedName("address")
    val address: String?,

    @SerializedName("cgpa")
    val cgpa: String?,

    @SerializedName("interests")
    val interests: String?
)
