package com.simats.aluminimanagement

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ViewAlumniProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_alumni_profile)

        // Back button
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        // Get alumni data from intent
        val rollNo = intent.getStringExtra("roll_no") ?: ""
        val name = intent.getStringExtra("name") ?: "Unknown"
        val department = intent.getStringExtra("department") ?: ""
        val batchYear = intent.getStringExtra("batch_year") ?: ""
        val company = intent.getStringExtra("company") ?: "Not specified"
        val location = intent.getStringExtra("location") ?: "Not specified"
        val mentorship = intent.getIntExtra("mentorship", 0)

        // Populate the UI
        findViewById<TextView>(R.id.tvProfileInitial).text = name.firstOrNull()?.toString()?.uppercase() ?: ""
        findViewById<TextView>(R.id.tvProfileName).text = name
        findViewById<TextView>(R.id.tvProfileTitle).text = "Works at $company"
        findViewById<TextView>(R.id.tvProfileLocation).text = location

        // Academic Info
        findViewById<TextView>(R.id.tvProfileBatch).text = batchYear
        findViewById<TextView>(R.id.tvProfileDepartment).text = department
        findViewById<TextView>(R.id.tvProfileRollNo).text = rollNo

        // Experience
        findViewById<TextView>(R.id.tvExperienceTitle).text = company
        findViewById<TextView>(R.id.tvExperienceCompany).text = "$company • $batchYear - Present"

        // Mentorship status
        val mentorshipStatus = if (mentorship == 1) "Available for Mentorship" else "Not Available for Mentorship"
        findViewById<TextView>(R.id.tvMentorshipStatus).text = mentorshipStatus
    }
}
