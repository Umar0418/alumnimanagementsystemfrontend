package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

// This class perfectly wraps the JSON response from your view_alumni_directory.php script
data class AlumniDirectoryResponse(
    @SerializedName("status")
    val status: Boolean = false,

    @SerializedName("alumni")
    val alumni: List<AlumniDirectoryItem> = emptyList()
)
