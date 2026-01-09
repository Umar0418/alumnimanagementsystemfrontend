package com.simats.aluminimanagement

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateJobActivity : AppCompatActivity() {

    private var existingJob: JobModel? = null
    private lateinit var tvLastDate: TextView
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_job)

        // --- Views ---
        val etTitle = findViewById<EditText>(R.id.etJobTitle)
        val etCompany = findViewById<EditText>(R.id.etJobCompany)
        val etLocation = findViewById<EditText>(R.id.etJobLocation)
        val etType = findViewById<EditText>(R.id.etJobType)
        val etSalary = findViewById<EditText>(R.id.etJobSalary)
        val etDescription = findViewById<EditText>(R.id.etJobDescription)
        tvLastDate = findViewById(R.id.tvJobLastDate) // Corrected ID
        val btnPostJob = findViewById<Button>(R.id.btnPostJob)
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val tvScreenTitle = findViewById<TextView>(R.id.tvScreenTitle)

        ivBack.setOnClickListener { finish() }

        // --- Handle Edit vs. Create ---
        intent.getStringExtra("job_data")?.let {
            existingJob = Gson().fromJson(it, JobModel::class.java)
            tvScreenTitle.text = "Update Job"
            btnPostJob.text = "Update Job"
            etTitle.setText(existingJob?.title)
            etCompany.setText(existingJob?.company)
            etLocation.setText(existingJob?.location)
            etType.setText(existingJob?.job_type)
            etSalary.setText(existingJob?.salary)
            etDescription.setText(existingJob?.description)
            tvLastDate.text = existingJob?.last_date
        }

        // --- Date Picker Logic (Re-implemented) ---
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        tvLastDate.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // --- Post/Update Job Logic ---
        btnPostJob.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val company = etCompany.text.toString().trim()
            val location = etLocation.text.toString().trim()
            val type = etType.text.toString().trim()
            val salary = etSalary.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val lastDate = tvLastDate.text.toString().trim()

            if (title.isEmpty() || company.isEmpty() || type.isEmpty() || salary.isEmpty() || lastDate.isEmpty() || lastDate == "mm/dd/yyyy") {
                Toast.makeText(this, "Required fields are missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (existingJob != null) {
                updateJob(existingJob!!.id, title, company, description, location, type, salary, lastDate)
            } else {
                addJob(title, company, description, location, type, salary, lastDate)
            }
        }
    }

    private fun addJob(title: String, company: String, description: String?, location: String?, type: String, salary: String, lastDate: String) {
        ApiClient.instance.addJob(title, company, description, location, type, salary, lastDate)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(this@CreateJobActivity, "Job posted successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@CreateJobActivity, "Failed to post job", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@CreateJobActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun updateJob(id: String, title: String, company: String, description: String?, location: String?, type: String, salary: String, lastDate: String) {
        ApiClient.instance.updateJob(id, title, company, description, location, type, salary, lastDate)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(this@CreateJobActivity, "Job updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@CreateJobActivity, "Failed to update job", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@CreateJobActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // your desired format
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        tvLastDate.text = sdf.format(calendar.time)
    }
}
