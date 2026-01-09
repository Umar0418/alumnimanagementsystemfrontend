package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

// Wraps the response for fetching the list of students
data class StudentListResponse(
    @SerializedName("status")
    val status: Boolean = false,

    @SerializedName("count")
    val count: Int = 0,

    @SerializedName("students")
    val students: List<Student> = emptyList()
)

// Defines the data structure for a single student
data class Student(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("name")
    val name: String = "",

    @SerializedName("roll_no")
    val rollNo: String = "",

    @SerializedName("email")
    val email: String = "",

    @SerializedName("phone")
    val phone: String = "",

    @SerializedName("department")
    val department: String = "",

    @SerializedName("year")
    val year: String = "",

    @SerializedName("address")
    val address: String = "",

    @SerializedName("cgpa")
    val cgpa: String = "",

    @SerializedName("interests")
    val interests: String = ""
)
