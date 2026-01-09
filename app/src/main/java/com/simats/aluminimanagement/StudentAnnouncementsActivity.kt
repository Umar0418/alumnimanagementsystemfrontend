package com.simats.aluminimanagement

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentAnnouncementsActivity : AppCompatActivity() {

    private lateinit var recyclerAnnouncements: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_announcements)

        recyclerAnnouncements = findViewById(R.id.recyclerAnnouncements)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)

        recyclerAnnouncements.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        loadAnnouncements()
    }

    private fun loadAnnouncements() {
        progressBar.visibility = View.VISIBLE
        recyclerAnnouncements.visibility = View.GONE
        emptyState.visibility = View.GONE

        // Load announcements for students (target = 'students' or 'all')
        ApiClient.instance.getAnnouncements("students").enqueue(object : Callback<AnnouncementListResponse> {
            override fun onResponse(call: Call<AnnouncementListResponse>, response: Response<AnnouncementListResponse>) {
                progressBar.visibility = View.GONE
                
                if (response.isSuccessful && response.body()?.status == true) {
                    val announcements = response.body()?.announcements ?: emptyList()
                    if (announcements.isNotEmpty()) {
                        recyclerAnnouncements.visibility = View.VISIBLE
                        recyclerAnnouncements.adapter = AnnouncementAdapter(announcements, null)
                    } else {
                        emptyState.visibility = View.VISIBLE
                    }
                } else {
                    emptyState.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<AnnouncementListResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                emptyState.visibility = View.VISIBLE
            }
        })
    }
}
