package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

// This data class PERFECTLY matches the response from your updatementorstatus.php script
data class UpdateStatusResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("roll_no")
    val roll_no: String?,

    @SerializedName("new_status")
    val new_status: String?
)
