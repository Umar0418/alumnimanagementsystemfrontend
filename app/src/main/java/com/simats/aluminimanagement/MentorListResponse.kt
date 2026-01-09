package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

data class MentorListResponse(
    val status: Boolean,
    val message: String? = null,
    // Support both field names from backend
    @SerializedName("requests")
    val requests: List<MentorModel>? = null,
    @SerializedName("mentors")
    val mentors: List<MentorModel>? = null
) {
    // Helper function to get mentors from either field
    fun getMentorList(): List<MentorModel> {
        return mentors ?: requests ?: emptyList()
    }
}
