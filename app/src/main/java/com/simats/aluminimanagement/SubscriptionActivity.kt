package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SubscriptionActivity : AppCompatActivity() {

    private var selectedPaymentMethod: String? = null
    private lateinit var userType: String

    // Payment method layouts
    private lateinit var layoutUpi: LinearLayout
    private lateinit var layoutCard: LinearLayout
    private lateinit var layoutNetBanking: LinearLayout

    // Check icons
    private lateinit var ivUpiCheck: ImageView
    private lateinit var ivCardCheck: ImageView
    private lateinit var ivNetBankingCheck: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)

        // Get user type from intent
        userType = intent.getStringExtra("user_type") ?: "student"

        initViews()
        setupClickListeners()
        checkSubscriptionStatus()
    }

    private fun initViews() {
        // Payment method layouts
        layoutUpi = findViewById(R.id.layoutUpi)
        layoutCard = findViewById(R.id.layoutCard)
        layoutNetBanking = findViewById(R.id.layoutNetBanking)

        // Check icons
        ivUpiCheck = findViewById(R.id.ivUpiCheck)
        ivCardCheck = findViewById(R.id.ivCardCheck)
        ivNetBankingCheck = findViewById(R.id.ivNetBankingCheck)
    }

    private fun setupClickListeners() {
        // Back button
        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        // Payment method selection
        layoutUpi.setOnClickListener {
            selectPaymentMethod("upi")
        }

        layoutCard.setOnClickListener {
            selectPaymentMethod("card")
        }

        layoutNetBanking.setOnClickListener {
            selectPaymentMethod("netbanking")
        }

        // Subscribe button
        findViewById<Button>(R.id.btnSubscribe).setOnClickListener {
            handleSubscribe()
        }

        // Skip button - go directly to login
        findViewById<TextView>(R.id.tvSkip).setOnClickListener {
            navigateToLogin()
        }
    }

    private fun selectPaymentMethod(method: String) {
        selectedPaymentMethod = method

        // Reset all selections
        layoutUpi.isSelected = false
        layoutCard.isSelected = false
        layoutNetBanking.isSelected = false

        ivUpiCheck.visibility = View.GONE
        ivCardCheck.visibility = View.GONE
        ivNetBankingCheck.visibility = View.GONE

        // Set selected state
        when (method) {
            "upi" -> {
                layoutUpi.isSelected = true
                ivUpiCheck.visibility = View.VISIBLE
            }
            "card" -> {
                layoutCard.isSelected = true
                ivCardCheck.visibility = View.VISIBLE
            }
            "netbanking" -> {
                layoutNetBanking.isSelected = true
                ivNetBankingCheck.visibility = View.VISIBLE
            }
        }
    }

    private fun handleSubscribe() {
        if (selectedPaymentMethod == null) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
            return
        }

        // Show payment processing message
        Toast.makeText(this, "Processing payment via ${getPaymentMethodName()}...", Toast.LENGTH_SHORT).show()

        // Simulate successful payment for demo
        // In production, integrate with actual payment gateway (Razorpay, PayU, etc.)
        simulatePaymentSuccess()
    }

    private fun getPaymentMethodName(): String {
        return when (selectedPaymentMethod) {
            "upi" -> "UPI"
            "card" -> "Card"
            "netbanking" -> "Net Banking"
            else -> "Unknown"
        }
    }

    private fun simulatePaymentSuccess() {
        // Save subscription status
        val sessionManager = SessionManager.getInstance(this)
        sessionManager.saveSubscriptionStatus(true)

        Toast.makeText(this, "Subscription activated successfully!", Toast.LENGTH_SHORT).show()

        // Navigate to login screen
        navigateToLogin()
    }

    private fun checkSubscriptionStatus() {
        val sessionManager = SessionManager.getInstance(this)
        val isSubscribed = sessionManager.isSubscribed()

        val layoutNotSubscribed = findViewById<LinearLayout>(R.id.layoutNotSubscribed)
        val layoutSubscribed = findViewById<LinearLayout>(R.id.layoutSubscribed)
        val tvSubscriptionMessage = findViewById<TextView>(R.id.tvSubscriptionMessage)
        val btnSubscribe = findViewById<Button>(R.id.btnSubscribe)
        val tvSkip = findViewById<TextView>(R.id.tvSkip)

        if (isSubscribed) {
            layoutNotSubscribed.visibility = View.GONE
            layoutSubscribed.visibility = View.VISIBLE
            tvSubscriptionMessage.text = "Your premium subscription is active"
            btnSubscribe.text = "Continue to Login"
            btnSubscribe.setOnClickListener { navigateToLogin() }
            tvSkip.visibility = View.GONE
        } else {
            layoutNotSubscribed.visibility = View.VISIBLE
            layoutSubscribed.visibility = View.GONE
            tvSubscriptionMessage.text = "Subscribe now to access premium features"
        }
    }

    private fun navigateToLogin() {
        val intent = when (userType.lowercase()) {
            "student" -> Intent(this, StudentLoginActivity::class.java)
            "alumni" -> Intent(this, AlumniLoginActivity::class.java)
            "admin" -> Intent(this, AdminLoginActivity::class.java)
            else -> Intent(this, StudentLoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
