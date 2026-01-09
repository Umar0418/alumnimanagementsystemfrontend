package com.simats.aluminimanagement

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class AlumniEventAdapter(
    private val events: List<EventModel>,
    private val registeredEventIds: List<Int>
) : RecyclerView.Adapter<AlumniEventAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val banner: ImageView = view.findViewById(R.id.ivEventBanner)
        val dateBadge: TextView = view.findViewById(R.id.tvEventDateBadge)
        val title: TextView = view.findViewById(R.id.tvEventTitle)
        val time: TextView = view.findViewById(R.id.tvEventTime)
        val venue: TextView = view.findViewById(R.id.tvEventVenue)
        val registerButton: Button = view.findViewById(R.id.btnRegisterEvent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumni_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.title.text = event.title
        holder.time.text = event.event_time
        holder.venue.text = event.venue
        holder.dateBadge.text = event.event_date

        // Check if the user is registered for this event
        val isRegistered = registeredEventIds.contains(event.id)
        
        if (isRegistered) {
            holder.registerButton.text = "Registered"
            holder.registerButton.isEnabled = false
            holder.registerButton.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.grey_400)
        } else {
            holder.registerButton.text = "Register Now"
            holder.registerButton.isEnabled = true
            holder.registerButton.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.colorPrimary)
        }

        // Convert event to JSON for passing to next activity
        val eventJson = Gson().toJson(event)

        // Click on card to view event details
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EventDetailsActivity::class.java)
            intent.putExtra("event_data", eventJson)
            intent.putExtra("is_registered", isRegistered)
            context.startActivity(intent)
        }

        // Register button also navigates to event details
        holder.registerButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EventDetailsActivity::class.java)
            intent.putExtra("event_data", eventJson)
            intent.putExtra("is_registered", isRegistered)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = events.size
}