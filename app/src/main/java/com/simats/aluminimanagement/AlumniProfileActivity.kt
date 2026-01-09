package com.simats.aluminimanagement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlumniProfileActivity : AppCompatActivity() {

    private var currentProfile: AlumniProfile? = null

    private val editProfileResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            loadProfileData() // Refresh data if the profile was saved
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumni_profile)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val collapsingToolbar = findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)
        collapsingToolbar.title = " " // Set an empty title to start

        // CORRECTED: Added the logout functionality
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            val sessionManager = SessionManager.getInstance(this)
            sessionManager.clear()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        findViewById<FloatingActionButton>(R.id.fabEditProfile).setOnClickListener {
            launchEditProfile()
        }
        
        // New Edit Profile button
        findViewById<Button>(R.id.btnEditProfile).setOnClickListener {
            launchEditProfile()
        }

        setupBottomNavigation()
        loadProfileData()
    }
    
    private fun setupBottomNavigation() {
        try {
            val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
            bottomNav?.let { nav ->
                nav.selectedItemId = R.id.nav_profile
                
                nav.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.nav_home -> {
                            startActivity(Intent(this, AlumniDashboardActivity::class.java))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finish()
                            true
                        }
                        R.id.nav_directory -> {
                            startActivity(Intent(this, AlumniDirectoryActivity::class.java))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finish()
                            true
                        }
                        R.id.nav_jobs -> {
                            startActivity(Intent(this, AlumniJobsActivity::class.java))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finish()
                            true
                        }
                        R.id.nav_events -> {
                            startActivity(Intent(this, AlumniEventsActivity::class.java))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finish()
                            true
                        }
                        R.id.nav_profile -> true
                        else -> false
                    }
                }
            }
        } catch (e: Exception) {
            // Bottom nav doesn't exist
        }
    }
    
    private fun launchEditProfile() {
        currentProfile?.let {
            val intent = Intent(this, EditAlumniProfileActivity::class.java)
            intent.putExtra("profile_data", Gson().toJson(it))
            editProfileResultLauncher.launch(intent)
        } ?: Toast.makeText(this, "Profile data not loaded yet.", Toast.LENGTH_SHORT).show()
    }

    private fun loadProfileData() {
        val sessionManager = SessionManager.getInstance(this)
        val rollNo = sessionManager.getUser()?.rollNo

        if (rollNo == null) {
            Toast.makeText(this, "Could not retrieve profile: User not logged in.", Toast.LENGTH_LONG).show()
            return
        }

        ApiClient.instance.getAlumniProfile(rollNo).enqueue(object : Callback<AlumniProfileResponse> {
            override fun onResponse(call: Call<AlumniProfileResponse>, response: Response<AlumniProfileResponse>) {
                val profileResponse = response.body()
                if (response.isSuccessful && profileResponse != null && profileResponse.status) {
                    currentProfile = profileResponse.profile
                    currentProfile?.let { populateUI(it) }
                } else {
                    Toast.makeText(this@AlumniProfileActivity, profileResponse?.message ?: "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AlumniProfileResponse>, t: Throwable) {
                Toast.makeText(this@AlumniProfileActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateUI(profile: AlumniProfile) {
        // Header
        findViewById<TextView>(R.id.tvProfileInitial).text = profile.name.firstOrNull()?.toString()?.uppercase() ?: ""
        findViewById<TextView>(R.id.tvProfileName).text = profile.name
        
        val loggedInUser = SessionManager.getInstance(this).getUser()
        findViewById<TextView>(R.id.tvEmail).text = loggedInUser?.email ?: "No email"

        findViewById<TextView>(R.id.tvProfileTitle).text = "Works at ${profile.company ?: "Not specified"}"
        findViewById<TextView>(R.id.tvProfileLocation).text = profile.location ?: "Not available"

        // Academic Info
        findViewById<TextView>(R.id.tvProfileBatch).text = profile.batchYear ?: ""
        findViewById<TextView>(R.id.tvProfileDepartment).text = profile.department ?: ""
        findViewById<TextView>(R.id.tvProfileRollNo).text = profile.rollNo

        // Experience
        findViewById<TextView>(R.id.tvExperienceTitle).text = "${profile.company ?: "Not specified"}"
        findViewById<TextView>(R.id.tvExperienceCompany).text = "${profile.company ?: ""} • ${profile.batchYear ?: ""} - Present"
        findViewById<TextView>(R.id.tvExperienceDescription).visibility = View.GONE 
    }
}