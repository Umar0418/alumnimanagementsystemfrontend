package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

data class EventModel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("event_date")
    val event_date: String,

    @SerializedName("event_time")
    val event_time: String,

    @SerializedName("venue")
    val venue: String,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("poster")
    val poster: String? = null
)
