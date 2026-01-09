package com.simats.aluminimanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class RecentActivityAdapter(private val activities: List<RecentActivity>) :
    RecyclerView.Adapter<RecentActivityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView? = view.findViewById(R.id.ivActivityIcon)
        val title: TextView = view.findViewById(R.id.tvActivityTitle)
        val timestamp: TextView = view.findViewById(R.id.tvActivityTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = activities[position]
        holder.title.text = activity.description
        holder.timestamp.text = formatTimestamp(activity.time)
        
        // Set icon based on activity type
        holder.icon?.setImageResource(
            when (activity.type?.lowercase()) {
                "event" -> R.drawable.ic_event
                "job" -> R.drawable.ic_jobs
                "announcement" -> R.drawable.ic_announcements
                "user" -> R.drawable.ic_person
                else -> R.drawable.ic_notification
            }
        )
    }

    private fun formatTimestamp(timestamp: String): String {
        return try {
            val formats = listOf(
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            )
            
            var date: Date? = null
            for (format in formats) {
                try {
                    date = format.parse(timestamp)
                    if (date != null) break
                } catch (e: Exception) { }
            }
            
            if (date != null) {
                val now = System.currentTimeMillis()
                val diff = now - date.time
                
                val minutes = diff / (1000 * 60)
                val hours = diff / (1000 * 60 * 60)
                val days = diff / (1000 * 60 * 60 * 24)
                
                when {
                    minutes < 1 -> "Just now"
                    minutes < 60 -> "$minutes min ago"
                    hours < 24 -> "$hours hours ago"
                    days < 7 -> "$days days ago"
                    else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
                }
            } else {
                timestamp
            }
        } catch (e: Exception) {
            timestamp
        }
    }

    override fun getItemCount() = activities.size
}