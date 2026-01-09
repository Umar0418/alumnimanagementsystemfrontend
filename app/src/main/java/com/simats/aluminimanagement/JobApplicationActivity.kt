package com.simats.aluminimanagement

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobApplicationActivity : AppCompatActivity() {

    private var resumeUri: Uri? = null
    private lateinit var tvResumeFileName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_application)

        // Get job data from intent
        val jobId = intent.getStringExtra("job_id") ?: ""
        val jobTitle = intent.getStringExtra("job_title") ?: "Job Title"
        val jobCompany = intent.getStringExtra("job_company") ?: "Company"

        // Back button
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        // Set job info
        findViewById<TextView>(R.id.tvCompanyInitial).text = jobCompany.firstOrNull()?.uppercase() ?: "C"
        findViewById<TextView>(R.id.tvJobTitle).text = jobTitle
        findViewById<TextView>(R.id.tvCompanyName).text = jobCompany

        // Pre-fill user info from session
        val sessionManager = SessionManager.getInstance(this)
        val user = sessionManager.getUser()
        
        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        
        user?.let {
            etFullName.setText(it.name ?: "")
            etEmail.setText(it.email ?: "")
            etPhone.setText(it.phone ?: "")
        }

        // Experience spinner
        val spinnerExperience = findViewById<Spinner>(R.id.spinnerExperience)
        val experiences = arrayOf("Fresher", "0-1 Years", "1-2 Years", "2-3 Years", "3-5 Years", "5+ Years")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, experiences)
        spinnerExperience.adapter = adapter

        // Resume upload
        tvResumeFileName = findViewById(R.id.tvResumeFileName)
        findViewById<LinearLayout>(R.id.layoutUploadResume).setOnClickListener {
            pickResume()
        }

        // Submit button
        findViewById<Button>(R.id.btnSubmitApplication).setOnClickListener {
            submitApplication(jobId, jobTitle, jobCompany)
        }
    }

    private fun pickResume() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ))
        startActivityForResult(Intent.createChooser(intent, "Select Resume"), PICK_RESUME_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_RESUME_REQUEST && resultCode == Activity.RESULT_OK) {
            resumeUri = data?.data
            resumeUri?.let { uri ->
                val fileName = getFileName(uri)
                tvResumeFileName.text = "✓ $fileName"
                tvResumeFileName.setTextColor(getColor(android.R.color.holo_green_dark))
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var result = "Resume uploaded"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index >= 0) {
                    result = it.getString(index)
                }
            }
        }
        return result
    }

    private fun submitApplication(jobId: String, jobTitle: String, jobCompany: String) {
        val fullName = findViewById<EditText>(R.id.etFullName).text.toString().trim()
        val email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
        val phone = findViewById<EditText>(R.id.etPhone).text.toString().trim()
        val skills = findViewById<EditText>(R.id.etSkills).text.toString().trim()
        val experience = findViewById<Spinner>(R.id.spinnerExperience).selectedItem.toString()
        val currentCompany = findViewById<EditText>(R.id.etCurrentCompany).text.toString().trim()
        val linkedIn = findViewById<EditText>(R.id.etLinkedIn).text.toString().trim()
        val coverLetter = findViewById<EditText>(R.id.etCoverLetter).text.toString().trim()
        val expectedSalary = findViewById<EditText>(R.id.etExpectedSalary).text.toString().trim()

        // Validation
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show()
            return
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }
        if (phone.isEmpty()) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show()
            return
        }
        if (skills.isEmpty()) {
            Toast.makeText(this, "Please enter your key skills", Toast.LENGTH_SHORT).show()
            return
        }
        if (resumeUri == null) {
            Toast.makeText(this, "Please upload your resume", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading
        val loadingDialog = AlertDialog.Builder(this)
            .setMessage("Submitting your application...")
            .setCancelable(false)
            .create()
        loadingDialog.show()

        // Get roll number
        val sessionManager = SessionManager.getInstance(this)
        val rollNo = sessionManager.getRollNo() ?: ""

        // Call API to submit application
        ApiClient.instance.submitJobApplication(
            rollNo = rollNo,
            jobId = jobId,
            fullName = fullName,
            email = email,
            phone = phone,
            skills = skills,
            experience = experience,
            currentCompany = currentCompany,
            linkedIn = linkedIn,
            coverLetter = coverLetter,
            expectedSalary = expectedSalary
        ).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                loadingDialog.dismiss()
                if (response.isSuccessful && response.body()?.status == true) {
                    showSuccessDialog(jobTitle, jobCompany)
                } else {
                    Toast.makeText(this@JobApplicationActivity, 
                        response.body()?.message ?: "Failed to submit application", 
                        Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loadingDialog.dismiss()
                Toast.makeText(this@JobApplicationActivity, 
                    "Network Error: ${t.message}", 
                    Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showSuccessDialog(jobTitle: String, jobCompany: String) {
        AlertDialog.Builder(this)
            .setTitle("Application Submitted! 🎉")
            .setMessage("Your application for $jobTitle at $jobCompany has been submitted successfully.\n\nThe company will review your application and get back to you soon.")
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    companion object {
        private const val PICK_RESUME_REQUEST = 1001
    }
}
