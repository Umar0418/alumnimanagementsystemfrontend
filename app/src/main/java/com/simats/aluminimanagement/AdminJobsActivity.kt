package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminJobsActivity : AppCompatActivity(), JobClickListener {

    private lateinit var recyclerJobs: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_jobs)

        recyclerJobs = findViewById(R.id.recyclerJobs)
        recyclerJobs.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        findViewById<ExtendedFloatingActionButton>(R.id.fabPostJob).setOnClickListener {
            startActivity(Intent(this, CreateJobActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadJobs()
    }

    private fun loadJobs() {
        ApiClient.instance.getJobs().enqueue(object : Callback<JobListResponse> {
            override fun onResponse(call: Call<JobListResponse>, response: Response<JobListResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    response.body()?.jobs?.let {
                        recyclerJobs.adapter = JobAdapter(it, this@AdminJobsActivity)
                    } ?: run {
                        recyclerJobs.adapter = JobAdapter(emptyList(), this@AdminJobsActivity)
                        Toast.makeText(this@AdminJobsActivity, "No jobs found.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AdminJobsActivity, "Failed to load jobs.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JobListResponse>, t: Throwable) {
                Toast.makeText(this@AdminJobsActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onApproveClicked(job: JobModel) {
        updateJobStatus(job.id, "active")
    }

    override fun onRejectClicked(job: JobModel) {
        updateJobStatus(job.id, "rejected")
    }

    override fun onEditClicked(job: JobModel) {
        val intent = Intent(this, CreateJobActivity::class.java)
        intent.putExtra("job_data", Gson().toJson(job))
        startActivity(intent)
    }

    override fun onDeleteClicked(job: JobModel) {
        AlertDialog.Builder(this)
            .setTitle("Delete Job")
            .setMessage("Are you sure you want to delete the job posting for '${job.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                deleteJob(job.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateJobStatus(jobId: String, status: String) {
        ApiClient.instance.updateJobStatus(jobId, status).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@AdminJobsActivity, "Job status updated.", Toast.LENGTH_SHORT).show()
                    loadJobs()
                } else {
                    Toast.makeText(this@AdminJobsActivity, "Failed to update job status.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@AdminJobsActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun deleteJob(jobId: String) {
        ApiClient.instance.deleteJob(jobId).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@AdminJobsActivity, "Job deleted successfully.", Toast.LENGTH_SHORT).show()
                    loadJobs()
                } else {
                    Toast.makeText(this@AdminJobsActivity, "Failed to delete job.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@AdminJobsActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
