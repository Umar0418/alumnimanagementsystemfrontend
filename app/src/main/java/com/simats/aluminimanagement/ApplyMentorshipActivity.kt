package com.simats.aluminimanagement

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApplyMentorshipActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_mentorship)

        val etExpertise = findViewById<EditText>(R.id.etExpertise)
        val spinnerAvailability = findViewById<Spinner>(R.id.spinnerAvailability)
        val etStyle = findViewById<EditText>(R.id.etMentorshipStyle)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitApplication)
        val ivBack = findViewById<ImageView>(R.id.ivBack)

        ivBack.setOnClickListener { finish() }

        ArrayAdapter.createFromResource(
            this,
            R.array.availability_options,
            R.layout.spinner_item_layout
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAvailability.adapter = adapter
        }

        btnSubmit.setOnClickListener {
            val expertise = etExpertise.text.toString().trim()
            val availability = spinnerAvailability.selectedItem.toString()
            val style = etStyle.text.toString().trim()

            // CORRECTED: Get the real roll number from the singleton SessionManager
            val sessionManager = SessionManager.getInstance(this)
            val rollNo = sessionManager.getRollNo()

            if (rollNo == null) {
                Toast.makeText(this, "Error: Could not submit. Please log in again.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (expertise.isEmpty() || style.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ApiClient.instance.applyForMentorship(rollNo, expertise, availability, style)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful && response.body()?.status == true) {
                            Toast.makeText(this@ApplyMentorshipActivity, "Application Submitted!", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(this@ApplyMentorshipActivity, response.body()?.message ?: "Submission failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@ApplyMentorshipActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}
