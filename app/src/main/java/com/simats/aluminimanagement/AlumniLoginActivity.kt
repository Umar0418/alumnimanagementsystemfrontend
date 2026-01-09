package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlumniLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumni_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        ivBack.setOnClickListener { finish() }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, AlumniRegisterActivity::class.java))
        }

        findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            intent.putExtra("user_type", "alumni")
            startActivity(intent)
        }

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
                        val user = loginResponse.user
                        
                        // CORRECTED: Provide much more specific error messages
                        if (user == null) {
                            Toast.makeText(this@AlumniLoginActivity, "Login failed: Could not parse user data from server.", Toast.LENGTH_LONG).show()
                        } else if (user.rollNo.isNullOrEmpty()) {
                            Toast.makeText(this@AlumniLoginActivity, "Login failed: 'roll_no' is missing from server response. Please update your login.php script.", Toast.LENGTH_LONG).show()
                        } else if (!user.userType.equals("alumni", ignoreCase = true)) {
                            Toast.makeText(this@AlumniLoginActivity, "Access Denied: Not an Alumni user.", Toast.LENGTH_LONG).show()
                        } else {
                            // Success
                            val sessionManager = SessionManager.getInstance(this@AlumniLoginActivity)
                            sessionManager.saveUser(user)

                            Toast.makeText(this@AlumniLoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                            
                            // Preload data in background for faster dashboard loading
                            DataCache.preloadForAlumni()

                            val intent = Intent(this@AlumniLoginActivity, AlumniDashboardActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        }

                    } else {
                        Toast.makeText(this@AlumniLoginActivity, loginResponse?.message ?: "Login Failed", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@AlumniLoginActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}