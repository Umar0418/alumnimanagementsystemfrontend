package com.simats.aluminimanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyEventsAdapter(
    private val myEvents: List<MyEvent>
) : RecyclerView.Adapter<MyEventsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventName: TextView = view.findViewById(R.id.tvEventName)
        val eventDate: TextView = view.findViewById(R.id.tvEventDate)
        val eventTime: TextView = view.findViewById(R.id.tvEventTime)
        val eventVenue: TextView = view.findViewById(R.id.tvEventVenue)
        val eventDescription: TextView = view.findViewById(R.id.tvEventDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = myEvents[position]
        
        holder.eventName.text = event.eventName
        holder.eventDate.text = event.eventDate ?: "Date TBD"
        holder.eventTime.text = event.eventTime ?: "Time TBD"
        holder.eventVenue.text = event.venue ?: "Venue TBD"
        
        // Show description if available
        if (!event.description.isNullOrBlank()) {
            holder.eventDescription.visibility = View.VISIBLE
            holder.eventDescription.text = event.description
        } else {
            holder.eventDescription.visibility = View.GONE
        }
    }

    override fun getItemCount() = myEvents.size
}