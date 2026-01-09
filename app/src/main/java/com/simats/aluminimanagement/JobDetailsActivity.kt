package com.simats.aluminimanagement

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class JobDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        ivBack.setOnClickListener { finish() }

        // Get data from Intent
        val title = intent.getStringExtra("job_title")
        val company = intent.getStringExtra("job_company")
        val location = intent.getStringExtra("job_location")
        val jobType = intent.getStringExtra("job_type")
        val salary = intent.getStringExtra("job_salary")
        val description = intent.getStringExtra("job_description")
        val deadline = intent.getStringExtra("job_deadline")

        // Find Views
        val tvTitle = findViewById<TextView>(R.id.tvJobTitleDetail)
        val tvCompany = findViewById<TextView>(R.id.tvCompanyDetail)
        val tvLocation = findViewById<TextView>(R.id.tvLocationDetail)
        val tvJobType = findViewById<TextView>(R.id.tvJobTypeDetail)
        val tvSalary = findViewById<TextView>(R.id.tvSalaryDetail)
        val tvDescription = findViewById<TextView>(R.id.tvJobDescriptionDetail)
        val tvDeadline = findViewById<TextView>(R.id.tvDeadlineDetail)
        val btnApplyNow = findViewById<Button>(R.id.btnApplyNow)

        // Set Data
        tvTitle.text = title ?: "Not Available"
        tvCompany.text = company ?: "Not Available"
        tvLocation.text = location ?: "Not specified"
        tvJobType.text = jobType ?: "Not specified"
        tvSalary.text = salary ?: "Not specified"
        tvDescription.text = description ?: "No description provided."
        tvDeadline.text = "Apply by: ${deadline ?: "Not specified"}"

        // Check user type and hide Apply button for admin
        val sessionManager = SessionManager.getInstance(this)
        val userType = sessionManager.getUser()?.userType ?: ""
        
        if (userType.equals("admin", ignoreCase = true)) {
            // Hide apply button for admin
            btnApplyNow.visibility = View.GONE
        } else {
            // Show apply button for students/alumni
            btnApplyNow.visibility = View.VISIBLE
            btnApplyNow.setOnClickListener {
                // Handle apply action - can be implemented later
                android.widget.Toast.makeText(this, "Apply functionality coming soon", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}