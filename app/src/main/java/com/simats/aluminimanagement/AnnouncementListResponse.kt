package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

// This class perfectly wraps the JSON response from your viewannouncement.php script
data class AnnouncementListResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("announcements")
    val announcements: List<AnnouncementModel>?
)
