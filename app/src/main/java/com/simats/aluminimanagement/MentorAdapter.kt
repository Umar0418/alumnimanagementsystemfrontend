package com.simats.aluminimanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

interface MentorClickListener {
    fun onUpdateStatus(rollNo: String, status: String)
}

class MentorAdapter(
    private var mentorshipRequests: List<MentorModel>,
    private val listener: MentorClickListener
) : RecyclerView.Adapter<MentorAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvRollNo: TextView = view.findViewById(R.id.tvRollNo)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvAvatar: TextView = view.findViewById(R.id.tvAvatar)
        val tvField: TextView = view.findViewById(R.id.tvField)
        val tvHours: TextView = view.findViewById(R.id.tvHours)
        val tvStyle: TextView = view.findViewById(R.id.tvStyle)
        val llActionButtons: LinearLayout = view.findViewById(R.id.llActionButtons)
        val llEditButtons: LinearLayout = view.findViewById(R.id.llEditButtons)
        val btnApprove: Button = view.findViewById(R.id.btnApprove)
        val btnReject: Button = view.findViewById(R.id.btnReject)
        val btnSetPending: Button = view.findViewById(R.id.btnSetPending)
        val btnChangeStatus: Button = view.findViewById(R.id.btnChangeStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mentor_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = mentorshipRequests[position]
        val context = holder.itemView.context

        // Set avatar initial - handle empty name
        val displayName = request.name.ifEmpty { request.roll_no }
        holder.tvAvatar.text = displayName.firstOrNull()?.uppercase()?.toString() ?: "?"
        
        // Set mentor details - handle nulls
        holder.tvName.text = displayName
        holder.tvRollNo.text = "Roll No: ${request.roll_no}"
        holder.tvField.text = request.mentorship_field ?: "-"
        holder.tvHours.text = request.working_hours ?: "-"
        holder.tvStyle.text = request.mentorship_style ?: "-"

        // Set status badge and buttons based on status
        when (request.status.lowercase()) {
            "pending" -> {
                holder.tvStatus.text = "Pending"
                holder.tvStatus.setBackgroundResource(R.drawable.badge_pending_bg)
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.badge_pending_text))
                holder.llActionButtons.visibility = View.VISIBLE
                holder.llEditButtons.visibility = View.GONE
            }
            "approved" -> {
                holder.tvStatus.text = "Approved"
                holder.tvStatus.setBackgroundResource(R.drawable.badge_active_bg)
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.badge_active_text))
                holder.llActionButtons.visibility = View.GONE
                holder.llEditButtons.visibility = View.VISIBLE
                holder.btnChangeStatus.text = "Reject"
                holder.btnChangeStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.error)
            }
            "rejected" -> {
                holder.tvStatus.text = "Rejected"
                holder.tvStatus.setBackgroundResource(R.drawable.badge_rejected_bg)
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.badge_rejected_text))
                holder.llActionButtons.visibility = View.GONE
                holder.llEditButtons.visibility = View.VISIBLE
                holder.btnChangeStatus.text = "Approve"
                holder.btnChangeStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.success)
            }
            else -> {
                holder.tvStatus.text = request.status.uppercase()
                holder.llActionButtons.visibility = View.GONE
                holder.llEditButtons.visibility = View.VISIBLE
            }
        }

        // Pending action buttons
        holder.btnApprove.setOnClickListener {
            listener.onUpdateStatus(request.roll_no, "approved")
        }

        holder.btnReject.setOnClickListener {
            listener.onUpdateStatus(request.roll_no, "rejected")
        }

        // Edit buttons for approved/rejected
        holder.btnSetPending.setOnClickListener {
            listener.onUpdateStatus(request.roll_no, "pending")
        }

        holder.btnChangeStatus.setOnClickListener {
            // Toggle between approved and rejected
            val newStatus = if (request.status.lowercase() == "approved") "rejected" else "approved"
            listener.onUpdateStatus(request.roll_no, newStatus)
        }
    }

    override fun getItemCount() = mentorshipRequests.size

    fun updateList(newList: List<MentorModel>) {
        mentorshipRequests = newList
        notifyDataSetChanged()
    }
}