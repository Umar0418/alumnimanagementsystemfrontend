package com.simats.aluminimanagement

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditStudentProfileActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_student_profile)

        sessionManager = SessionManager.getInstance(this)
        val user = sessionManager.getUser()

        // Back button
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        // Pre-fill fields
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etDepartment = findViewById<EditText>(R.id.etDepartment)
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val etCGPA = findViewById<EditText>(R.id.etCGPA)
        val etInterests = findViewById<EditText>(R.id.etInterests)

        user?.let {
            etName.setText(it.name ?: "")
            etEmail.setText(it.email ?: "")
            etPhone.setText(it.phone ?: "")
            etDepartment.setText(it.department ?: "")
            etAddress.setText(it.address ?: "")
            etCGPA.setText(it.cgpa ?: "")
            etInterests.setText(it.interests ?: "")
        }

        // Save button
        findViewById<Button>(R.id.btnSaveProfile).setOnClickListener {
            saveProfile(
                etName.text.toString().trim(),
                etPhone.text.toString().trim(),
                etDepartment.text.toString().trim(),
                etAddress.text.toString().trim(),
                etCGPA.text.toString().trim(),
                etInterests.text.toString().trim()
            )
        }
    }

    private fun saveProfile(
        name: String,
        phone: String,
        department: String,
        address: String,
        cgpa: String,
        interests: String
    ) {
        val user = sessionManager.getUser() ?: return
        val rollNo = user.rollNo ?: return

        ApiClient.instance.updateStudentProfile(
            rollNo, name, phone, department, address, cgpa, interests
        ).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    // Update local session
                    val updatedUser = User(
                        id = user.id,
                        rollNo = user.rollNo,
                        name = name,
                        email = user.email,
                        phone = phone,
                        userType = user.userType,
                        department = department,
                        year = user.year,
                        address = address,
                        cgpa = cgpa,
                        interests = interests
                    )
                    sessionManager.saveUser(updatedUser)

                    Toast.makeText(this@EditStudentProfileActivity, "Profile updated!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this@EditStudentProfileActivity, 
                        response.body()?.message ?: "Failed to update", 
                        Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@EditStudentProfileActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
