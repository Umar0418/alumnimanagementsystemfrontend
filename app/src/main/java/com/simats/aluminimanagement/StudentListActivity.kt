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

class StudentListActivity : AppCompatActivity() {

    private val TAG = "StudentListActivity"
    private lateinit var recyclerStudents: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var tvCount: TextView
    private lateinit var etSearch: EditText
    private var allStudents: List<Student> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)

        recyclerStudents = findViewById(R.id.recyclerStudents)
        emptyState = findViewById(R.id.emptyState)
        tvCount = findViewById(R.id.tvCount)
        etSearch = findViewById(R.id.etSearch)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        recyclerStudents.layoutManager = LinearLayoutManager(this)

        // Search filter
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterStudents(s.toString())
            }
        })

        loadStudents()
    }

    override fun onResume() {
        super.onResume()
        loadStudents()
    }

    private fun loadStudents() {
        Log.d(TAG, "Loading students...")
        ApiClient.instance.getStudents().enqueue(object : Callback<StudentListResponse> {
            override fun onResponse(call: Call<StudentListResponse>, response: Response<StudentListResponse>) {
                Log.d(TAG, "Response code: ${response.code()}")
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Log.d(TAG, "Status: ${body.status}, Students count: ${body.students?.size ?: 0}")
                    allStudents = body.students ?: listOf()
                    tvCount.text = allStudents.size.toString()
                    updateList(allStudents)
                } else {
                    Log.e(TAG, "Error response: ${response.errorBody()?.string()}")
                    emptyState.visibility = View.VISIBLE
                    recyclerStudents.visibility = View.GONE
                    Toast.makeText(this@StudentListActivity, "Failed to load students", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StudentListResponse>, t: Throwable) {
                Log.e(TAG, "Network error: ${t.message}")
                emptyState.visibility = View.VISIBLE
                recyclerStudents.visibility = View.GONE
                Toast.makeText(this@StudentListActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterStudents(query: String) {
        val filtered = if (query.isEmpty()) {
            allStudents
        } else {
            allStudents.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.rollNo.contains(query, ignoreCase = true) ||
                it.department?.contains(query, ignoreCase = true) == true
            }
        }
        updateList(filtered)
    }

    private fun updateList(list: List<Student>) {
        if (list.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            recyclerStudents.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            recyclerStudents.visibility = View.VISIBLE
            recyclerStudents.adapter = StudentAdapter(list)
        }
    }
}