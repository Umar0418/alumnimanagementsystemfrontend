package com.simats.aluminimanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface CommunityActionListener {
    fun onJoinGroup(community: CommunityModel)
    fun onOpenChat(community: CommunityModel)
}

class CommunityAdapter(
    private var communities: List<CommunityModel>,
    private val listener: CommunityActionListener
) : RecyclerView.Adapter<CommunityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvGroupName: TextView = view.findViewById(R.id.tvGroupName)
        val tvMemberCount: TextView = view.findViewById(R.id.tvMemberCount)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val llOpenChat: LinearLayout = view.findViewById(R.id.llOpenChat)
        val btnOpenChat: TextView = view.findViewById(R.id.btnOpenChat)
        val btnJoinGroup: Button = view.findViewById(R.id.btnJoinGroup)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_community, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val community = communities[position]

        holder.tvGroupName.text = community.name
        holder.tvMemberCount.text = community.memberCount.toString()
        holder.tvDescription.text = community.description

        if (community.isMember) {
            // User has joined - show Open Chat
            holder.llOpenChat.visibility = View.VISIBLE
            holder.btnJoinGroup.visibility = View.GONE
        } else {
            // User hasn't joined - show Join button
            holder.llOpenChat.visibility = View.GONE
            holder.btnJoinGroup.visibility = View.VISIBLE
        }

        holder.btnJoinGroup.setOnClickListener {
            listener.onJoinGroup(community)
        }

        holder.btnOpenChat.setOnClickListener {
            listener.onOpenChat(community)
        }

        holder.llOpenChat.setOnClickListener {
            listener.onOpenChat(community)
        }
    }

    override fun getItemCount() = communities.size

    fun updateList(newList: List<CommunityModel>) {
        communities = newList
        notifyDataSetChanged()
    }

    fun updateCommunity(communityId: Int, isMember: Boolean) {
        val index = communities.indexOfFirst { it.id == communityId }
        if (index != -1) {
            val updated = communities[index].copy(
                isMember = isMember,
                memberCount = if (isMember) communities[index].memberCount + 1 else communities[index].memberCount
            )
            communities = communities.toMutableList().apply { set(index, updated) }
            notifyItemChanged(index)
        }
    }
}
