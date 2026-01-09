package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

// This model perfectly matches the data from your viewfund.php script
data class FundModel(
    @SerializedName("id")
    val id: String,

    @SerializedName("fund_title")
    val title: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("target_amount")
    val targetAmount: Double,

    @SerializedName("collected_amount")
    val collectedAmount: Double,

    @SerializedName("last_date")
    val lastDate: String,

    // The design shows a donor count, but the PHP doesn't provide it.
    // I'll add a placeholder field for now.
    @SerializedName("donors")
    val donors: Int? = 0
)
