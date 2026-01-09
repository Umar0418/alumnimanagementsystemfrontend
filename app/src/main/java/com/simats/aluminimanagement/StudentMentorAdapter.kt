package com.simats.aluminimanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface StudentMentorClickListener {
    fun onRequestMentorship(mentor: MentorModel)
}

class StudentMentorAdapter(
    private var mentors: List<MentorModel>,
    private val listener: StudentMentorClickListener
) : RecyclerView.Adapter<StudentMentorAdapter.MentorViewHolder>() {

    private var filteredMentors: List<MentorModel> = mentors

    inner class MentorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvInitials: TextView = itemView.findViewById(R.id.tvMentorInitials)
        val tvName: TextView = itemView.findViewById(R.id.tvMentorName)
        val tvRole: TextView = itemView.findViewById(R.id.tvMentorRole)
        val tvSlotsBadge: TextView = itemView.findViewById(R.id.tvSlotsBadge)
        val tvSkill1: TextView = itemView.findViewById(R.id.tvSkill1)
        val tvSkill2: TextView = itemView.findViewById(R.id.tvSkill2)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val btnRequest: Button = itemView.findViewById(R.id.btnRequestMentorship)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mentor, parent, false)
        return MentorViewHolder(view)
    }

    override fun onBindViewHolder(holder: MentorViewHolder, position: Int) {
        val mentor = filteredMentors[position]

        // Set initials
        val names = mentor.name.split(" ")
        val initials = if (names.size >= 2) {
            "${names[0].firstOrNull() ?: ""}${names[1].firstOrNull() ?: ""}"
        } else {
            mentor.name.take(2)
        }
        holder.tvInitials.text = initials.uppercase()

        // Set name
        holder.tvName.text = mentor.name

        // Set role/field
        holder.tvRole.text = "${mentor.mentorship_field ?: "Not specified"} • ${mentor.working_hours ?: "N/A"}"

        // Set slots badge
        val slotsLeft = (1..5).random()
        if (slotsLeft > 0) {
            holder.tvSlotsBadge.text = "$slotsLeft slots\nleft"
            holder.tvSlotsBadge.visibility = View.VISIBLE
            holder.tvSlotsBadge.setBackgroundResource(R.drawable.badge_slots_bg)
            holder.tvSlotsBadge.setTextColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
        } else {
            holder.tvSlotsBadge.text = "Full"
            holder.tvSlotsBadge.setBackgroundResource(R.drawable.badge_rejected_bg)
            holder.tvSlotsBadge.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
        }

        // Set skills from mentorship_field
        val skills = (mentor.mentorship_field ?: "").split(",").map { it.trim() }
        holder.tvSkill1.text = skills.getOrElse(0) { mentor.mentorship_style ?: "" }
        holder.tvSkill2.text = skills.getOrElse(1) { mentor.working_hours ?: "" }
        holder.tvSkill1.visibility = View.VISIBLE
        holder.tvSkill2.visibility = if (skills.size > 1) View.VISIBLE else View.GONE

        // Set rating
        holder.tvRating.text = "4.${(5..9).random()} (${(5..20).random()} reviews)"

        // Request button
        holder.btnRequest.setOnClickListener {
            listener.onRequestMentorship(mentor)
        }
    }

    override fun getItemCount(): Int = filteredMentors.size

    fun updateList(newMentors: List<MentorModel>) {
        mentors = newMentors
        filteredMentors = newMentors
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredMentors = if (query.isEmpty()) {
            mentors
        } else {
            mentors.filter {
                // CORRECTED: Added null-safe calls to all nullable fields
                it.name.contains(query, ignoreCase = true) ||
                it.mentorship_field?.contains(query, ignoreCase = true) == true ||
                it.mentorship_style?.contains(query, ignoreCase = true) == true
            }
        }
        notifyDataSetChanged()
    }

    fun filterByCategory(category: String) {
        filteredMentors = if (category == "All") {
            mentors
        } else {
            mentors.filter {
                // CORRECTED: Added a null-safe call
                it.mentorship_field?.contains(category, ignoreCase = true) == true
            }
        }
        notifyDataSetChanged()
    }
}
