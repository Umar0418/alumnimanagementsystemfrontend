package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlumniJobsActivity : AppCompatActivity(), AlumniJobClickListener {

    private val TAG = "AlumniJobsActivity"
    private lateinit var recyclerJobs: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumni_jobs)

        recyclerJobs = findViewById(R.id.recyclerJobs)
        recyclerJobs.layoutManager = LinearLayoutManager(this)
        
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.llEmptyState)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
        
        findViewById<ImageView>(R.id.ivPostJob).setOnClickListener {
            try {
                startActivity(Intent(this, PostJobReferralActivity::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Error opening PostJobReferralActivity: ${e.message}")
                Toast.makeText(this, "Unable to open post job screen", Toast.LENGTH_SHORT).show()
            }
        }

        setupBottomNavigation()
        loadJobs()
    }
    
    private fun setupBottomNavigation() {
        try {
            val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
            bottomNav?.let { nav ->
                nav.selectedItemId = R.id.nav_jobs
                
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
                        R.id.nav_jobs -> true
                        R.id.nav_events -> {
                            startActivity(Intent(this, AlumniEventsActivity::class.java))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finish()
                            true
                        }
                        R.id.nav_profile -> {
                            startActivity(Intent(this, AlumniProfileActivity::class.java))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finish()
                            true
                        }
                        else -> false
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Bottom nav error: ${e.message}")
        }
    }

    override fun onResume() {
        super.onResume()
        loadJobs()
    }

    private fun loadJobs() {
        progressBar.visibility = View.VISIBLE
        emptyState.visibility = View.GONE
        recyclerJobs.visibility = View.GONE

        Log.d(TAG, "Loading jobs...")
        
        // Use cached data for instant loading
        DataCache.getJobs({ allJobs ->
            runOnUiThread {
                progressBar.visibility = View.GONE
                Log.d(TAG, "Jobs loaded: ${allJobs.size}")
                
                if (allJobs.isEmpty()) {
                    emptyState.visibility = View.VISIBLE
                    recyclerJobs.visibility = View.GONE
                } else {
                    emptyState.visibility = View.GONE
                    recyclerJobs.visibility = View.VISIBLE
                    recyclerJobs.adapter = AlumniJobAdapter(allJobs, this@AlumniJobsActivity)
                    
                    findViewById<TextView>(R.id.tvTotalJobs)?.text = allJobs.size.toString()
                    
                    // Count new jobs (if we have date info)
                    val newJobsCount = allJobs.size // For now show total
                    findViewById<TextView>(R.id.tvNewJobs)?.text = newJobsCount.toString()
                    
                    try {
                        val slideUp = AnimationUtils.loadAnimation(this@AlumniJobsActivity, R.anim.slide_up)
                        recyclerJobs.startAnimation(slideUp)
                    } catch (e: Exception) {
                        Log.e(TAG, "Animation error: ${e.message}")
                    }
                }
            }
        })
    }

    override fun onViewDetails(job: JobModel) {
        try {
            val intent = Intent(this, JobDetailsActivity::class.java)
            intent.putExtra("job_id", job.id)
            intent.putExtra("job_title", job.title)
            intent.putExtra("job_company", job.company)
            intent.putExtra("job_location", job.location)
            intent.putExtra("job_type", job.job_type)
            intent.putExtra("job_salary", job.salary)
            intent.putExtra("job_description", job.description)
            intent.putExtra("job_deadline", job.last_date)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening job details: ${e.message}")
            Toast.makeText(this, "Unable to open job details", Toast.LENGTH_SHORT).show()
        }
    }
}