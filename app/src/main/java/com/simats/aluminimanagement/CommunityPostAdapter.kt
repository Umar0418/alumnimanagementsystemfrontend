package com.simats.aluminimanagement

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

interface CommunityPostActionListener {
    fun onLikePost(post: CommunityPost, position: Int)
    fun onCommentPost(post: CommunityPost)
    fun onSharePost(post: CommunityPost)
}

class CommunityPostAdapter(
    private var posts: MutableList<CommunityPost>,
    private val listener: CommunityPostActionListener
) : RecyclerView.Adapter<CommunityPostAdapter.ViewHolder>() {

    private val avatarColors = listOf(
        R.drawable.circle_attendee_1,
        R.drawable.circle_attendee_2,
        R.drawable.circle_attendee_3,
        R.drawable.circle_attendee_4,
        R.drawable.circle_attendee_5
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUserInitial: TextView = view.findViewById(R.id.tvUserInitial)
        val tvUserName: TextView = view.findViewById(R.id.tvUserName)
        val tvPostTime: TextView = view.findViewById(R.id.tvPostTime)
        val tvPostContent: TextView = view.findViewById(R.id.tvPostContent)
        val ivLike: ImageView = view.findViewById(R.id.ivLike)
        val tvLikeCount: TextView = view.findViewById(R.id.tvLikeCount)
        val tvCommentCount: TextView = view.findViewById(R.id.tvCommentCount)
        val layoutLike: LinearLayout = view.findViewById(R.id.layoutLike)
        val layoutComment: LinearLayout = view.findViewById(R.id.layoutComment)
        val layoutShare: LinearLayout = view.findViewById(R.id.layoutShare)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_community_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]

        // Set user avatar with random color based on name
        val colorIndex = post.userName.hashCode().let { Math.abs(it) % avatarColors.size }
        holder.tvUserInitial.setBackgroundResource(avatarColors[colorIndex])
        holder.tvUserInitial.text = post.userName.firstOrNull()?.uppercase() ?: "U"

        // User info
        holder.tvUserName.text = post.userName
        holder.tvPostTime.text = getTimeAgo(post.createdAt)

        // Post content
        holder.tvPostContent.text = post.content

        // Like count and state
        holder.tvLikeCount.text = post.likesCount.toString()
        if (post.isLiked) {
            holder.ivLike.setColorFilter(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_light))
        } else {
            holder.ivLike.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.grey_600))
        }

        // Comment count
        holder.tvCommentCount.text = post.commentsCount.toString()

        // Click listeners
        holder.layoutLike.setOnClickListener {
            listener.onLikePost(post, position)
        }

        holder.layoutComment.setOnClickListener {
            listener.onCommentPost(post)
        }

        holder.layoutShare.setOnClickListener {
            listener.onSharePost(post)
        }
    }

    override fun getItemCount() = posts.size

    fun updateList(newPosts: List<CommunityPost>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    fun updateLike(position: Int, isLiked: Boolean, newCount: Int) {
        if (position in posts.indices) {
            posts[position].isLiked = isLiked
            notifyItemChanged(position)
        }
    }

    fun addPost(post: CommunityPost) {
        posts.add(0, post)
        notifyItemInserted(0)
    }

    private fun getTimeAgo(dateString: String): String {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = format.parse(dateString) ?: return dateString
            val now = Date()
            val diff = now.time - date.time

            when {
                diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
                diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)} min ago"
                diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)} hours ago"
                diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)} day ago"
                else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
            }
        } catch (e: Exception) {
            dateString
        }
    }
}
