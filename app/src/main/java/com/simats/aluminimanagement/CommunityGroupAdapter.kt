package com.simats.aluminimanagement

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface CommunityGroupListener {
    fun onJoinCommunity(community: CommunityModel)
    fun onOpenCommunity(community: CommunityModel)
}

class CommunityGroupAdapter(
    private var communities: MutableList<CommunityModel>,
    private val listener: CommunityGroupListener
) : RecyclerView.Adapter<CommunityGroupAdapter.ViewHolder>() {

    private val avatarColors = listOf(
        R.drawable.circle_attendee_1,
        R.drawable.circle_attendee_2,
        R.drawable.circle_attendee_3,
        R.drawable.circle_attendee_4,
        R.drawable.circle_attendee_5
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCommunityInitial: TextView = view.findViewById(R.id.tvCommunityInitial)
        val tvCommunityName: TextView = view.findViewById(R.id.tvCommunityName)
        val tvMemberCount: TextView = view.findViewById(R.id.tvMemberCount)
        val tvCommunityDescription: TextView = view.findViewById(R.id.tvCommunityDescription)
        val btnJoin: Button = view.findViewById(R.id.btnJoin)
        val btnOpen: Button = view.findViewById(R.id.btnOpen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_community_group, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val community = communities[position]

        // Set avatar with color based on name
        val colorIndex = community.name.hashCode().let { Math.abs(it) % avatarColors.size }
        holder.tvCommunityInitial.setBackgroundResource(avatarColors[colorIndex])
        holder.tvCommunityInitial.text = community.name.firstOrNull()?.uppercase() ?: "C"

        holder.tvCommunityName.text = community.name
        holder.tvMemberCount.text = "${community.memberCount} members"
        holder.tvCommunityDescription.text = community.description

        // Show join or open button based on membership
        if (community.isMember) {
            holder.btnJoin.visibility = View.GONE
            holder.btnOpen.visibility = View.VISIBLE
        } else {
            holder.btnJoin.visibility = View.VISIBLE
            holder.btnOpen.visibility = View.GONE
        }

        holder.btnJoin.setOnClickListener {
            listener.onJoinCommunity(community)
        }

        holder.btnOpen.setOnClickListener {
            listener.onOpenCommunity(community)
        }

        // Click anywhere on card to open if member
        holder.itemView.setOnClickListener {
            if (community.isMember) {
                listener.onOpenCommunity(community)
            } else {
                listener.onJoinCommunity(community)
            }
        }
    }

    override fun getItemCount() = communities.size

    fun updateList(newList: List<CommunityModel>) {
        communities.clear()
        communities.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateMembership(communityId: Int, isMember: Boolean) {
        val index = communities.indexOfFirst { it.id == communityId }
        if (index != -1) {
            communities[index].isMember = isMember
            notifyItemChanged(index)
        }
    }
}
