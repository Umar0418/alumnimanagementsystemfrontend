package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlumniDashboardActivity : AppCompatActivity() {

    private val TAG = "AlumniDashboard"
    private lateinit var recyclerUpcomingEvents: RecyclerView
    private lateinit var btnMentorship: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumni_dashboard)

        recyclerUpcomingEvents = findViewById(R.id.recyclerUpcomingEvents)
        recyclerUpcomingEvents.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        
        btnMentorship = findViewById(R.id.btnMentorship)

        setupWelcomeMessage()
        setupNavigation()
        animateEntrance()
    }

    override fun onResume() {
        super.onResume()
        loadUpcomingEvents()
        checkMentorStatus()
        loadDashboardCounts()
    }

    private fun loadDashboardCounts() {
        val sessionManager = SessionManager.getInstance(this)
        val email = sessionManager.getUser()?.email ?: return

        // Load mentees count
        loadMenteesCount(email)
        
        // Load job referrals count
        loadJobReferralsCount(email)
    }

    private fun loadMenteesCount(email: String) {
        try {
            ApiClient.instance.getMenteeCount(email).enqueue(object : Callback<CountResponse> {
                override fun onResponse(call: Call<CountResponse>, response: Response<CountResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        val count = response.body()?.count ?: 0
                        findViewById<TextView>(R.id.tvMenteesCount)?.text = count.toString()
                    }
                }
                override fun onFailure(call: Call<CountResponse>, t: Throwable) {
                    Log.e(TAG, "Error loading mentees count: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Exception loading mentees count: ${e.message}")
        }
    }

    private fun loadJobReferralsCount(email: String) {
        try {
            ApiClient.instance.getJobReferralsCount(email).enqueue(object : Callback<CountResponse> {
                override fun onResponse(call: Call<CountResponse>, response: Response<CountResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        val count = response.body()?.count ?: 0
                        findViewById<TextView>(R.id.tvReferralsCount)?.text = count.toString()
                    }
                }
                override fun onFailure(call: Call<CountResponse>, t: Throwable) {
                    Log.e(TAG, "Error loading job referrals count: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Exception loading job referrals count: ${e.message}")
        }
    }

    private fun checkMentorStatus() {
        try {
            val sessionManager = SessionManager.getInstance(this)
            val rollNo = sessionManager.getRollNo() ?: return

            ApiClient.instance.getMyMentorStatus(rollNo).enqueue(object : Callback<MyMentorStatusResponse> {
                override fun onResponse(call: Call<MyMentorStatusResponse>, response: Response<MyMentorStatusResponse>) {
                    val body = response.body()
                    if (response.isSuccessful && body != null && body.status && body.data != null) {
                        // CORRECTED: Used a null-safe call on the 'status' property
                        when (body.data.status?.lowercase()) {
                            "pending" -> {
                                btnMentorship.text = "Applied ⏳"
                                btnMentorship.setTextColor(ContextCompat.getColor(this@AlumniDashboardActivity, R.color.warning))
                            }
                            "approved" -> {
                                btnMentorship.text = "Mentor ✓"
                                btnMentorship.setTextColor(ContextCompat.getColor(this@AlumniDashboardActivity, R.color.success))
                            }
                            "rejected" -> {
                                btnMentorship.text = "Apply Again"
                                btnMentorship.setTextColor(ContextCompat.getColor(this@AlumniDashboardActivity, R.color.white))
                            }
                        }
                    } else {
                        btnMentorship.text = "Be a Mentor"
                        btnMentorship.setTextColor(ContextCompat.getColor(this@AlumniDashboardActivity, R.color.white))
                    }
                }

                override fun onFailure(call: Call<MyMentorStatusResponse>, t: Throwable) {
                    Log.e(TAG, "Error checking mentor status: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Exception in checkMentorStatus: ${e.message}")
        }
    }

    private fun animateEntrance() {
        try {
            val cardWelcome = findViewById<CardView>(R.id.cardWelcome)
            val fadeInScale = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale)
            val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

            cardWelcome.alpha = 0f
            cardWelcome.postDelayed({
                cardWelcome.alpha = 1f
                cardWelcome.startAnimation(fadeInScale)
            }, 100)

            // Animate quick action cards
            val cards = listOf(
                findViewById<CardView>(R.id.cardPostJob),
                findViewById<CardView>(R.id.cardEvents),
                findViewById<CardView>(R.id.cardCommunity),
                findViewById<CardView>(R.id.cardDirectory),
                findViewById<CardView>(R.id.cardMyEvents),
                findViewById<CardView>(R.id.cardAnnouncements)
            )
            
            cards.forEachIndexed { index, card ->
                card?.let {
                    it.alpha = 0f
                    it.translationY = 50f
                    it.postDelayed({
                        it.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(300)
                            .start()
                    }, (200 + index * 80).toLong())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Animation error: ${e.message}")
        }
    }

    private fun setupWelcomeMessage() {
        try {
            val tvWelcomeMessage = findViewById<TextView>(R.id.tvWelcomeMessage)
            val tvUserEmail = findViewById<TextView>(R.id.tvUserEmail)
            val tvUserInitials = findViewById<TextView>(R.id.tvUserInitials)
            val sessionManager = SessionManager.getInstance(this)
            val user = sessionManager.getUser()
            val userName = user?.name?.split(" ")?.get(0) ?: "Alumni"
            tvWelcomeMessage?.text = "Welcome, ${userName.replaceFirstChar { it.uppercase() }}!"
            tvUserEmail?.text = user?.email ?: ""
            tvUserInitials?.text = (user?.name?.take(2)?.uppercase() ?: "AL")
        } catch (e: Exception) {
            Log.e(TAG, "Welcome message error: ${e.message}")
        }
    }

    private fun setupNavigation() {
        // Notification icon - navigate to announcements
        findViewById<android.widget.ImageView>(R.id.ivNotification)?.setOnClickListener {
            animateClickAndNavigate(it, Intent(this, AlumniAnnouncementsActivity::class.java))
        }

        btnMentorship.setOnClickListener { animateClickAndNavigate(it, Intent(this, MentorshipStatusActivity::class.java)) }

        findViewById<Button>(R.id.btnDonate).setOnClickListener { animateClickAndNavigate(it, Intent(this, DonateActivity::class.java)) }
        
        // Stat cards with animations
        findViewById<CardView>(R.id.cardMentees)?.setOnClickListener { animateClickAndNavigate(it, Intent(this, MentorshipDashboardActivity::class.java)) }
        findViewById<CardView>(R.id.cardReferrals)?.setOnClickListener { animateClickAndNavigate(it, Intent(this, AlumniJobsActivity::class.java)) }
        
        // Quick action cards with animations
        findViewById<CardView>(R.id.cardPostJob)?.setOnClickListener { animateClickAndNavigate(it, Intent(this, PostJobReferralActivity::class.java)) }
        findViewById<CardView>(R.id.cardEvents)?.setOnClickListener { animateClickAndNavigate(it, Intent(this, AlumniEventsActivity::class.java)) }
        findViewById<CardView>(R.id.cardCommunity)?.setOnClickListener { animateClickAndNavigate(it, Intent(this, CommunityGroupsActivity::class.java)) }
        findViewById<CardView>(R.id.cardDirectory)?.setOnClickListener { animateClickAndNavigate(it, Intent(this, AlumniDirectoryActivity::class.java)) }
        findViewById<CardView>(R.id.cardMyEvents)?.setOnClickListener { animateClickAndNavigate(it, Intent(this, MyEventsActivity::class.java)) }
        findViewById<CardView>(R.id.cardAnnouncements)?.setOnClickListener { animateClickAndNavigate(it, Intent(this, AlumniAnnouncementsActivity::class.java)) }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_directory -> { safeStartActivity(Intent(this, AlumniDirectoryActivity::class.java)); true }
                R.id.nav_jobs -> { safeStartActivity(Intent(this, AlumniJobsActivity::class.java)); true }
                R.id.nav_events -> { safeStartActivity(Intent(this, AlumniEventsActivity::class.java)); true }
                R.id.nav_profile -> { safeStartActivity(Intent(this, AlumniProfileActivity::class.java)); true }
                else -> false
            }
        }
    }
    
    private fun animateClickAndNavigate(view: View, intent: Intent) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .withEndAction {
                        safeStartActivity(intent)
                    }
                    .start()
            }
            .start()
    }

    private fun safeStartActivity(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start activity: ${e.message}")
            Toast.makeText(this, "Unable to open this screen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUpcomingEvents() {
        try {
            val sessionManager = SessionManager.getInstance(this)
            val rollNo = sessionManager.getRollNo() ?: ""

            // Use cached data for instant loading
            DataCache.getEvents({ events ->
                runOnUiThread {
                    val upcomingEvents = events.take(3)
                    
                    // If we have roll number, fetch registered events
                    if (rollNo.isNotEmpty()) {
                        ApiClient.instance.getMyRegisteredEvents(rollNo).enqueue(object : Callback<MyEventsResponse> {
                            override fun onResponse(call: Call<MyEventsResponse>, response: Response<MyEventsResponse>) {
                                val registeredIds = if (response.isSuccessful && response.body()?.status == true) {
                                    response.body()?.registeredEvents?.map { it.eventId } ?: emptyList()
                                } else {
                                    emptyList()
                                }
                                recyclerUpcomingEvents.adapter = AlumniEventAdapter(upcomingEvents, registeredIds)
                            }

                            override fun onFailure(call: Call<MyEventsResponse>, t: Throwable) {
                                Log.e(TAG, "Error loading registered events: ${t.message}")
                                recyclerUpcomingEvents.adapter = AlumniEventAdapter(upcomingEvents, emptyList())
                            }
                        })
                    } else {
                        recyclerUpcomingEvents.adapter = AlumniEventAdapter(upcomingEvents, emptyList())
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Exception loading events: ${e.message}")
        }
    }
}