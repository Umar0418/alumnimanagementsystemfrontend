package com.simats.aluminimanagement

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

interface EventClickListener {
    fun onEditEvent(event: EventModel)
    fun onDeleteEvent(event: EventModel)
}

class EventAdapter(
    private val events: List<EventModel>,
    private val listener: EventClickListener? = null
) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvEventTitle)
        val description: TextView = view.findViewById(R.id.tvEventDescription)
        val date: TextView = view.findViewById(R.id.tvEventDate)
        val time: TextView = view.findViewById(R.id.tvEventTime)
        val venue: TextView = view.findViewById(R.id.tvEventVenue)
        val editBtn: ImageView = view.findViewById(R.id.ivEditEvent)
        val deleteBtn: ImageView = view.findViewById(R.id.ivDeleteEvent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.title.text = event.title
        holder.description.text = event.description ?: "No description available"
        holder.date.text = event.event_date
        holder.time.text = event.event_time
        holder.venue.text = event.venue

        // Click on card to view event details
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EventDetailsActivity::class.java)
            intent.putExtra("event_data", Gson().toJson(event))
            context.startActivity(intent)
        }

        // Show/hide edit/delete based on listener availability
        if (listener != null) {
            holder.editBtn.visibility = View.VISIBLE
            holder.deleteBtn.visibility = View.VISIBLE
            
            holder.editBtn.setOnClickListener {
                listener.onEditEvent(event)
            }
            holder.deleteBtn.setOnClickListener {
                listener.onDeleteEvent(event)
            }
        } else {
            holder.editBtn.visibility = View.GONE
            holder.deleteBtn.visibility = View.GONE
        }
    }

    override fun getItemCount() = events.size
}