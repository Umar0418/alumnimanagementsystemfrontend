package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class EventDetailsActivity : AppCompatActivity() {

    private var eventJson: String = ""
    private var event: EventModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        // Get event data from intent as JSON
        eventJson = intent.getStringExtra("event_data") ?: ""
        val isRegistered = intent.getBooleanExtra("is_registered", false)

        if (eventJson.isEmpty()) {
            Toast.makeText(this, "Error: Event data not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Parse JSON to EventModel
        event = Gson().fromJson(eventJson, EventModel::class.java)

        if (event == null) {
            Toast.makeText(this, "Error: Invalid event data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val currentEvent = event!!

        // Back button
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        // Share button
        findViewById<ImageView>(R.id.ivShare).setOnClickListener {
            shareEvent(currentEvent.title)
        }

        // Set event details
        findViewById<TextView>(R.id.tvEventTitle).text = currentEvent.title
        findViewById<TextView>(R.id.tvHostedBy).text = "Hosted by College Administration"

        // Format and set date
        val formattedDate = formatDate(currentEvent.event_date)
        findViewById<TextView>(R.id.tvEventDate).text = formattedDate
        findViewById<TextView>(R.id.tvEventTime).text = formatTime(currentEvent.event_time)

        // Venue
        findViewById<TextView>(R.id.tvVenueName).text = currentEvent.venue
        findViewById<TextView>(R.id.tvVenueAddress).text = "College Campus"

        // Description
        findViewById<TextView>(R.id.tvEventDescription).text = if (!currentEvent.description.isNullOrEmpty()) {
            currentEvent.description
        } else {
            "Join us for this exciting event! More details will be shared soon. Stay tuned for updates."
        }

        // Attendees count (placeholder - you can add API to get actual count)
        val attendeesCount = 0
        val tvAttendeesCount = findViewById<TextView>(R.id.tvAttendeesCount)
        tvAttendeesCount.text = "Attendees ($attendeesCount)"

        val tvMoreAttendees = findViewById<TextView>(R.id.tvMoreAttendees)
        if (attendeesCount > 5) {
            tvMoreAttendees.text = "+${attendeesCount - 5}"
        } else {
            tvMoreAttendees.text = ""
        }

        // See All attendees
        findViewById<TextView>(R.id.tvSeeAll).setOnClickListener {
            Toast.makeText(this, "Attendee list coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Register button
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        if (isRegistered) {
            btnRegister.text = "Already Registered"
            btnRegister.isEnabled = false
            btnRegister.alpha = 0.6f
        } else {
            btnRegister.setOnClickListener {
                navigateToRegistration()
            }
        }
    }

    private fun formatDate(dateStr: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateStr)
            date?.let { outputFormat.format(it) } ?: dateStr
        } catch (e: Exception) {
            dateStr
        }
    }

    private fun formatTime(timeStr: String): String {
        return try {
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val time = inputFormat.parse(timeStr)
            time?.let { outputFormat.format(it) } ?: timeStr
        } catch (e: Exception) {
            timeStr
        }
    }

    private fun shareEvent(title: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, 
            "Check out this event: $title\n\nRegister now on the Alumni Management App!")
        startActivity(Intent.createChooser(shareIntent, "Share Event"))
    }

    private fun navigateToRegistration() {
        val intent = Intent(this, EventRegistrationActivity::class.java)
        intent.putExtra("event_data", eventJson)
        startActivity(intent)
    }
}
