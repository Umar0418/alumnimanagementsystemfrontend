package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

// This now perfectly matches the JSON from myevents.php script
data class MyEventsResponse(
    @SerializedName("status")
    val status: Boolean,

    // FIXED: Changed from "registered_events" to "my_events" to match PHP
    @SerializedName("my_events")
    val registeredEvents: List<MyEvent>?
)

data class MyEvent(
    @SerializedName("event_id")
    val eventId: Int = 0,

    @SerializedName("event_name")
    val eventName: String = "",

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("event_date")
    val eventDate: String? = null,

    @SerializedName("event_time")
    val eventTime: String? = null,

    @SerializedName("venue")
    val venue: String? = null,

    @SerializedName("registered_at")
    val registeredAt: String? = null
)
