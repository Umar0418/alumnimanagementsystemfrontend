package com.simats.aluminimanagement

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateCampaignActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_campaign)

        val etTitle = findViewById<EditText>(R.id.etCampaignTitle)
        val etAmount = findViewById<EditText>(R.id.etGoalAmount)
        val etDeadline = findViewById<EditText>(R.id.etDeadline)
        val etDescription = findViewById<EditText>(R.id.etFundDescription)
        val btnLaunch = findViewById<Button>(R.id.btnLaunchCampaign)
        val ivBack = findViewById<ImageView>(R.id.ivBack)

        ivBack.setOnClickListener { finish() }

        btnLaunch.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val amount = etAmount.text.toString().trim().toDoubleOrNull()
            val deadline = etDeadline.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (title.isEmpty() || amount == null || deadline.isEmpty()) {
                Toast.makeText(this, "Title, Amount, and Deadline are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ApiClient.instance.addFund(title, description, amount, deadline)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful && response.body()?.status == true) {
                            Toast.makeText(this@CreateCampaignActivity, "Campaign Launched!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@CreateCampaignActivity, "Failed to launch campaign", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@CreateCampaignActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}
