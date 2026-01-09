package com.simats.aluminimanagement

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyEventsActivity : AppCompatActivity() {

    private lateinit var recyclerMyEvents: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_events)

        recyclerMyEvents = findViewById(R.id.recyclerMyEvents)
        recyclerMyEvents.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        loadMyRegisteredEvents()
    }

    private fun loadMyRegisteredEvents() {
        val rollNo = SessionManager.getInstance(this).getRollNo() ?: return

        ApiClient.instance.getMyRegisteredEvents(rollNo).enqueue(object : Callback<MyEventsResponse> {
            override fun onResponse(call: Call<MyEventsResponse>, response: Response<MyEventsResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val myEvents = response.body()?.registeredEvents ?: emptyList()
                    if (myEvents.isNotEmpty()) {
                        recyclerMyEvents.adapter = MyEventsAdapter(myEvents)
                    } else {
                        Toast.makeText(this@MyEventsActivity, "You haven\'t registered for any events yet.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@MyEventsActivity, "Failed to load your events", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<MyEventsResponse>, t: Throwable) {
                Toast.makeText(this@MyEventsActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}