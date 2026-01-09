package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentDashboardActivity : AppCompatActivity(), StudentJobClickListener {
    
    private lateinit var recyclerUpcomingEvents: RecyclerView
    private lateinit var recyclerRecommendedJobs: RecyclerView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        val sessionManager = SessionManager.getInstance(this)
        val user = sessionManager.getUser()

        // Set user info
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val tvStudentInfo = findViewById<TextView>(R.id.tvStudentInfo)
        val tvUserInitials = findViewById<TextView>(R.id.tvUserInitials)
        val tvProfileProgress = findViewById<TextView>(R.id.tvProfileProgress)

        user?.let {
            tvWelcome.text = "Hello, ${it.name?.split(" ")?.firstOrNull() ?: "Student"}!"
            
            // Better display for department and year
            val dept = if (!it.department.isNullOrEmpty()) it.department else "Computer Science"
            val yearDisplay = if (!it.year.isNullOrEmpty()) {
                val startYear = it.year.toIntOrNull() ?: 2021
                val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                val yearNum = currentYear - startYear + 1
                when {
                    yearNum <= 1 -> "1st Year"
                    yearNum == 2 -> "2nd Year"
                    yearNum == 3 -> "3rd Year"
                    yearNum >= 4 -> "4th Year"
                    else -> "Student"
                }
            } else "Student"
            
            tvStudentInfo.text = "$dept • $yearDisplay"
            
            // Set initials
            val names = it.name?.split(" ") ?: listOf("S", "N")
            val initials = if (names.size >= 2) {
                "${names[0].firstOrNull() ?: ""}${names[1].firstOrNull() ?: ""}"
            } else {
                names.firstOrNull()?.take(2) ?: "SN"
            }
            tvUserInitials.text = initials.uppercase()

            // Calculate and display profile completion %
            val profileCompletion = StudentProfileActivity.calculateProfileCompletion(it)
            tvProfileProgress.text = "$profileCompletion% complete"
        }

        // Click on user initials to open profile
        tvUserInitials.setOnClickListener {
            startActivity(Intent(this, StudentProfileActivity::class.java))
        }

        // Initialize RecyclerViews
        recyclerUpcomingEvents = findViewById(R.id.recyclerUpcomingEvents)
        recyclerRecommendedJobs = findViewById(R.id.recyclerRecommendedJobs)
        
        recyclerUpcomingEvents.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerRecommendedJobs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Load data
        loadUpcomingEvents()
        loadRecommendedJobs()

        // View Profile Button
        findViewById<Button>(R.id.btnViewProfile).setOnClickListener {
            startActivity(Intent(this, StudentProfileActivity::class.java))
        }
        
        // Notification Icon - open announcements
        findViewById<ImageView>(R.id.ivNotification).setOnClickListener {
            startActivity(Intent(this, StudentAnnouncementsActivity::class.java))
        }

        // Quick Actions
        findViewById<CardView>(R.id.cardMentorship).setOnClickListener {
            startActivity(Intent(this, StudentMentorshipActivity::class.java))
        }

        findViewById<CardView>(R.id.cardJobs).setOnClickListener {
            startActivity(Intent(this, StudentJobsActivity::class.java))
        }

        findViewById<CardView>(R.id.cardEvents).setOnClickListener {
            startActivity(Intent(this, AlumniEventsActivity::class.java))
        }

        findViewById<CardView>(R.id.cardCommunity).setOnClickListener {
            startActivity(Intent(this, CommunityGroupsActivity::class.java))
        }

        // See All Events
        findViewById<TextView>(R.id.tvSeeAllEvents).setOnClickListener {
            startActivity(Intent(this, AlumniEventsActivity::class.java))
        }

        // See All Jobs
        findViewById<TextView>(R.id.tvSeeAllJobs).setOnClickListener {
            startActivity(Intent(this, StudentJobsActivity::class.java))
        }

        // Bottom Navigation
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        // Reset bottom navigation to Home when returning to dashboard
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home
    }

    private fun loadUpcomingEvents() {
        // Use cached data for instant loading
        DataCache.getEvents({ events ->
            runOnUiThread {
                val displayEvents = events.take(5)
                if (displayEvents.isNotEmpty()) {
                    recyclerUpcomingEvents.adapter = StudentEventAdapter(displayEvents) { event ->
                        // Handle event click - navigate to event details
                        val intent = Intent(this@StudentDashboardActivity, EventDetailsActivity::class.java)
                        intent.putExtra("event_data", Gson().toJson(event))
                        intent.putExtra("is_registered", false)
                        startActivity(intent)
                    }
                }
            }
        })
    }

    private fun loadRecommendedJobs() {
        // Use cached data for instant loading
        DataCache.getJobs({ jobs ->
            runOnUiThread {
                val displayJobs = jobs.take(3)
                if (displayJobs.isNotEmpty()) {
                    recyclerRecommendedJobs.adapter = StudentJobAdapter(displayJobs, this@StudentDashboardActivity)
                }
            }
        })
    }

    // StudentJobClickListener implementation - open job details
    override fun onJobClick(job: JobModel) {
        val intent = Intent(this, StudentJobDetailsActivity::class.java)
        intent.putExtra("job_id", job.id)
        intent.putExtra("job_title", job.title)
        intent.putExtra("job_company", job.company)
        intent.putExtra("job_description", job.description)
        intent.putExtra("job_location", job.location)
        intent.putExtra("job_type", job.job_type)
        intent.putExtra("job_salary", job.salary)
        intent.putExtra("job_last_date", job.last_date)
        startActivity(intent)
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home
        
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_alumni -> {
                    // Stay as student, just view alumni directory
                    startActivity(Intent(this, AlumniDirectoryActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.nav_jobs -> {
                    startActivity(Intent(this, StudentJobsActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.nav_events -> {
                    startActivity(Intent(this, AlumniEventsActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, StudentProfileActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                else -> false
            }
        }
    }
}