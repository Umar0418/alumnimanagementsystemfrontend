package com.simats.aluminimanagement

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlumniListActivity : AppCompatActivity() {

    private val TAG = "AlumniListActivity"
    private lateinit var recyclerAlumni: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var tvCount: TextView
    private lateinit var etSearch: EditText
    private var allAlumni: List<Alumni> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumni_list)

        recyclerAlumni = findViewById(R.id.recyclerAlumni)
        emptyState = findViewById(R.id.emptyState)
        tvCount = findViewById(R.id.tvCount)
        etSearch = findViewById(R.id.etSearch)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        recyclerAlumni.layoutManager = LinearLayoutManager(this)

        // Search filter
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterAlumni(s.toString())
            }
        })

        loadAlumni()
    }

    private fun loadAlumni() {
        Log.d(TAG, "Loading alumni...")
        ApiClient.instance.getAlumni().enqueue(object : Callback<AlumniListResponse> {
            override fun onResponse(call: Call<AlumniListResponse>, response: Response<AlumniListResponse>) {
                Log.d(TAG, "Response code: ${response.code()}")
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Log.d(TAG, "Status: ${body.status}, Alumni count: ${body.alumni?.size ?: 0}")
                    allAlumni = body.alumni ?: listOf()
                    tvCount.text = allAlumni.size.toString()
                    updateList(allAlumni)
                } else {
                    Log.e(TAG, "Error response: ${response.errorBody()?.string()}")
                    emptyState.visibility = View.VISIBLE
                    recyclerAlumni.visibility = View.GONE
                    Toast.makeText(this@AlumniListActivity, "Failed to load alumni", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AlumniListResponse>, t: Throwable) {
                Log.e(TAG, "Network error: ${t.message}")
                emptyState.visibility = View.VISIBLE
                recyclerAlumni.visibility = View.GONE
                Toast.makeText(this@AlumniListActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterAlumni(query: String) {
        val filtered = if (query.isEmpty()) {
            allAlumni
        } else {
            allAlumni.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.company?.contains(query, ignoreCase = true) == true ||
                it.department?.contains(query, ignoreCase = true) == true
            }
        }
        updateList(filtered)
    }

    private fun updateList(list: List<Alumni>) {
        if (list.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            recyclerAlumni.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            recyclerAlumni.visibility = View.VISIBLE
            recyclerAlumni.adapter = AlumniAdapter(list)
        }
    }
}