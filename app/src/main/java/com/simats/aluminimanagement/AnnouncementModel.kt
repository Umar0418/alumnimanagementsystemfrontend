package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// Defines the data structure for a single announcement
data class AnnouncementModel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("target")
    val target: String,

    // CORRECTED: Added the missing created_at field
    @SerializedName("created_at")
    val created_at: String?
) : Serializable // Implement Serializable to allow passing in Intents
