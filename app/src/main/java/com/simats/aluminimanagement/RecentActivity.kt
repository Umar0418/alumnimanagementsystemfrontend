package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

data class RecentActivity(
    @SerializedName("type")
    val type: String? = null,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("time")
    val time: String
) {
    // Helper property to get display title
    val title: String
        get() = description
    
    // Helper property to get display timestamp  
    val timestamp: String
        get() = time
}
