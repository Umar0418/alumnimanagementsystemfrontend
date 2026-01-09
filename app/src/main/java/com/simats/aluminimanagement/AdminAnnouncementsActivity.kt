package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminAnnouncementsActivity : AppCompatActivity(), AnnouncementClickListener {

    private lateinit var recyclerAnnouncements: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_announcements)

        recyclerAnnouncements = findViewById(R.id.recyclerAnnouncements)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        recyclerAnnouncements.layoutManager = LinearLayoutManager(this)

        // Back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }

        val fab = findViewById<FloatingActionButton>(R.id.fabAddAnnouncement)
        fab.setOnClickListener {
            startActivity(Intent(this, SendAnnouncementActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadAnnouncements()
    }

    private fun loadAnnouncements() {
        android.util.Log.d("AdminAnnouncements", "Loading announcements...")
        ApiClient.instance.getAnnouncements(null).enqueue(object : Callback<AnnouncementListResponse> {
            override fun onResponse(call: Call<AnnouncementListResponse>, response: Response<AnnouncementListResponse>) {
                android.util.Log.d("AdminAnnouncements", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    val body = response.body()
                    android.util.Log.d("AdminAnnouncements", "Status: ${body?.status}, Count: ${body?.announcements?.size}")
                    if (body?.status == true) {
                        val announcements = body.announcements ?: emptyList()
                        if (announcements.isEmpty()) {
                            recyclerAnnouncements.visibility = View.GONE
                            emptyStateLayout.visibility = View.VISIBLE
                        } else {
                            recyclerAnnouncements.visibility = View.VISIBLE
                            emptyStateLayout.visibility = View.GONE
                            recyclerAnnouncements.adapter = AnnouncementAdapter(announcements, this@AdminAnnouncementsActivity)
                        }
                    } else {
                        android.util.Log.e("AdminAnnouncements", "Status false")
                        Toast.makeText(this@AdminAnnouncementsActivity, "Failed to load announcements", Toast.LENGTH_SHORT).show()
                        recyclerAnnouncements.visibility = View.GONE
                        emptyStateLayout.visibility = View.VISIBLE
                    }
                } else {
                    android.util.Log.e("AdminAnnouncements", "Error response: ${response.errorBody()?.string()}")
                    Toast.makeText(this@AdminAnnouncementsActivity, "Failed to load announcements", Toast.LENGTH_SHORT).show()
                    recyclerAnnouncements.visibility = View.GONE
                    emptyStateLayout.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<AnnouncementListResponse>, t: Throwable) {
                android.util.Log.e("AdminAnnouncements", "Network Error: ${t.message}", t)
                Toast.makeText(this@AdminAnnouncementsActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                recyclerAnnouncements.visibility = View.GONE
                emptyStateLayout.visibility = View.VISIBLE
            }
        })
    }

    override fun onEditAnnouncementClicked(announcement: AnnouncementModel) {
        // TODO: Implement edit functionality
        Toast.makeText(this, "Edit coming soon", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteAnnouncementClicked(announcement: AnnouncementModel) {
        // TODO: Implement delete functionality
        Toast.makeText(this, "Delete coming soon", Toast.LENGTH_SHORT).show()
    }
}