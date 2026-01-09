package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)

        ivBack.setOnClickListener { finish() }

        // Navigate to Admin Registration
        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, AdminRegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ApiClient.instance.login(email, password)
                .enqueue(object : Callback<LoginResponse> {

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        val loginResponse = response.body()
                        // Check for a successful HTTP response first
                        if (response.isSuccessful && loginResponse != null) {
                            // Then check the 'status' from your API
                            if (loginResponse.status) {
                                if (loginResponse.user?.userType.equals("admin", ignoreCase = true))
                                    {
                                    loginResponse.user?.let {
                                        val sessionManager = SessionManager.getInstance(this@AdminLoginActivity)
                                        sessionManager.saveUser(it)
                                    }
                                    Toast.makeText(this@AdminLoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                                    
                                    // Preload data in background for faster dashboard loading
                                    DataCache.preloadForAdmin()
                                    
                                    startActivity(Intent(this@AdminLoginActivity, AdminDashboardActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this@AdminLoginActivity, "Access Denied: Not an Admin user.", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                // The API returned status: false
                                Toast.makeText(this@AdminLoginActivity, loginResponse.message ?: "Login Failed", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            // The server returned a non-successful HTTP status code (e.g., 404, 500)
                            // This will prevent the "Expected BEGIN_OBJECT" crash
                            val errorMsg = "Login Failed: Server returned error ${response.code()}"
                            Toast.makeText(this@AdminLoginActivity, errorMsg, Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        t.printStackTrace()
                        Toast.makeText(
                            this@AdminLoginActivity,
                            "Network Error: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }
}