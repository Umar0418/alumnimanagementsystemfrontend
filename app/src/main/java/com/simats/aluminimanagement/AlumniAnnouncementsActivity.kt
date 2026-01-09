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

class AlumniAnnouncementsActivity : AppCompatActivity() {

    private lateinit var recyclerAnnouncements: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumni_announcements)

        // CORRECTED: Changed the ID to match the one in the layout file
        recyclerAnnouncements = findViewById(R.id.recyclerAlumniAnnouncements)
        recyclerAnnouncements.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        loadAnnouncements()
    }

    private fun loadAnnouncements() {
        ApiClient.instance.getAnnouncements("alumni").enqueue(object : Callback<AnnouncementListResponse> {
            override fun onResponse(call: Call<AnnouncementListResponse>, response: Response<AnnouncementListResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val announcements = response.body()?.announcements ?: emptyList()
                    if (announcements.isNotEmpty()) {
                        recyclerAnnouncements.adapter = AnnouncementAdapter(announcements, null)
                    } else {
                        Toast.makeText(this@AlumniAnnouncementsActivity, "No announcements at this time.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AlumniAnnouncementsActivity, "Failed to load announcements", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AnnouncementListResponse>, t: Throwable) {
                Toast.makeText(this@AlumniAnnouncementsActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}