package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val studentBtn = findViewById<Button>(R.id.btnStudent)
        val alumniBtn = findViewById<Button>(R.id.btnAlumni)
        val adminBtn = findViewById<TextView>(R.id.btnAdmin)

        studentBtn.setOnClickListener {
            val intent = Intent(this@SplashActivity, SubscriptionActivity::class.java)
            intent.putExtra("user_type", "student")
            startActivity(intent)
        }

        alumniBtn.setOnClickListener {
            val intent = Intent(this@SplashActivity, SubscriptionActivity::class.java)
            intent.putExtra("user_type", "alumni")
            startActivity(intent)
        }

        adminBtn.setOnClickListener {
            val intent = Intent(this@SplashActivity, SubscriptionActivity::class.java)
            intent.putExtra("user_type", "admin")
            startActivity(intent)
        }
    }
}
