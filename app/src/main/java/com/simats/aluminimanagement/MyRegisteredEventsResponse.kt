package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

data class MyRegisteredEventsResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("registered_events")
    val registeredEvents: List<Int>?
)
