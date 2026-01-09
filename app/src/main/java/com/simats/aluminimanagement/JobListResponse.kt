package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

// This class perfectly wraps the JSON response from your viewjobs.php script
data class JobListResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("jobs")
    val jobs: List<JobModel>?
)
