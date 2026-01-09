package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is already logged in
        val sessionManager = SessionManager.getInstance(this)
        if (sessionManager.isLoggedIn()) {
            val user = sessionManager.getUser()
            val targetActivity = when (user?.userType?.lowercase()) {
                "admin" -> AdminDashboardActivity::class.java
                "alumni" -> AlumniDashboardActivity::class.java
                "student" -> StudentDashboardActivity::class.java
                else -> null
            }
            
            if (targetActivity != null) {
                startActivity(Intent(this, targetActivity))
                finish()
                return
            }
        }

        setContentView(R.layout.activity_main)

        // Apply animations
        animateViews()

        // Setup button clicks
        findViewById<TextView>(R.id.btnAdmin).setOnClickListener {
            startActivity(Intent(this, AdminLoginActivity::class.java))
        }

        findViewById<Button>(R.id.btnAlumni).setOnClickListener {
            startActivity(Intent(this, AlumniLoginActivity::class.java))
        }

        findViewById<Button>(R.id.btnStudent).setOnClickListener {
            startActivity(Intent(this, StudentLoginActivity::class.java))
        }
    }

    private fun animateViews() {
        // Fade in logo
        val logo = findViewById<ImageView>(R.id.ivLogo)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale)
        logo.alpha = 0f
        logo.postDelayed({
            logo.alpha = 1f
            logo.startAnimation(fadeIn)
        }, 100)

        // Fade in title
        val title = findViewById<TextView>(R.id.tvTitle)
        title.alpha = 0f
        title.postDelayed({
            title.alpha = 1f
            title.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_scale))
        }, 300)

        // Fade in subtitle
        val subtitle = findViewById<TextView>(R.id.tvSubtitle)
        subtitle.alpha = 0f
        subtitle.postDelayed({
            subtitle.alpha = 1f
            subtitle.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_scale))
        }, 450)

        // Slide up buttons
        val buttonsLayout = findViewById<LinearLayout>(R.id.llButtons)
        buttonsLayout.alpha = 0f
        buttonsLayout.postDelayed({
            buttonsLayout.alpha = 1f
            buttonsLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up))
        }, 600)
    }
}