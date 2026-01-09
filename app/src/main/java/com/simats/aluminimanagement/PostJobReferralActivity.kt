package com.simats.aluminimanagement

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostJobReferralActivity : AppCompatActivity() {

    private lateinit var etJobTitle: EditText
    private lateinit var etCompany: EditText
    private lateinit var etLocation: EditText
    private lateinit var etJobType: EditText
    private lateinit var etExperience: EditText
    private lateinit var etDescription: EditText
    private lateinit var etApplicationLink: EditText
    private lateinit var btnPostJob: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_job_referral)

        etJobTitle = findViewById(R.id.etJobTitle)
        etCompany = findViewById(R.id.etCompany)
        etLocation = findViewById(R.id.etLocation)
        etJobType = findViewById(R.id.etJobType)
        etExperience = findViewById(R.id.etExperience)
        etDescription = findViewById(R.id.etDescription)
        etApplicationLink = findViewById(R.id.etApplicationLink)
        btnPostJob = findViewById(R.id.btnPostJob)
        progressBar = findViewById(R.id.progressBar)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        btnPostJob.setOnClickListener { submitJob() }
    }

    private fun submitJob() {
        val title = etJobTitle.text.toString().trim()
        val company = etCompany.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val jobType = etJobType.text.toString().trim().ifEmpty { "Full-time" }
        val experience = etExperience.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val applicationLink = etApplicationLink.text.toString().trim()

        // Validation
        if (title.isEmpty()) {
            etJobTitle.error = "Job title is required"
            return
        }
        if (company.isEmpty()) {
            etCompany.error = "Company name is required"
            return
        }

        val sessionManager = SessionManager.getInstance(this)
        val rollNo = sessionManager.getRollNo()
        val userName = sessionManager.getUser()?.name ?: "Alumni"

        if (rollNo == null) {
            Toast.makeText(this, "Please login to post a job", Toast.LENGTH_SHORT).show()
            return
        }

        btnPostJob.isEnabled = false
        progressBar.visibility = View.VISIBLE

        // Combine experience with description
        val fullDescription = if (experience.isNotEmpty()) {
            "Experience: $experience\n\n$description\n\nApply: $applicationLink"
        } else {
            "$description\n\nApply: $applicationLink"
        }

        ApiClient.instance.postJobReferral(
            rollNo = rollNo,
            title = title,
            company = company,
            description = fullDescription,
            location = location,
            jobType = jobType,
            salary = experience, // Using salary field for experience
            lastDate = "" // Optional - admin can set
        ).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                progressBar.visibility = View.GONE
                btnPostJob.isEnabled = true
                
                val body = response.body()
                if (response.isSuccessful && body != null && body.status) {
                    Toast.makeText(this@PostJobReferralActivity, 
                        "Job referral submitted! Waiting for admin approval.", 
                        Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@PostJobReferralActivity, 
                        body?.message ?: "Failed to submit", 
                        Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                btnPostJob.isEnabled = true
                Toast.makeText(this@PostJobReferralActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
