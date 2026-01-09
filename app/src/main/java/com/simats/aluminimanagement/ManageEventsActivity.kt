package com.simats.aluminimanagement

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageEventsActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private lateinit var ivBannerPreview: ImageView
    private lateinit var llUploadPlaceholder: LinearLayout
    
    // For edit mode
    private var isEditMode = false
    private var editEventId: String? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            ivBannerPreview.setImageURI(it)
            ivBannerPreview.visibility = View.VISIBLE
            llUploadPlaceholder.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        // --- Views ---
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etTime = findViewById<EditText>(R.id.etTime)
        val etVenue = findViewById<EditText>(R.id.etVenue)
        val etDesc = findViewById<EditText>(R.id.etDescription)
        val btnPublish = findViewById<Button>(R.id.btnPublish)
        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val tvHeader = findViewById<TextView>(R.id.tvHeader)
        val spinnerAudience = findViewById<Spinner>(R.id.spinnerAudience)
        val flUploadBanner = findViewById<FrameLayout>(R.id.flUploadBanner)
        ivBannerPreview = findViewById(R.id.ivBannerPreview)
        llUploadPlaceholder = findViewById(R.id.llUploadPlaceholder)

        // --- Setup ---
        ivBack.setOnClickListener { finish() }

        // Setup Spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.target_audience_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAudience.adapter = adapter
        }

        // Setup Image Picker
        flUploadBanner.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Check for edit mode
        intent.getStringExtra("event_data")?.let { eventJson ->
            try {
                val event = Gson().fromJson(eventJson, EventModel::class.java)
                isEditMode = true
                editEventId = event.id.toString()
                
                // Update UI for edit mode
                tvHeader?.text = "Edit Event"
                btnPublish.text = "Update Event"
                
                // Pre-fill form
                etTitle.setText(event.title)
                etDate.setText(event.event_date)
                etTime.setText(event.event_time)
                etVenue.setText(event.venue)
                etDesc.setText(event.description ?: "")
            } catch (e: Exception) {
                Toast.makeText(this, "Error loading event data", Toast.LENGTH_SHORT).show()
            }
        }

        // --- Actions ---
        btnPublish.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val date = etDate.text.toString().trim()
            val time = etTime.text.toString().trim()
            val venue = etVenue.text.toString().trim()
            val description = etDesc.text.toString().trim()

            if (title.isEmpty() || date.isEmpty() || time.isEmpty() || venue.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isEditMode && editEventId != null) {
                // Update existing event
                ApiClient.instance.updateEvent(editEventId!!, title, description, date, time, venue)
                    .enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                            if (response.isSuccessful && response.body()?.status == true) {
                                Toast.makeText(this@ManageEventsActivity, "Event Updated Successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@ManageEventsActivity, "Failed to update event: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            Toast.makeText(this@ManageEventsActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
                        }
                    })
            } else {
                // Add new event
                ApiClient.instance.addEvent(title, description, date, time, venue)
                    .enqueue(object : Callback<LoginResponse> {
                        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                            if (response.isSuccessful && response.body()?.status == true) {
                                Toast.makeText(this@ManageEventsActivity, "Event Added Successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@ManageEventsActivity, "Failed to add event: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            Toast.makeText(this@ManageEventsActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
                        }
                    })
            }
        }
    }
}
