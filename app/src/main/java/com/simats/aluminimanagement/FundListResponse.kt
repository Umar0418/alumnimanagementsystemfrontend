package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

// This class perfectly wraps the JSON response from your viewfund.php script
data class FundListResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("funds")
    val funds: List<FundModel>?
)
