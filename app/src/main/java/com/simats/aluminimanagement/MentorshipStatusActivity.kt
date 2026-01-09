package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MentorshipStatusActivity : AppCompatActivity() {

    private val TAG = "MentorshipStatus"
    
    private lateinit var progressBar: ProgressBar
    private lateinit var statusView: CardView
    private lateinit var applyView: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mentorship_status)

        progressBar = findViewById(R.id.progressBarStatus)
        statusView = findViewById(R.id.cardStatusView)
        applyView = findViewById(R.id.llApplyView)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnApplyNow).setOnClickListener {
            try {
                startActivity(Intent(this, ApplyMentorshipActivity::class.java))
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "Error opening apply activity: ${e.message}")
                Toast.makeText(this, "Unable to open apply screen", Toast.LENGTH_SHORT).show()
            }
        }

        loadMentorStatus()
    }

    override fun onResume() {
        super.onResume()
        loadMentorStatus()
    }

    private fun loadMentorStatus() {
        val sessionManager = SessionManager.getInstance(this)
        val rollNo = sessionManager.getRollNo()

        Log.d(TAG, "Checking mentor status for roll_no: $rollNo")

        if (rollNo == null) {
            progressBar.visibility = View.GONE
            applyView.visibility = View.VISIBLE
            Toast.makeText(this, "Error: Could not retrieve user session.", Toast.LENGTH_LONG).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        statusView.visibility = View.GONE
        applyView.visibility = View.GONE

        try {
            ApiClient.instance.getMyMentorStatus(rollNo).enqueue(object : Callback<MyMentorStatusResponse> {
                override fun onResponse(call: Call<MyMentorStatusResponse>, response: Response<MyMentorStatusResponse>) {
                    try {
                        progressBar.visibility = View.GONE
                        
                        Log.d(TAG, "Response code: ${response.code()}")
                        val body = response.body()
                        Log.d(TAG, "Response body: status=${body?.status}, data=${body?.data}")
                        
                        if (response.isSuccessful && body != null && body.status && body.data != null) {
                            showStatusView(body.data)
                        } else {
                            showApplyView()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing response: ${e.message}")
                        showApplyView()
                    }
                }

                override fun onFailure(call: Call<MyMentorStatusResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.e(TAG, "Network error: ${t.message}")
                    Toast.makeText(this@MentorshipStatusActivity, "Network Error", Toast.LENGTH_SHORT).show()
                    showApplyView()
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error making API call: ${e.message}")
            progressBar.visibility = View.GONE
            showApplyView()
        }
    }

    private fun showStatusView(data: MentorStatusData) {
        statusView.visibility = View.VISIBLE
        applyView.visibility = View.GONE
        
        val statusTitle = findViewById<TextView>(R.id.tvStatusTitle)
        val statusSubtitle = findViewById<TextView>(R.id.tvStatusSubtitle)
        val statusBadge = findViewById<TextView>(R.id.tvStatusBadge)
        val statusIcon = findViewById<ImageView>(R.id.ivStatusIcon)
        val cardDetails = findViewById<CardView>(R.id.cardDetails)
        
        // Detail fields
        findViewById<TextView>(R.id.tvFieldValue)?.text = data.field ?: "-"
        findViewById<TextView>(R.id.tvHoursValue)?.text = data.workingHours ?: "-"
        findViewById<TextView>(R.id.tvStyleValue)?.text = data.mentorshipStyle ?: "-"

        val mentorStatus = data.status?.lowercase() ?: ""
        Log.d(TAG, "Mentor status: $mentorStatus")

        when(mentorStatus) {
            "pending" -> {
                statusTitle.text = "Application Submitted"
                statusSubtitle.text = "Your mentorship application is being reviewed by the admin. You'll be notified once it's processed."
                statusBadge.text = "PENDING"
                statusBadge.backgroundTintList = ContextCompat.getColorStateList(this, R.color.warning)
                statusIcon.backgroundTintList = ContextCompat.getColorStateList(this, R.color.warning)
                cardDetails?.visibility = View.VISIBLE
            }
            "approved" -> {
                statusTitle.text = "You're a Mentor!"
                statusSubtitle.text = "Congratulations! Your application has been approved. Students can now request mentorship from you."
                statusBadge.text = "APPROVED"
                statusBadge.backgroundTintList = ContextCompat.getColorStateList(this, R.color.success)
                statusIcon.backgroundTintList = ContextCompat.getColorStateList(this, R.color.success)
                cardDetails?.visibility = View.VISIBLE
            }
            "rejected" -> {
                statusTitle.text = "Application Not Approved"
                statusSubtitle.text = "Unfortunately, your application was not approved at this time. You can apply again with updated information."
                statusBadge.text = "REJECTED"
                statusBadge.backgroundTintList = ContextCompat.getColorStateList(this, R.color.error)
                statusIcon.backgroundTintList = ContextCompat.getColorStateList(this, R.color.error)
                cardDetails?.visibility = View.VISIBLE
                
                // Show apply again button
                applyView.visibility = View.VISIBLE
                findViewById<TextView>(R.id.tvApplyTitle)?.text = "Want to try again?"
                findViewById<TextView>(R.id.tvApplySubtitle)?.text = "You can reapply for mentorship with updated information."
                findViewById<Button>(R.id.btnApplyNow)?.text = "Apply Again"
            }
            else -> {
                statusTitle.text = "Application Status"
                statusSubtitle.text = "Your application status: ${data.status}"
                statusBadge.text = data.status?.uppercase() ?: "UNKNOWN"
                cardDetails?.visibility = View.VISIBLE
            }
        }
        
        // Animate card entrance
        try {
            val fadeInScale = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale)
            statusView.startAnimation(fadeInScale)
        } catch (e: Exception) {
            Log.e(TAG, "Animation error: ${e.message}")
        }
    }

    private fun showApplyView() {
        statusView.visibility = View.GONE
        applyView.visibility = View.VISIBLE
        
        findViewById<TextView>(R.id.tvApplyTitle)?.text = "Become a Mentor"
        findViewById<TextView>(R.id.tvApplySubtitle)?.text = "Share your experience and guide students on their career path."
        findViewById<Button>(R.id.btnApplyNow)?.text = "Apply for Mentorship"
        
        // Animate apply view
        try {
            val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
            applyView.startAnimation(slideUp)
        } catch (e: Exception) {
            Log.e(TAG, "Animation error: ${e.message}")
        }
    }
}
