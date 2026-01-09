package com.simats.aluminimanagement

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendAnnouncementActivity : AppCompatActivity() {

    private var existingAnnouncement: AnnouncementModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_announcement)

        val etTitle = findViewById<EditText>(R.id.etAnnouncementTitle)
        val etMessage = findViewById<EditText>(R.id.etAnnouncementMessage)
        val cbStudents = findViewById<CheckBox>(R.id.cbStudents)
        val cbAlumni = findViewById<CheckBox>(R.id.cbAlumni)
        val btnSend = findViewById<Button>(R.id.btnSendBroadcast)
        val ivBack = findViewById<ImageView>(R.id.ivBack)

        ivBack.setOnClickListener { finish() }

        intent.getStringExtra("announcement_data")?.let {
            existingAnnouncement = Gson().fromJson(it, AnnouncementModel::class.java)
            etTitle.setText(existingAnnouncement?.title)
            etMessage.setText(existingAnnouncement?.message)
            btnSend.text = "Update Broadcast"
            when (existingAnnouncement?.target) {
                "students" -> cbStudents.isChecked = true
                "alumni" -> cbAlumni.isChecked = true
                "both" -> {
                    cbStudents.isChecked = true
                    cbAlumni.isChecked = true
                }
            }
        }

        btnSend.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val message = etMessage.text.toString().trim()

            if (title.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Title and message are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val target = when {
                cbStudents.isChecked && cbAlumni.isChecked -> "both"
                cbStudents.isChecked -> "students"
                cbAlumni.isChecked -> "alumni"
                else -> {
                    Toast.makeText(this, "Please select at least one recipient group", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            if (existingAnnouncement != null) {
                updateAnnouncement(existingAnnouncement!!.id.toString(), title, message, target)
            } else {
                addAnnouncement(title, message, target)
            }
        }
    }

    private fun addAnnouncement(title: String, message: String, target: String) {
        ApiClient.instance.addAnnouncement(title, message, target)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(this@SendAnnouncementActivity, "Announcement Sent!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@SendAnnouncementActivity, "Failed to send announcement", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@SendAnnouncementActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    // Update an existing announcement - the 'id' parameter is a String for the API
    private fun updateAnnouncement(id: String, title: String, message: String, target: String) {
        ApiClient.instance.updateAnnouncement(id, title, message, target)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(this@SendAnnouncementActivity, "Announcement Updated!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@SendAnnouncementActivity, "Failed to update announcement", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@SendAnnouncementActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}