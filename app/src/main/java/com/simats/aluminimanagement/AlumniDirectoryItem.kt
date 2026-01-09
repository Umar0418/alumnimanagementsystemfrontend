package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

// This model matches the data from view_alumni_directory.php
data class AlumniDirectoryItem(
    @SerializedName("roll_no")
    val rollNo: String = "",

    @SerializedName("name")
    val name: String = "",

    @SerializedName("department")
    val department: String = "",

    @SerializedName("batch_year")
    val batchYear: String = "",

    @SerializedName("company")
    val company: String = "",

    @SerializedName("location")
    val location: String = "",

    @SerializedName("mentorship")
    val mentorship: Int = 0
)
