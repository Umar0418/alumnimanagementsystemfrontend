package com.simats.aluminimanagement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class StudentJobDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_job_details)

        // Get job data from intent
        val jobId = intent.getStringExtra("job_id") ?: ""
        val jobTitle = intent.getStringExtra("job_title") ?: "Job Title"
        val jobCompany = intent.getStringExtra("job_company") ?: "Company"
        val jobDescription = intent.getStringExtra("job_description") ?: ""
        val jobLocation = intent.getStringExtra("job_location") ?: "Remote"
        val jobType = intent.getStringExtra("job_type") ?: "Full-time"
        val jobSalary = intent.getStringExtra("job_salary") ?: "Not disclosed"
        val jobLastDate = intent.getStringExtra("job_last_date") ?: ""

        // Back button
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        // Company initial
        findViewById<TextView>(R.id.tvCompanyInitial).text = jobCompany.firstOrNull()?.uppercase() ?: "C"

        // Job info
        findViewById<TextView>(R.id.tvJobTitle).text = jobTitle
        findViewById<TextView>(R.id.tvCompanyName).text = jobCompany
        findViewById<TextView>(R.id.tvJobType).text = jobType
        findViewById<TextView>(R.id.tvLocationTag).text = if (jobLocation.contains("Remote", ignoreCase = true)) "Remote" else "On-site"

        // Details grid
        findViewById<TextView>(R.id.tvLocation).text = jobLocation
        findViewById<TextView>(R.id.tvSalary).text = jobSalary
        findViewById<TextView>(R.id.tvExperience).text = "Fresher"
        findViewById<TextView>(R.id.tvLastDate).text = if (jobLastDate.isNotEmpty()) jobLastDate else "Open"

        // Description
        findViewById<TextView>(R.id.tvDescription).text = if (jobDescription.isNullOrEmpty()) {
            "No description available for this position. Please contact the company directly for more information about the role and responsibilities."
        } else {
            jobDescription
        }

        // Add requirements dynamically
        val requirementsList = findViewById<LinearLayout>(R.id.requirementsList)
        val requirements = listOf(
            "Relevant educational background",
            "Good communication skills",
            "Willingness to learn",
            "Team player"
        )

        requirements.forEach { requirement ->
            val requirementView = layoutInflater.inflate(R.layout.item_requirement, requirementsList, false)
            requirementView.findViewById<TextView>(R.id.tvRequirement).text = requirement
            requirementsList.addView(requirementView)
        }

        // Apply button - Navigate to application form
        findViewById<Button>(R.id.btnApply).setOnClickListener {
            val intent = Intent(this, JobApplicationActivity::class.java)
            intent.putExtra("job_id", jobId)
            intent.putExtra("job_title", jobTitle)
            intent.putExtra("job_company", jobCompany)
            startActivity(intent)
        }
    }
}
