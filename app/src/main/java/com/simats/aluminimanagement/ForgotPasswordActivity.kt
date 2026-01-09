package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnSendResetLink: Button
    private lateinit var progressBar: ProgressBar
    private var userType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Get user type from intent (passed from login screens)
        userType = intent.getStringExtra("user_type") ?: ""

        etEmail = findViewById(R.id.etEmail)
        btnSendResetLink = findViewById(R.id.btnSendResetLink)
        progressBar = findViewById(R.id.progressBar)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tvBackToLogin).setOnClickListener { finish() }

        btnSendResetLink.setOnClickListener { goToResetPassword() }
    }

    private fun goToResetPassword() {
        val email = etEmail.text.toString().trim()

        // Validation
        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Please enter a valid email"
            return
        }

        // Navigate to Reset Password screen with email and user type
        val intent = Intent(this, ResetPasswordActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("user_type", userType)
        startActivity(intent)
    }
}
