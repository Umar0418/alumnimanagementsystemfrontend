package com.simats.aluminimanagement

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlumniEventsActivity : AppCompatActivity() {

    private lateinit var recyclerEvents: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumni_events)

        recyclerEvents = findViewById(R.id.recyclerAlumniEvents)
        recyclerEvents.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        setupBottomNavigation()
        loadAllEvents()
    }
    
    private fun setupBottomNavigation() {
        val sessionManager = SessionManager.getInstance(this)
        val userType = sessionManager.getUserType()
        
        try {
            val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
            bottomNav?.let { nav ->
                nav.selectedItemId = R.id.nav_events
                
                nav.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.nav_home -> {
                            if (userType == "alumni") {
                                startActivity(android.content.Intent(this, AlumniDashboardActivity::class.java))
                            } else {
                                startActivity(android.content.Intent(this, StudentDashboardActivity::class.java))
                            }
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finish()
                            true
                        }
                        R.id.nav_alumni -> {
                            startActivity(android.content.Intent(this, AlumniDirectoryActivity::class.java))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            true
                        }
                        R.id.nav_jobs -> {
                            if (userType == "alumni") {
                                startActivity(android.content.Intent(this, AlumniJobsActivity::class.java))
                            } else {
                                startActivity(android.content.Intent(this, StudentJobsActivity::class.java))
                            }
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            true
                        }
                        R.id.nav_events -> true
                        R.id.nav_profile -> {
                            if (userType == "alumni") {
                                startActivity(android.content.Intent(this, AlumniProfileActivity::class.java))
                            } else {
                                startActivity(android.content.Intent(this, StudentProfileActivity::class.java))
                            }
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            true
                        }
                        else -> false
                    }
                }
            }
        } catch (e: Exception) {
            // Bottom nav doesn't exist
        }
    }

    private fun loadAllEvents() {
        // Use cached data for instant loading
        DataCache.getEvents({ events ->
            runOnUiThread {
                fetchRegisteredEventsAndDisplay(events)
            }
        })
    }

    private fun fetchRegisteredEventsAndDisplay(allEvents: List<EventModel>) {
        val sessionManager = SessionManager.getInstance(this)
        val rollNo = sessionManager.getRollNo()

        if (rollNo == null) {
            // If user isn't logged in, just show all events with "Register Now"
            recyclerEvents.adapter = AlumniEventAdapter(allEvents, emptyList())
            return
        }

        // If logged in, get their registered event IDs
        ApiClient.instance.getMyRegisteredEvents(rollNo).enqueue(object : Callback<MyEventsResponse> {
            override fun onResponse(call: Call<MyEventsResponse>, response: Response<MyEventsResponse>) {
                val registeredIds = if (response.isSuccessful && response.body()?.status == true) {
                    response.body()?.registeredEvents?.map { it.eventId } ?: emptyList()
                } else {
                    emptyList()
                }
                // Finally, create the adapter with both lists
                recyclerEvents.adapter = AlumniEventAdapter(allEvents, registeredIds)
            }

            override fun onFailure(call: Call<MyEventsResponse>, t: Throwable) {
                // If this fails, still show the events, just without the "Registered" status
                recyclerEvents.adapter = AlumniEventAdapter(allEvents, emptyList())
            }
        })
    }
}
