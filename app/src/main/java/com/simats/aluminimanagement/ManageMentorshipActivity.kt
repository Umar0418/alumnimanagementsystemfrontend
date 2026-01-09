package com.simats.aluminimanagement

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageMentorshipActivity : AppCompatActivity(), MentorClickListener {

    private val TAG = "ManageMentorship"
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: MentorAdapter
    
    private var allRequests: List<MentorModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_mentorship)

        recyclerView = findViewById(R.id.recyclerMentorship)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.llEmptyState)
        tabLayout = findViewById(R.id.tabLayout)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        // Initialize adapter once with empty list
        adapter = MentorAdapter(emptyList(), this)
        recyclerView.adapter = adapter

        setupTabListener()
        loadMentors()
    }

    override fun onResume() {
        super.onResume()
        loadMentors()
    }

    private fun setupTabListener() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                filterByStatus(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun filterByStatus(position: Int) {
        val filtered = when (position) {
            1 -> allRequests.filter { it.status.lowercase() == "pending" }
            2 -> allRequests.filter { it.status.lowercase() == "approved" }
            3 -> allRequests.filter { it.status.lowercase() == "rejected" }
            else -> allRequests
        }
        
        Log.d(TAG, "Filtering by position $position: ${filtered.size} results")
        adapter.updateList(filtered)
        
        emptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun updateStats() {
        val pending = allRequests.count { it.status.lowercase() == "pending" }
        val approved = allRequests.count { it.status.lowercase() == "approved" }
        val rejected = allRequests.count { it.status.lowercase() == "rejected" }

        Log.d(TAG, "Stats - Pending: $pending, Approved: $approved, Rejected: $rejected")

        findViewById<TextView>(R.id.tvPendingCount)?.text = pending.toString()
        findViewById<TextView>(R.id.tvApprovedCount)?.text = approved.toString()
        findViewById<TextView>(R.id.tvRejectedCount)?.text = rejected.toString()
    }

    private fun loadMentors() {
        progressBar.visibility = View.VISIBLE
        emptyState.visibility = View.GONE
        recyclerView.visibility = View.GONE
        
        Log.d(TAG, "Loading mentors...")
        
        ApiClient.instance.getMentorList().enqueue(object : Callback<MentorListResponse> {
            override fun onResponse(call: Call<MentorListResponse>, response: Response<MentorListResponse>) {
                progressBar.visibility = View.GONE
                
                Log.d(TAG, "Response code: ${response.code()}")
                
                try {
                    val body = response.body()
                    Log.d(TAG, "Response body: status=${body?.status}, requests count=${body?.requests?.size}")
                    
                    if (response.isSuccessful && body != null && body.status) {
                        allRequests = body.requests ?: emptyList()
                        Log.d(TAG, "Loaded ${allRequests.size} mentor requests")
                        
                        if (allRequests.isNotEmpty()) {
                            allRequests.forEach { 
                                Log.d(TAG, "Mentor: ${it.name}, Status: ${it.status}, Roll: ${it.roll_no}")
                            }
                        }
                        
                        updateStats()
                        filterByStatus(tabLayout.selectedTabPosition)
                        
                        if (allRequests.isNotEmpty()) {
                            recyclerView.visibility = View.VISIBLE
                        }
                    } else {
                        Log.e(TAG, "Response not successful or body null")
                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "Error body: $errorBody")
                        Toast.makeText(this@ManageMentorshipActivity, "No mentorship data available", Toast.LENGTH_SHORT).show()
                        allRequests = emptyList()
                        updateStats()
                        emptyState.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing data: ${e.message}", e)
                    Toast.makeText(this@ManageMentorshipActivity, "Error parsing data: ${e.message}", Toast.LENGTH_SHORT).show()
                    emptyState.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<MentorListResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.e(TAG, "Network error: ${t.message}", t)
                Toast.makeText(this@ManageMentorshipActivity, "Network Error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                emptyState.visibility = View.VISIBLE
            }
        })
    }

    override fun onUpdateStatus(rollNo: String, status: String) {
        Log.d(TAG, "Updating mentor status: rollNo=$rollNo, status=$status")
        progressBar.visibility = View.VISIBLE
        
        ApiClient.instance.updateMentorStatus(rollNo, status).enqueue(object : Callback<UpdateStatusResponse> {
            override fun onResponse(call: Call<UpdateStatusResponse>, response: Response<UpdateStatusResponse>) {
                progressBar.visibility = View.GONE
                val body = response.body()
                Log.d(TAG, "Update response: ${body?.status}, ${body?.message}")
                
                if (response.isSuccessful && body != null && body.status) {
                    Toast.makeText(this@ManageMentorshipActivity, body.message, Toast.LENGTH_SHORT).show()
                    loadMentors()
                } else {
                    Toast.makeText(this@ManageMentorshipActivity, "Update Failed", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<UpdateStatusResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.e(TAG, "Update error: ${t.message}")
                Toast.makeText(this@ManageMentorshipActivity, "Network Error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }
}