package com.simats.aluminimanagement

import android.os.Bundle
import android.util.Log
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

class MyMentorRequestsActivity : AppCompatActivity() {

    private lateinit var recyclerRequests: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var adapter: MyRequestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_mentor_requests)

        recyclerRequests = findViewById(R.id.recyclerRequests)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)

        recyclerRequests.layoutManager = LinearLayoutManager(this)
        adapter = MyRequestsAdapter(emptyList())
        recyclerRequests.adapter = adapter

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        loadMyRequests()
    }

    private fun loadMyRequests() {
        val sessionManager = SessionManager.getInstance(this)
        val studentRollNo = sessionManager.getRollNo()

        if (studentRollNo == null) {
            Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        progressBar.visibility = View.VISIBLE
        recyclerRequests.visibility = View.GONE
        emptyState.visibility = View.GONE

        // First, load local requests
        val localRequests = LocalMentorRequestStorage.getRequests(this)
        
        // Try to get from server
        ApiClient.instance.getMyMentorRequests(studentRollNo)
            .enqueue(object : Callback<MyMentorRequestsResponse> {
                override fun onResponse(
                    call: Call<MyMentorRequestsResponse>,
                    response: Response<MyMentorRequestsResponse>
                ) {
                    progressBar.visibility = View.GONE

                    val serverRequests = if (response.isSuccessful && response.body()?.status == true) {
                        response.body()?.requests ?: emptyList()
                    } else {
                        emptyList()
                    }
                    
                    // Combine server and local requests, removing duplicates
                    val allRequests = combineRequests(serverRequests, localRequests)
                    displayRequests(allRequests)
                }

                override fun onFailure(call: Call<MyMentorRequestsResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.e("MyRequests", "Network error: ${t.message}")
                    
                    // Show local requests if network fails
                    displayRequests(localRequests)
                }
            })
    }
    
    private fun combineRequests(
        serverRequests: List<MyMentorRequest>, 
        localRequests: List<MyMentorRequest>
    ): List<MyMentorRequest> {
        // If server has data, prefer server data
        if (serverRequests.isNotEmpty()) {
            return serverRequests
        }
        // Otherwise show local requests
        return localRequests
    }
    
    private fun displayRequests(requests: List<MyMentorRequest>) {
        if (requests.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            recyclerRequests.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            recyclerRequests.visibility = View.VISIBLE
            adapter.updateList(requests)
        }
    }
}
