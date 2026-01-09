package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class StudentProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_profile)

        val sessionManager = SessionManager.getInstance(this)
        val user = sessionManager.getUser()

        // Set profile data
        user?.let {
            findViewById<TextView>(R.id.tvProfileInitials).text = getInitials(it.name ?: "Student")
            findViewById<TextView>(R.id.tvProfileName).text = it.name ?: "Student"
            val year = calculateYear(it.year)
            findViewById<TextView>(R.id.tvDepartmentYear).text = "${it.department ?: "Computer Science"} • $year Year"
            findViewById<TextView>(R.id.tvRollNumber).text = "ID: ${it.rollNo ?: "N/A"}"
            findViewById<TextView>(R.id.tvEmail).text = it.email ?: "Not provided"
            findViewById<TextView>(R.id.tvPhone).text = it.phone ?: "Not provided"
            findViewById<TextView>(R.id.tvAddress).text = it.address ?: "Not provided"
            findViewById<TextView>(R.id.tvCGPA).text = it.cgpa ?: "N/A"
            val batchStart = it.year?.toIntOrNull() ?: 2021
            findViewById<TextView>(R.id.tvBatch).text = "$batchStart - ${batchStart + 4}"
            setupInterests(it.interests)
        }

        // Edit Profile (FAB - hidden)
        findViewById<ImageView>(R.id.ivEditProfile).setOnClickListener {
            startActivityForResult(Intent(this, EditStudentProfileActivity::class.java), EDIT_PROFILE_REQUEST)
        }
        
        // Edit Profile Button
        findViewById<Button>(R.id.btnEditProfile).setOnClickListener {
            startActivityForResult(Intent(this, EditStudentProfileActivity::class.java), EDIT_PROFILE_REQUEST)
        }

        // CORRECTED: Added logout functionality
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            sessionManager.clear()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == RESULT_OK) {
            refreshProfileData()
        }
    }

    private fun refreshProfileData() {
        val sessionManager = SessionManager.getInstance(this)
        val user = sessionManager.getUser()

        user?.let {
            findViewById<TextView>(R.id.tvProfileInitials).text = getInitials(it.name ?: "Student")
            findViewById<TextView>(R.id.tvProfileName).text = it.name ?: "Student"
            val year = calculateYear(it.year)
            findViewById<TextView>(R.id.tvDepartmentYear).text = "${it.department ?: "Computer Science"} • $year Year"
            findViewById<TextView>(R.id.tvRollNumber).text = "ID: ${it.rollNo ?: "N/A"}"
            findViewById<TextView>(R.id.tvEmail).text = it.email ?: "Not provided"
            findViewById<TextView>(R.id.tvPhone).text = it.phone ?: "Not provided"
            findViewById<TextView>(R.id.tvAddress).text = it.address ?: "Not provided"
            findViewById<TextView>(R.id.tvCGPA).text = it.cgpa ?: "N/A"
            val batchStart = it.year?.toIntOrNull() ?: 2021
            findViewById<TextView>(R.id.tvBatch).text = "$batchStart - ${batchStart + 4}"
            setupInterests(it.interests)
        }
    }

    private fun getInitials(name: String): String {
        val parts = name.split(" ")
        return when {
            parts.size >= 2 -> "${parts[0].firstOrNull()?.uppercase() ?: ""}${parts[1].firstOrNull()?.uppercase() ?: ""}"
            parts.isNotEmpty() -> parts[0].take(2).uppercase()
            else -> "ST"
        }
    }

    private fun calculateYear(batchYear: String?): String {
        val startYear = batchYear?.toIntOrNull() ?: return "1st"
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val yearDiff = currentYear - startYear + 1
        return when {
            yearDiff <= 1 -> "1st"
            yearDiff == 2 -> "2nd"
            yearDiff == 3 -> "3rd"
            else -> "${yearDiff}th"
        }
    }

    private fun setupInterests(interests: String?) {
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroupInterests)
        chipGroup.removeAllViews()

        val interestList = interests?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
            ?: listOf("Web Development", "Machine Learning", "UI/UX Design", "Hackathons")

        interestList.forEach { interest ->
            val chip = Chip(this)
            chip.text = interest
            chip.isClickable = false
            chipGroup.addView(chip)
        }
    }

    companion object {
        private const val EDIT_PROFILE_REQUEST = 100

        fun calculateProfileCompletion(user: User?): Int {
            if (user == null) return 0
            var completed = 0
            val total = 8
            if (!user.name.isNullOrEmpty()) completed++
            if (!user.email.isNullOrEmpty()) completed++
            if (!user.phone.isNullOrEmpty()) completed++
            if (!user.rollNo.isNullOrEmpty()) completed++
            if (!user.department.isNullOrEmpty()) completed++
            if (!user.year.isNullOrEmpty()) completed++
            if (!user.cgpa.isNullOrEmpty()) completed++
            if (!user.address.isNullOrEmpty()) completed++
            return (completed * 100) / total
        }
    }
}