package com.simats.aluminimanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface AnnouncementClickListener {
    fun onEditAnnouncementClicked(announcement: AnnouncementModel)
    fun onDeleteAnnouncementClicked(announcement: AnnouncementModel)
}

class AnnouncementAdapter(
    private val announcements: List<AnnouncementModel>,
    private val listener: AnnouncementClickListener?
) : RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvAnnouncementTitle)
        val message: TextView = view.findViewById(R.id.tvAnnouncementMessage)
        val target: TextView = view.findViewById(R.id.tvAnnouncementTarget)
        val date: TextView? = view.findViewById(R.id.tvAnnouncementDate)
        val editButton: ImageView = view.findViewById(R.id.ivEditAnnouncement)
        val deleteButton: ImageView = view.findViewById(R.id.ivDeleteAnnouncement)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_announcement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val announcement = announcements[position]
        holder.title.text = announcement.title
        holder.message.text = announcement.message
        
        // Format target text for the badge
        val targetText = when (announcement.target.lowercase()) {
            "all" -> "For: Everyone"
            "alumni" -> "For: Alumni"
            "students" -> "For: Students"
            else -> "For: ${announcement.target.replaceFirstChar { it.uppercase() }}"
        }
        holder.target.text = targetText
        
        // Set date if available
        holder.date?.text = "Posted: ${announcement.created_at ?: "Recently"}"

        // Conditionally show/hide buttons based on whether a listener is provided
        if (listener != null) {
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE

            holder.editButton.setOnClickListener {
                listener.onEditAnnouncementClicked(announcement)
            }

            holder.deleteButton.setOnClickListener {
                listener.onDeleteAnnouncementClicked(announcement)
            }
        } else {
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        }
    }

    override fun getItemCount() = announcements.size
}