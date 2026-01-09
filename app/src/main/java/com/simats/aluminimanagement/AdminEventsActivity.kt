package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminEventsActivity : AppCompatActivity(), EventClickListener {

    private lateinit var recyclerEvents: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_events)

        recyclerEvents = findViewById(R.id.recyclerEvents)
        recyclerEvents.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        findViewById<ExtendedFloatingActionButton>(R.id.fabAddEvent).setOnClickListener {
            startActivity(Intent(this, ManageEventsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadEvents()
    }

    private fun loadEvents() {
        ApiClient.instance.getEvents().enqueue(object : Callback<EventListResponse> {
            override fun onResponse(call: Call<EventListResponse>, response: Response<EventListResponse>) {
                val body = response.body()
                if (response.isSuccessful && body != null && body.status) {
                    if (!body.events.isNullOrEmpty()) {
                        recyclerEvents.adapter = EventAdapter(body.events, this@AdminEventsActivity)
                    } else {
                        recyclerEvents.adapter = EventAdapter(emptyList(), this@AdminEventsActivity)
                        Toast.makeText(this@AdminEventsActivity, "No events found.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMsg = if (!response.isSuccessful) "HTTP ${response.code()}" else "API Error"
                    Toast.makeText(this@AdminEventsActivity, "Failed to load events: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<EventListResponse>, t: Throwable) {
                Toast.makeText(this@AdminEventsActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onEditEvent(event: EventModel) {
        val intent = Intent(this, ManageEventsActivity::class.java)
        intent.putExtra("event_data", Gson().toJson(event))
        startActivity(intent)
    }

    override fun onDeleteEvent(event: EventModel) {
        AlertDialog.Builder(this)
            .setTitle("Delete Event")
            .setMessage("Are you sure you want to delete '${event.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                // CORRECTED: Passing the event ID as a String, without the incorrect .toInt() conversion.
                deleteEvent(event.id.toString())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // CORRECTED: The function now correctly accepts a String ID.
    private fun deleteEvent(eventId: String) {
        ApiClient.instance.deleteEvent(eventId).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@AdminEventsActivity, "Event deleted successfully", Toast.LENGTH_SHORT).show()
                    loadEvents()
                } else {
                    Toast.makeText(this@AdminEventsActivity, "Failed to delete event", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@AdminEventsActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}