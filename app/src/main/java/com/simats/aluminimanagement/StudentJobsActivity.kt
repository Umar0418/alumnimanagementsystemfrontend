package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentJobsActivity : AppCompatActivity(), StudentJobClickListener {

    private lateinit var recyclerJobs: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var etSearch: EditText
    private lateinit var adapter: JobListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_jobs)

        recyclerJobs = findViewById(R.id.recyclerJobs)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)
        etSearch = findViewById(R.id.etSearch)

        recyclerJobs.layoutManager = LinearLayoutManager(this)
        adapter = JobListAdapter(emptyList(), this)
        recyclerJobs.adapter = adapter

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        // Search functionality
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        setupCategoryChips()
        setupBottomNavigation()
        loadJobs()
    }

    private fun setupCategoryChips() {
        val chips = listOf(
            findViewById<TextView>(R.id.chipAll) to "All",
            findViewById<TextView>(R.id.chipInternships) to "Internships",
            findViewById<TextView>(R.id.chipFullTime) to "Full-time",
            findViewById<TextView>(R.id.chipRemote) to "Remote",
            findViewById<TextView>(R.id.chipEngineering) to "Engineering"
        )

        chips.forEach { (chip, type) ->
            chip.setOnClickListener {
                chips.forEach { (c, _) ->
                    c.setBackgroundResource(R.drawable.chip_unselected_bg)
                    c.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
                }
                chip.setBackgroundResource(R.drawable.chip_selected_bg)
                chip.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                adapter.filterByType(type)
            }
        }
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_jobs

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, StudentDashboardActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_alumni -> {
                    startActivity(Intent(this, AlumniDirectoryActivity::class.java))
                    true
                }
                R.id.nav_jobs -> true
                R.id.nav_events -> {
                    startActivity(Intent(this, AlumniEventsActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, StudentProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun loadJobs() {
        progressBar.visibility = View.VISIBLE
        recyclerJobs.visibility = View.GONE
        emptyState.visibility = View.GONE

        // Use cached data for instant loading
        DataCache.getJobs({ jobs ->
            runOnUiThread {
                progressBar.visibility = View.GONE
                
                if (jobs.isEmpty()) {
                    emptyState.visibility = View.VISIBLE
                    recyclerJobs.visibility = View.GONE
                } else {
                    emptyState.visibility = View.GONE
                    recyclerJobs.visibility = View.VISIBLE
                    adapter.updateList(jobs)
                }
            }
        })
    }

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
}
