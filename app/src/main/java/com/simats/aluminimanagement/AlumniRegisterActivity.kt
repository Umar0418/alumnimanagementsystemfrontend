package com.simats.aluminimanagement

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

class AlumniRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumni_register)

        val etName = findViewById<EditText>(R.id.etName)
        val etRollNo = findViewById<EditText>(R.id.etRollNo)
        val etDepartment = findViewById<EditText>(R.id.etDepartment)
        val etBatch = findViewById<EditText>(R.id.etBatch)
        val etEmail = findViewById<EditText>(R.id.etRegisterEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etDegree = findViewById<EditText>(R.id.etDegree)
        val etPassword = findViewById<EditText>(R.id.etRegisterPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)
        val tvBackToLogin = findViewById<TextView>(R.id.tvBackToLogin)

        // Back button
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        tvBackToLogin.setOnClickListener { finish() }

        btnCreateAccount.setOnClickListener {
            val name = etName.text.toString().trim()
            val rollNo = etRollNo.text.toString().trim()
            val department = etDepartment.text.toString().trim()
            val batch = etBatch.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val degree = etDegree.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (name.isEmpty() || rollNo.isEmpty() || department.isEmpty() || batch.isEmpty() || email.isEmpty() || phone.isEmpty() || degree.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // CORRECTED: The password parameter is now correctly passed to the function call.
            ApiClient.instance.registerAlumni(rollNo, name, email, phone, batch, degree, department, password)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        val body = response.body()
                        if (response.isSuccessful && body != null && body.status) {
                            Toast.makeText(this@AlumniRegisterActivity, body.message ?: "Registration Successful!", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            val errorMessage = body?.message ?: body?.error ?: "Registration failed. Please try again."
                            Toast.makeText(this@AlumniRegisterActivity, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@AlumniRegisterActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}
