package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

data class CommunityListResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("communities")
    val communities: List<CommunityModel>?,

    @SerializedName("message")
    val message: String?
)

data class CommunityModel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("member_count")
    val memberCount: Int,

    // CORRECTED: Changed from val to var to allow modification
    @SerializedName("is_member")
    var isMember: Boolean
)

data class JoinCommunityResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String
)
