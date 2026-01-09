package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_login)

        val etEmail = findViewById<EditText>(R.id.etStudentEmail)
        val etPassword = findViewById<EditText>(R.id.etStudentPassword)
        val btnSignIn = findViewById<Button>(R.id.btnStudentSignIn)

        val card = findViewById<CardView>(R.id.studentLoginCard)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        card.startAnimation(fadeIn)

        btnSignIn.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ApiClient.instance.login(email, password).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val loginResponse = response.body()
                    if (response.isSuccessful && loginResponse != null && loginResponse.status) {
                        if (loginResponse.user?.userType.equals("student", ignoreCase = true)) {
                            loginResponse.user?.let {
                                val sessionManager = SessionManager.getInstance(this@StudentLoginActivity)
                                sessionManager.saveUser(it)
                            }

                            Toast.makeText(this@StudentLoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                            
                            // Preload data in background for faster dashboard loading
                            DataCache.preloadForStudent()

                            val intent = Intent(this@StudentLoginActivity, StudentDashboardActivity::class.java)
                            startActivity(intent)
                            finishAffinity()

                        } else {
                            Toast.makeText(this@StudentLoginActivity, "Access Denied: Not a Student user.", Toast.LENGTH_LONG).show()
                        }

                    } else {
                        Toast.makeText(this@StudentLoginActivity, loginResponse?.message ?: "Login Failed", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@StudentLoginActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        // CORRECTED: This now launches the correct StudentRegisterActivity
        findViewById<TextView>(R.id.tvCreateStudentAccount).setOnClickListener {
            startActivity(Intent(this, StudentRegisterActivity::class.java))
        }
        
        // Forgot Password
        findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            intent.putExtra("user_type", "student")
            startActivity(intent)
        }
        
        // Back button
        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }
    }
}