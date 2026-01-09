package com.simats.aluminimanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DashboardEventAdapter(private val events: List<EventModel>) : RecyclerView.Adapter<DashboardEventAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.ivEventImage)
        val title: TextView = view.findViewById(R.id.tvEventTitle)
        val date: TextView = view.findViewById(R.id.tvEventDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dashboard_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.title.text = event.title
        holder.date.text = event.event_date
        // Here you would use a library like Glide or Picasso to load the image
        // holder.image.setImageResource(R.drawable.placeholder_banner)
    }

    override fun getItemCount() = events.size
}
