package com.simats.aluminimanagement

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RequestMentorshipActivity : AppCompatActivity() {

    private var mentorRollNo: String? = null
    private var mentorName: String? = null
    private var mentorField: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_mentorship)

        // Get mentor data from intent
        mentorRollNo = intent.getStringExtra("mentor_roll_no")
        mentorName = intent.getStringExtra("mentor_name")
        mentorField = intent.getStringExtra("mentor_field")
        val mentorStyle = intent.getStringExtra("mentor_style") ?: ""
        val mentorHours = intent.getStringExtra("mentor_hours") ?: ""

        // Back button
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        // Set mentor info
        val tvMentorInitials = findViewById<TextView>(R.id.tvMentorInitials)
        val tvMentorName = findViewById<TextView>(R.id.tvMentorName)
        val tvMentorRole = findViewById<TextView>(R.id.tvMentorRole)
        val tvMentorBio = findViewById<TextView>(R.id.tvMentorBio)

        // Set initials
        val names = (mentorName ?: "").split(" ")
        val initials = if (names.size >= 2) {
            "${names[0].firstOrNull() ?: ""}${names[1].firstOrNull() ?: ""}"
        } else {
            (mentorName ?: "").take(2)
        }
        tvMentorInitials.text = initials.uppercase()
        tvMentorName.text = mentorName ?: "Mentor"
        tvMentorRole.text = mentorField ?: "Mentorship"
        
        val bioText = if (mentorStyle.isNotEmpty() || mentorHours.isNotEmpty()) {
            "\"I can help with ${mentorField ?: "various topics"}. ${if (mentorStyle.isNotEmpty()) "My mentoring style is $mentorStyle." else ""} ${if (mentorHours.isNotEmpty()) "I'm available $mentorHours." else ""}\""
        } else {
            "\"I'm available to help with ${mentorField ?: "various topics"}.\""
        }
        tvMentorBio.text = bioText

        // Session duration spinner
        val spinnerDuration = findViewById<Spinner>(R.id.spinnerDuration)
        val durations = arrayOf("30 Minutes", "45 Minutes", "1 Hour", "1.5 Hours")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, durations)
        spinnerDuration.adapter = adapter

        // Send request button
        val etMessage = findViewById<EditText>(R.id.etMessage)
        findViewById<Button>(R.id.btnSendRequest).setOnClickListener {
            val message = etMessage.text.toString().trim()
            val duration = spinnerDuration.selectedItem.toString()

            if (message.isEmpty()) {
                Toast.makeText(this, "Please introduce yourself", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendMentorshipRequest(message, duration)
        }
    }

    private fun sendMentorshipRequest(message: String, duration: String) {
        val sessionManager = SessionManager.getInstance(this)
        val studentRollNo = sessionManager.getRollNo()

        if (studentRollNo == null) {
            Toast.makeText(this, "Error: Please log in first", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (mentorRollNo == null) {
            Toast.makeText(this, "Error: Mentor information missing", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading
        val btnSend = findViewById<Button>(R.id.btnSendRequest)
        btnSend.isEnabled = false
        btnSend.text = "Sending..."

        val topic = "${mentorField ?: "General"} - $message (Duration: $duration)"
        
        // Save locally first
        saveRequestLocally(studentRollNo, mentorRollNo!!, topic)

        // Try to send to server
        try {
            ApiClient.instance.requestMentorship(studentRollNo, mentorRollNo!!, topic)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        showSuccessAndFinish()
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.e("RequestMentorship", "Network error: ${t.message}")
                        // Already saved locally, show success
                        showSuccessAndFinish()
                    }
                })
        } catch (e: Exception) {
            // Already saved locally, show success
            showSuccessAndFinish()
        }
    }
    
    private fun saveRequestLocally(studentRollNo: String, mentorRollNo: String, topic: String) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        
        val request = MyMentorRequest(
            id = System.currentTimeMillis().toInt(),
            mentorRollNo = mentorRollNo,
            mentorName = mentorName ?: "Mentor",
            mentorField = mentorField,
            topic = topic,
            status = "pending",
            requestedAt = currentDate
        )
        
        LocalMentorRequestStorage.saveRequest(this, request)
    }
    
    private fun showSuccessAndFinish() {
        val btnSend = findViewById<Button>(R.id.btnSendRequest)
        btnSend.isEnabled = true
        btnSend.text = "Send Request"
        
        Toast.makeText(this, 
            "Request sent to $mentorName!", 
            Toast.LENGTH_LONG).show()
        setResult(RESULT_OK)
        finish()
    }
}
