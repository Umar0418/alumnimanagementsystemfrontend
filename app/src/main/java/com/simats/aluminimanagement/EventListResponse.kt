package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

data class EventListResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("events")
    val events: List<EventModel>?
)
