package com.simats.aluminimanagement

import com.google.gson.annotations.SerializedName

// Wraps the response for fetching a list of posts
data class CommunityPostsResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("posts")
    val posts: List<CommunityPost>?
)

// Defines the data structure for a single community post
data class CommunityPost(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("user_initials")
    val userInitials: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("likes_count")
    var likesCount: Int,
    @SerializedName("comments_count")
    val commentsCount: Int,
    @SerializedName("is_liked")
    var isLiked: Boolean
)

// Wraps the response for creating a new post
data class CreatePostResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String?
)
