package com.simats.aluminimanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class MyRequestsAdapter(
    private var requests: List<MyMentorRequest>
) : RecyclerView.Adapter<MyRequestsAdapter.RequestViewHolder>() {

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvInitials: TextView = itemView.findViewById(R.id.tvMentorInitials)
        val tvName: TextView = itemView.findViewById(R.id.tvMentorName)
        val tvRole: TextView = itemView.findViewById(R.id.tvMentorRole)
        val ivStatusIcon: ImageView = itemView.findViewById(R.id.ivStatusIcon)
        val tvRequestedTime: TextView = itemView.findViewById(R.id.tvRequestedTime)
        val tvStatusBadge: TextView = itemView.findViewById(R.id.tvStatusBadge)
        val tvApprovedMessage: TextView = itemView.findViewById(R.id.tvApprovedMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requests[position]
        val context = holder.itemView.context

        // Set initials
        val names = request.mentorName.split(" ")
        val initials = if (names.size >= 2) {
            "${names[0].firstOrNull() ?: ""}${names[1].firstOrNull() ?: ""}"
        } else {
            request.mentorName.take(2)
        }
        holder.tvInitials.text = initials.uppercase()

        // Set name and role
        holder.tvName.text = request.mentorName
        holder.tvRole.text = request.mentorField ?: "Mentor"

        // Set requested time
        holder.tvRequestedTime.text = "Requested   ${request.requestedAt ?: "recently"}"

        // Set status
        when (request.status.lowercase()) {
            "pending" -> {
                holder.tvStatusBadge.text = "Pending"
                holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_pending_bg)
                holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_pending_text))
                holder.ivStatusIcon.setImageResource(R.drawable.ic_time)
                holder.ivStatusIcon.setColorFilter(ContextCompat.getColor(context, R.color.badge_pending_text))
                holder.tvApprovedMessage.visibility = View.GONE
            }
            "approved" -> {
                holder.tvStatusBadge.text = "Approved"
                holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_active_bg)
                holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_active_text))
                holder.ivStatusIcon.setImageResource(R.drawable.ic_check_circle)
                holder.ivStatusIcon.setColorFilter(ContextCompat.getColor(context, R.color.badge_active_text))
                holder.tvApprovedMessage.visibility = View.VISIBLE
                holder.tvApprovedMessage.text = "You can now message this mentor."
            }
            "rejected" -> {
                holder.tvStatusBadge.text = "Rejected"
                holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_rejected_bg)
                holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_rejected_text))
                holder.ivStatusIcon.setImageResource(R.drawable.ic_delete)
                holder.ivStatusIcon.setColorFilter(ContextCompat.getColor(context, R.color.badge_rejected_text))
                holder.tvApprovedMessage.visibility = View.GONE
            }
            else -> {
                holder.tvStatusBadge.text = request.status.uppercase()
                holder.tvApprovedMessage.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = requests.size

    fun updateList(newRequests: List<MyMentorRequest>) {
        requests = newRequests
        notifyDataSetChanged()
    }
}
