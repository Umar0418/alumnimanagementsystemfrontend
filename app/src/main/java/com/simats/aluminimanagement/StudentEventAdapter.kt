package com.simats.aluminimanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class StudentEventAdapter(
    private val events: List<EventModel>,
    private val onEventClick: (EventModel) -> Unit
) : RecyclerView.Adapter<StudentEventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardEvent: CardView = itemView.findViewById(R.id.cardEvent)
        val tvEventTitle: TextView = itemView.findViewById(R.id.tvEventTitle)
        val tvEventDate: TextView = itemView.findViewById(R.id.tvEventDate)
        val tvEventVenue: TextView = itemView.findViewById(R.id.tvEventVenue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        
        holder.tvEventTitle.text = event.title
        holder.tvEventDate.text = event.event_date
        holder.tvEventVenue.text = event.venue
        
        holder.cardEvent.setOnClickListener {
            onEventClick(event)
        }
    }

    override fun getItemCount(): Int = events.size
}
