package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

// This model perfectly matches the columns in your 'jobs' database table
data class JobModel(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("company")
    val company: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("location")
    val location: String?,

    @SerializedName("job_type")
    val job_type: String,

    @SerializedName("salary")
    val salary: String,

    @SerializedName("last_date")
    val last_date: String,

    // The design implies these fields. I am assuming they exist in your table.
    @SerializedName("status")
    val status: String? = "pending",

    @SerializedName("posted_by")
    val posted_by: String? = "Admin"
)
