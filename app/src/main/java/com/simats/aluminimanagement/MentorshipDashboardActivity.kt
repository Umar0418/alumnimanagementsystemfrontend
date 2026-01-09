package com.simats.aluminimanagement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MentorshipDashboardActivity : AppCompatActivity(), MenteeActionListener {

    private val TAG = "MentorshipDashboard"
    private lateinit var recyclerMentees: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var cardMentorStatus: CardView
    private lateinit var llApplyView: LinearLayout
    private lateinit var llMenteesSection: LinearLayout
    private var mentorRollNo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mentorship_dashboard)

        recyclerMentees = findViewById(R.id.recyclerMentees)
        recyclerMentees.layoutManager = LinearLayoutManager(this)
        
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.llEmptyState)
        cardMentorStatus = findViewById(R.id.cardMentorStatus)
        llApplyView = findViewById(R.id.llApplyView)
        llMenteesSection = findViewById(R.id.llMenteesSection)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
        
        findViewById<Button>(R.id.btnApplyNow).setOnClickListener {
            startActivity(Intent(this, ApplyMentorshipActivity::class.java))
        }

        val sessionManager = SessionManager.getInstance(this)
        mentorRollNo = sessionManager.getRollNo()

        if (mentorRollNo == null) {
            Toast.makeText(this, "Error: Session not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        checkMentorStatusAndLoad()
    }

    override fun onResume() {
        super.onResume()
        checkMentorStatusAndLoad()
    }

    private fun checkMentorStatusAndLoad() {
        val rollNo = mentorRollNo ?: return
        
        progressBar.visibility = View.VISIBLE
        cardMentorStatus.visibility = View.GONE
        llApplyView.visibility = View.GONE
        llMenteesSection.visibility = View.GONE
        emptyState.visibility = View.GONE
        
        Log.d(TAG, "Checking mentor status for: $rollNo")
        
        ApiClient.instance.getMyMentorStatus(rollNo).enqueue(object : Callback<MyMentorStatusResponse> {
            override fun onResponse(call: Call<MyMentorStatusResponse>, response: Response<MyMentorStatusResponse>) {
                progressBar.visibility = View.GONE
                val body = response.body()
                Log.d(TAG, "Response: ${response.code()}, status: ${body?.status}, data: ${body?.data?.status}")
                
                if (response.isSuccessful && body != null && body.status && body.data != null) {
                    // CORRECTED: Used a null-safe call on the 'status' property
                    val mentorStatus = body.data.status?.lowercase()
                    Log.d(TAG, "Mentor status: $mentorStatus")
                    
                    when (mentorStatus) {
                        "approved" -> {
                            // Show mentor dashboard
                            showApprovedMentorView(body.data)
                            loadMentees()
                        }
                        "pending" -> {
                            // Show pending status
                            showPendingView()
                        }
                        "rejected" -> {
                            // Show rejected - can apply again
                            showRejectedView()
                        }
                        else -> {
                            showApplyView()
                        }
                    }
                } else {
                    // No mentor application found - show apply
                    Log.d(TAG, "No mentor status found, showing apply view")
                    showApplyView()
                }
            }

            override fun onFailure(call: Call<MyMentorStatusResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.e(TAG, "Network error: ${t.message}")
                showApplyView()
                Toast.makeText(this@MentorshipDashboardActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showApprovedMentorView(data: MentorStatusData) {
        cardMentorStatus.visibility = View.VISIBLE
        llMenteesSection.visibility = View.VISIBLE
        llApplyView.visibility = View.GONE
        
        findViewById<TextView>(R.id.tvApprovalDate).text = "Application approved"
        findViewById<TextView>(R.id.tvMentorBadge).apply {
            text = "Active\nMentor"
            backgroundTintList = ContextCompat.getColorStateList(this@MentorshipDashboardActivity, R.color.success)
        }
        findViewById<View>(R.id.statusAccent).setBackgroundColor(
            ContextCompat.getColor(this, R.color.success)
        )
    }

    private fun showPendingView() {
        cardMentorStatus.visibility = View.VISIBLE
        llMenteesSection.visibility = View.GONE
        llApplyView.visibility = View.GONE
        
        findViewById<TextView>(R.id.tvApprovalDate).text = "Waiting for admin approval"
        findViewById<TextView>(R.id.tvMentorBadge).apply {
            text = "Pending\nApproval"
            backgroundTintList = ContextCompat.getColorStateList(this@MentorshipDashboardActivity, R.color.warning)
        }
        findViewById<View>(R.id.statusAccent).setBackgroundColor(
            ContextCompat.getColor(this, R.color.warning)
        )
    }

    private fun showRejectedView() {
        cardMentorStatus.visibility = View.VISIBLE
        llMenteesSection.visibility = View.GONE
        llApplyView.visibility = View.VISIBLE
        
        findViewById<TextView>(R.id.tvApprovalDate).text = "Application was rejected"
        findViewById<TextView>(R.id.tvMentorBadge).apply {
            text = "Rejected"
            backgroundTintList = ContextCompat.getColorStateList(this@MentorshipDashboardActivity, R.color.error)
        }
        findViewById<View>(R.id.statusAccent).setBackgroundColor(
            ContextCompat.getColor(this, R.color.error)
        )
        
        findViewById<TextView>(R.id.tvApplyTitle).text = "Want to try again?"
        findViewById<TextView>(R.id.tvApplySubtitle).text = "You can reapply for mentorship with updated information."
        findViewById<Button>(R.id.btnApplyNow).text = "Apply Again"
    }

    private fun showApplyView() {
        cardMentorStatus.visibility = View.GONE
        llMenteesSection.visibility = View.GONE
        llApplyView.visibility = View.VISIBLE
        
        findViewById<TextView>(R.id.tvApplyTitle).text = "Become a Mentor"
        findViewById<TextView>(R.id.tvApplySubtitle).text = "Share your experience and guide students on their career path."
        findViewById<Button>(R.id.btnApplyNow).text = "Apply for Mentorship"
    }

    private fun loadMentees() {
        val rollNo = mentorRollNo ?: return
        
        progressBar.visibility = View.VISIBLE
        emptyState.visibility = View.GONE
        recyclerMentees.visibility = View.GONE

        ApiClient.instance.getMentees(rollNo).enqueue(object : Callback<MenteeListResponse> {
            override fun onResponse(call: Call<MenteeListResponse>, response: Response<MenteeListResponse>) {
                progressBar.visibility = View.GONE
                val body = response.body()
                if (response.isSuccessful && body != null && body.status) {
                    val menteeList = body.mentees ?: emptyList()
                    if (menteeList.isEmpty()) {
                        emptyState.visibility = View.VISIBLE
                        recyclerMentees.visibility = View.GONE
                    } else {
                        emptyState.visibility = View.GONE
                        recyclerMentees.visibility = View.VISIBLE
                        recyclerMentees.adapter = MenteeAdapter(menteeList, this@MentorshipDashboardActivity)
                        
                        try {
                            val slideUp = AnimationUtils.loadAnimation(this@MentorshipDashboardActivity, R.anim.slide_up)
                            recyclerMentees.startAnimation(slideUp)
                        } catch (e: Exception) {
                            Log.e(TAG, "Animation error: ${e.message}")
                        }
                    }
                } else {
                    emptyState.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<MenteeListResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                emptyState.visibility = View.VISIBLE
                Toast.makeText(this@MentorshipDashboardActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onAccept(mentee: MenteeModel) {
        updateMenteeStatus(mentee, "active")
    }

    override fun onDecline(mentee: MenteeModel) {
        updateMenteeStatus(mentee, "declined")
    }

    override fun onMessage(mentee: MenteeModel) {
        mentee.email?.let { email ->
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, "Mentorship: ${mentee.topic}")
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "No contact info available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMenteeStatus(mentee: MenteeModel, status: String) {
        ApiClient.instance.updateMenteeStatus(mentee.id.toString(), status).enqueue(object : Callback<UpdateMenteeStatusResponse> {
            override fun onResponse(call: Call<UpdateMenteeStatusResponse>, response: Response<UpdateMenteeStatusResponse>) {
                val body = response.body()
                if (response.isSuccessful && body != null && body.status) {
                    Toast.makeText(this@MentorshipDashboardActivity, body.message, Toast.LENGTH_SHORT).show()
                    loadMentees()
                } else {
                    Toast.makeText(this@MentorshipDashboardActivity, "Failed to update", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateMenteeStatusResponse>, t: Throwable) {
                Toast.makeText(this@MentorshipDashboardActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}