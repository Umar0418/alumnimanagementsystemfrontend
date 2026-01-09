package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

// CORRECTED: This now perfectly matches the response from your get_alumni_profile.php script
data class AlumniProfileResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("profile")
    val profile: AlumniProfile?,

    @SerializedName("message")
    val message: String?
)

data class AlumniProfile(
    @SerializedName("roll_no")
    val rollNo: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("department")
    val department: String?,

    @SerializedName("batch_year")
    val batchYear: String?,

    @SerializedName("company")
    val company: String?,

    @SerializedName("location")
    val location: String?,

    // The PHP script does not send a 'title', so it's removed from here.

    @SerializedName("mentorship")
    val mentorship: String? // Assuming 'yes' or 'no'
)
