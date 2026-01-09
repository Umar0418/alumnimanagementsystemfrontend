package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDashboardActivity : AppCompatActivity() {

    private val TAG = "AdminDashboard"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)
        setupNavigation()
        animateEntrance()
    }

    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }

    private fun animateEntrance() {
        val statsGrid = findViewById<GridLayout>(R.id.statsGrid)
        val fadeInScale = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale)

        statsGrid.alpha = 0f
        statsGrid.postDelayed({
            statsGrid.alpha = 1f
            statsGrid.startAnimation(fadeInScale)
        }, 150)

        val cards = listOf(
            findViewById<CardView>(R.id.cardAlumni),
            findViewById<CardView>(R.id.cardStudents),
            findViewById<CardView>(R.id.cardEvents),
            findViewById<CardView>(R.id.cardJobs)
        )

        cards.forEachIndexed { index, card ->
            card.alpha = 0f
            card.postDelayed({
                card.alpha = 1f
                card.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_scale))
            }, (200 + index * 100).toLong())
        }
    }

    private fun setupNavigation() {
        // Stat cards navigation
        findViewById<CardView>(R.id.cardAlumni).setOnClickListener {
            startActivity(Intent(this, AlumniListActivity::class.java))
        }
        findViewById<CardView>(R.id.cardStudents).setOnClickListener {
            startActivity(Intent(this, StudentListActivity::class.java))
        }
        findViewById<CardView>(R.id.cardEvents).setOnClickListener {
            startActivity(Intent(this, AdminEventsActivity::class.java))
        }
        findViewById<CardView>(R.id.cardJobs).setOnClickListener {
            startActivity(Intent(this, AdminJobsActivity::class.java))
        }

        // Management tools
        findViewById<TextView>(R.id.toolMentorships).setOnClickListener {
            startActivity(Intent(this, ManageMentorshipActivity::class.java))
        }
        findViewById<TextView>(R.id.toolDonations).setOnClickListener {
            startActivity(Intent(this, AdminFundsActivity::class.java))
        }
        findViewById<TextView>(R.id.toolAnnouncements).setOnClickListener {
            startActivity(Intent(this, AdminAnnouncementsActivity::class.java))
        }

        // Logout
        findViewById<ImageView>(R.id.ivLogout).setOnClickListener {
            SessionManager.getInstance(this).clear()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadDashboardData() {
        // Load all counts using cache for instant display
        loadCount(ApiClient.instance.getAlumniCount(), findViewById(R.id.tvAlumniCount), "Alumni")
        loadCount(ApiClient.instance.getStudentCount(), findViewById(R.id.tvStudentCount), "Student")
        
        // Use cached data for events and jobs counts
        DataCache.getEvents({ events ->
            runOnUiThread {
                findViewById<TextView>(R.id.tvEventsCount).text = events.size.toString()
            }
        })
        
        DataCache.getJobs({ jobs ->
            runOnUiThread {
                findViewById<TextView>(R.id.tvJobsCount).text = jobs.size.toString()
            }
        })

        // Load recent activity
        loadRecentActivity()
    }

    private fun loadCount(call: Call<CountResponse>, textView: TextView, label: String) {
        call.enqueue(object : Callback<CountResponse> {
            override fun onResponse(call: Call<CountResponse>, response: Response<CountResponse>) {
                val body = response.body()
                Log.d(TAG, "$label count response: code=${response.code()}, body=$body")
                if (response.isSuccessful && body != null && body.status) {
                    textView.text = body.count.toString()
                    Log.d(TAG, "$label count loaded: ${body.count}")
                } else {
                    Log.e(TAG, "$label count failed: code=${response.code()}, body=${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<CountResponse>, t: Throwable) {
                Log.e(TAG, "$label count error: ${t.message}")
            }
        })
    }

    private fun loadRecentActivity() {
        val recyclerRecentActivity = findViewById<RecyclerView>(R.id.recyclerRecentActivity)
        recyclerRecentActivity.layoutManager = LinearLayoutManager(this)
        
        ApiClient.instance.getRecentActivity().enqueue(object : Callback<List<RecentActivity>> {
            override fun onResponse(call: Call<List<RecentActivity>>, response: Response<List<RecentActivity>>) {
                Log.d(TAG, "Recent activity response: ${response.code()}")
                if (response.isSuccessful) {
                    response.body()?.let { activities ->
                        Log.d(TAG, "Recent activity loaded: ${activities.size} items")
                        if (activities.isNotEmpty()) {
                            recyclerRecentActivity.adapter = RecentActivityAdapter(activities)
                        }
                    }
                } else {
                    Log.e(TAG, "Recent activity failed: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<RecentActivity>>, t: Throwable) {
                Log.e(TAG, "Recent activity error: ${t.message}")
            }
        })
    }
}
