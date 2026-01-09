package com.simats.aluminimanagement

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditAlumniProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etCompany: EditText
    private lateinit var etLocation: EditText
    private lateinit var etDepartment: EditText
    private lateinit var etBatch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_alumni_profile)

        etName = findViewById(R.id.etEditName)
        etCompany = findViewById(R.id.etEditCompany)
        etLocation = findViewById(R.id.etEditLocation)
        etDepartment = findViewById(R.id.etEditDepartment)
        etBatch = findViewById(R.id.etEditBatch)

        val profileJson = intent.getStringExtra("profile_data")
        val profile = Gson().fromJson(profileJson, AlumniProfile::class.java)

        // Pre-fill the form
        etName.setText(profile.name)
        etCompany.setText(profile.company)
        etLocation.setText(profile.location)
        etDepartment.setText(profile.department)
        etBatch.setText(profile.batchYear)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnSaveChanges).setOnClickListener {
            saveChanges()
        }
    }

    private fun saveChanges() {
        val rollNo = SessionManager.getInstance(this).getRollNo() ?: return

        val name = etName.text.toString().trim()
        val company = etCompany.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val department = etDepartment.text.toString().trim()
        val batch = etBatch.text.toString().trim()

        ApiClient.instance.updateAlumniProfile(rollNo, name, company, location, department, batch)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(this@EditAlumniProfileActivity, "Profile updated!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this@EditAlumniProfileActivity, response.body()?.message ?: "Failed to update profile", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@EditAlumniProfileActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}