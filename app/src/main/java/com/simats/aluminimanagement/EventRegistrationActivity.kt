package com.simats.aluminimanagement

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventRegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_registration)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        ivBack.setOnClickListener { finish() }

        val eventJson = intent.getStringExtra("event_data")
        if (eventJson == null) {
            Toast.makeText(this, "Error: Event data not found.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val event = Gson().fromJson(eventJson, EventModel::class.java)
        val sessionManager = SessionManager.getInstance(this)
        val user = sessionManager.getUser()

        // --- Populate Views ---
        findViewById<TextView>(R.id.tvEventTitleDetail).text = event.title
        findViewById<TextView>(R.id.tvEventDateTimeDetail).text = "${event.event_date} • ${event.event_time}"
        findViewById<TextView>(R.id.tvEventDescriptionDetail).text = event.description

        val etName = findViewById<EditText>(R.id.etRegName)
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etPhone = findViewById<EditText>(R.id.etRegPhone)

        etName.setText(user?.name ?: "")
        etEmail.setText(user?.email ?: "")
        etPhone.setText(user?.phone ?: "")

        // --- Registration Logic ---
        val btnConfirm = findViewById<Button>(R.id.btnConfirmRegistration)
        btnConfirm.setOnClickListener {
            val rollNo = user?.rollNo
            if (rollNo == null) {
                Toast.makeText(this, "Error: User session invalid. Please log in again.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // No change needed here, as event.id is now correctly an Int
            ApiClient.instance.registerForEvent(rollNo, event.id)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful && response.body()?.status == true) {
                            Toast.makeText(this@EventRegistrationActivity, "Registration Confirmed!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@EventRegistrationActivity, response.body()?.message ?: "Registration Failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@EventRegistrationActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}