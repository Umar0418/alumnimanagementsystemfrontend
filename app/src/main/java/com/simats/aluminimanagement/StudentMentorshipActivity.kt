package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentMentorshipActivity : AppCompatActivity(), StudentMentorClickListener {

    private lateinit var recyclerMentors: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var etSearch: EditText
    private lateinit var adapter: StudentMentorAdapter
    private var allMentors: List<MentorModel> = emptyList()
    private var studentRollNo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_mentorship)

        val sessionManager = SessionManager.getInstance(this)
        studentRollNo = sessionManager.getRollNo()

        recyclerMentors = findViewById(R.id.recyclerMentors)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)
        etSearch = findViewById(R.id.etSearch)

        recyclerMentors.layoutManager = LinearLayoutManager(this)
        adapter = StudentMentorAdapter(emptyList(), this)
        recyclerMentors.adapter = adapter

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        findViewById<TextView>(R.id.tvMyRequests).setOnClickListener {
            startActivity(Intent(this, MyMentorRequestsActivity::class.java))
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        setupCategoryChips()
        loadMentors()
    }

    private fun setupCategoryChips() {
        val chips = listOf(
            findViewById<TextView>(R.id.chipAll) to "All",
            findViewById<TextView>(R.id.chipEngineering) to "Engineering",
            findViewById<TextView>(R.id.chipDesign) to "Design",
            findViewById<TextView>(R.id.chipProduct) to "Product",
            findViewById<TextView>(R.id.chipData) to "Data"
        )

        chips.forEach { (chip, category) ->
            chip.setOnClickListener {
                chips.forEach { (c, _) ->
                    c.setBackgroundResource(R.drawable.chip_unselected_bg)
                    c.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
                }
                chip.setBackgroundResource(R.drawable.chip_selected_bg)
                chip.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                adapter.filterByCategory(category)
            }
        }
    }

    private fun loadMentors() {
        progressBar.visibility = View.VISIBLE
        recyclerMentors.visibility = View.GONE
        emptyState.visibility = View.GONE

        android.util.Log.d("MentorLoad", "Loading available mentors...")
        
        // Use cached data for instant loading
        DataCache.getMentors({ mentors ->
            runOnUiThread {
                progressBar.visibility = View.GONE
                allMentors = mentors
                
                android.util.Log.d("MentorLoad", "Found ${allMentors.size} mentors")

                if (allMentors.isEmpty()) {
                    emptyState.visibility = View.VISIBLE
                    recyclerMentors.visibility = View.GONE
                } else {
                    emptyState.visibility = View.GONE
                    recyclerMentors.visibility = View.VISIBLE
                    adapter.updateList(allMentors)
                }
            }
        })
    }

    override fun onRequestMentorship(mentor: MentorModel) {
        val rollNo = studentRollNo
        if (rollNo == null) {
            Toast.makeText(this, "Please log in to request mentorship", Toast.LENGTH_SHORT).show()
            return
        }

        // Navigate to RequestMentorshipActivity
        val intent = Intent(this, RequestMentorshipActivity::class.java)
        intent.putExtra("mentor_roll_no", mentor.roll_no)
        intent.putExtra("mentor_name", mentor.name)
        intent.putExtra("mentor_field", mentor.mentorship_field ?: "General")
        intent.putExtra("mentor_style", mentor.mentorship_style ?: "")
        intent.putExtra("mentor_hours", mentor.working_hours ?: "")
        startActivity(intent)
    }

    private fun sendMentorshipRequest(studentRollNo: String, mentorRollNo: String, topic: String) {
        android.util.Log.d("MentorRequest", "Sending request: student=$studentRollNo, mentor=$mentorRollNo, topic=$topic")
        
        ApiClient.instance.requestMentorship(studentRollNo, mentorRollNo, topic).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                android.util.Log.d("MentorRequest", "Response code: ${response.code()}")
                
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    android.util.Log.d("MentorRequest", "Body: status=${body.status}, message=${body.message}")
                    
                    if (body.status) {
                        Toast.makeText(this@StudentMentorshipActivity, body.message, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@StudentMentorshipActivity, body.message ?: "Request failed", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("MentorRequest", "Error body: $errorBody")
                    Toast.makeText(this@StudentMentorshipActivity, "Server error: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                android.util.Log.e("MentorRequest", "Network failure: ${t.message}", t)
                Toast.makeText(this@StudentMentorshipActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}