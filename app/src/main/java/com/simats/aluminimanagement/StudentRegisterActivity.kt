package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout 
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_register)

        val etName = findViewById<EditText>(R.id.etRegName)
        val etStudentId = findViewById<EditText>(R.id.etRegStudentId)
        val etYear = findViewById<EditText>(R.id.etRegYear)
        val etDepartment = findViewById<EditText>(R.id.etRegDepartment)
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etPassword = findViewById<EditText>(R.id.etRegPassword)
        val btnCreate = findViewById<Button>(R.id.btnCreateAccount)

        val form = findViewById<LinearLayout>(R.id.formContainer)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        form.startAnimation(fadeIn)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        findViewById<TextView>(R.id.tvSignInLink).setOnClickListener {
            finish() // Go back to the login screen
        }

        btnCreate.setOnClickListener {
            val name = etName.text.toString().trim()
            val studentId = etStudentId.text.toString().trim()
            val year = etYear.text.toString().trim()
            val department = etDepartment.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isEmpty() || studentId.isEmpty() || year.isEmpty() || department.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // CORRECTED: The year and department parameters are now in the correct order
            ApiClient.instance.registerStudent(studentId, name, email, year, department, password)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        val regResponse = response.body()
                        if (response.isSuccessful && regResponse != null && regResponse.status) {
                            Toast.makeText(this@StudentRegisterActivity, "Account Created! Please sign in.", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this@StudentRegisterActivity, regResponse?.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@StudentRegisterActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}