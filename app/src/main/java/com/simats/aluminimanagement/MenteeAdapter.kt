package com.simats.aluminimanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

interface MenteeActionListener {
    fun onAccept(mentee: MenteeModel)
    fun onDecline(mentee: MenteeModel)
    fun onMessage(mentee: MenteeModel)
}

class MenteeAdapter(
    private var mentees: List<MenteeModel>,
    private val listener: MenteeActionListener
) : RecyclerView.Adapter<MenteeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAvatar: TextView = view.findViewById(R.id.tvAvatar)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvDepartment: TextView = view.findViewById(R.id.tvDepartment)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvTopic: TextView = view.findViewById(R.id.tvTopic)
        val llActiveActions: LinearLayout = view.findViewById(R.id.llActiveActions)
        val llPendingActions: LinearLayout = view.findViewById(R.id.llPendingActions)
        val btnMessage: TextView = view.findViewById(R.id.btnMessage)
        val btnDecline: TextView = view.findViewById(R.id.btnDecline)
        val btnAccept: Button = view.findViewById(R.id.btnAccept)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mentee, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mentee = mentees[position]
        val context = holder.itemView.context

        // Avatar - first letter
        holder.tvAvatar.text = mentee.name.firstOrNull()?.uppercase() ?: "?"
        
        // Basic info
        holder.tvName.text = mentee.name
        holder.tvDepartment.text = "${mentee.department} - ${mentee.year}"
        holder.tvTopic.text = mentee.topic

        // Status badge styling
        when (mentee.status.lowercase()) {
            "active" -> {
                holder.tvStatus.text = "Active"
                holder.tvStatus.setBackgroundResource(R.drawable.badge_active_bg)
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.badge_active_text))
                holder.llActiveActions.visibility = View.VISIBLE
                holder.llPendingActions.visibility = View.GONE
            }
            "pending" -> {
                holder.tvStatus.text = "Pending"
                holder.tvStatus.setBackgroundResource(R.drawable.badge_pending_bg)
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.badge_pending_text))
                holder.llActiveActions.visibility = View.GONE
                holder.llPendingActions.visibility = View.VISIBLE
            }
            "completed" -> {
                holder.tvStatus.text = "Completed"
                holder.tvStatus.setBackgroundResource(R.drawable.badge_active_bg)
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
                holder.llActiveActions.visibility = View.GONE
                holder.llPendingActions.visibility = View.GONE
            }
            else -> {
                holder.tvStatus.text = mentee.status.replaceFirstChar { it.uppercase() }
                holder.llActiveActions.visibility = View.GONE
                holder.llPendingActions.visibility = View.GONE
            }
        }

        // Click listeners
        holder.btnMessage.setOnClickListener {
            listener.onMessage(mentee)
        }

        holder.btnAccept.setOnClickListener {
            listener.onAccept(mentee)
        }

        holder.btnDecline.setOnClickListener {
            listener.onDecline(mentee)
        }
    }

    override fun getItemCount() = mentees.size

    fun updateList(newList: List<MenteeModel>) {
        mentees = newList
        notifyDataSetChanged()
    }
}
