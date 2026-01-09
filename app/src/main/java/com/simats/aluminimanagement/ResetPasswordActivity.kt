package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnResetPassword: Button
    private lateinit var progressBar: ProgressBar
    private var userType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etEmail = findViewById(R.id.etEmail)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnResetPassword = findViewById(R.id.btnResetPassword)
        progressBar = findViewById(R.id.progressBar)

        // Get email and user type from intent
        intent.getStringExtra("email")?.let {
            etEmail.setText(it)
        }
        userType = intent.getStringExtra("user_type") ?: ""

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tvBackToLogin).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        btnResetPassword.setOnClickListener { resetPassword() }
    }

    private fun resetPassword() {
        val email = etEmail.text.toString().trim()
        val newPassword = etNewPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Validation
        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Please enter a valid email"
            etEmail.requestFocus()
            return
        }

        if (newPassword.isEmpty()) {
            etNewPassword.error = "Password is required"
            etNewPassword.requestFocus()
            return
        }

        if (newPassword.length < 6) {
            etNewPassword.error = "Password must be at least 6 characters"
            etNewPassword.requestFocus()
            return
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Please confirm your password"
            etConfirmPassword.requestFocus()
            return
        }

        if (newPassword != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            etConfirmPassword.requestFocus()
            return
        }

        // Show loading
        btnResetPassword.isEnabled = false
        btnResetPassword.text = "Resetting..."
        progressBar.visibility = View.VISIBLE

        // Call API with user type for validation
        ApiClient.instance.resetPasswordDirect(email, newPassword, userType)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    progressBar.visibility = View.GONE
                    btnResetPassword.isEnabled = true
                    btnResetPassword.text = "Reset Password"

                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Password reset successfully! Please login with your new password.",
                            Toast.LENGTH_LONG
                        ).show()

                        // Navigate to login
                        val intent = Intent(this@ResetPasswordActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            response.body()?.message ?: "Failed to reset password. Please check your email.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    btnResetPassword.isEnabled = true
                    btnResetPassword.text = "Reset Password"
                    
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Network error. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
