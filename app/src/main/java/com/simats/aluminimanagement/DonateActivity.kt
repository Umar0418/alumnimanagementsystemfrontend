package com.simats.aluminimanagement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DonateActivity : AppCompatActivity() {

    private lateinit var rgCampaigns: RadioGroup
    private lateinit var progressCampaigns: ProgressBar
    private lateinit var tvNoCampaigns: TextView
    private lateinit var etCustomAmount: EditText
    private lateinit var btnProceedToPay: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var btnAmount1: TextView
    private lateinit var btnAmount2: TextView
    private lateinit var btnAmount3: TextView

    private var selectedCampaign: FundModel? = null
    private var selectedAmount: Int = 0
    private var campaigns: List<FundModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)

        initViews()
        setupAmountButtons()
        loadCampaigns()

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
        
        btnProceedToPay.setOnClickListener { proceedToPayment() }
    }

    private fun initViews() {
        rgCampaigns = findViewById(R.id.rgCampaigns)
        progressCampaigns = findViewById(R.id.progressCampaigns)
        tvNoCampaigns = findViewById(R.id.tvNoCampaigns)
        etCustomAmount = findViewById(R.id.etCustomAmount)
        btnProceedToPay = findViewById(R.id.btnProceedToPay)
        progressBar = findViewById(R.id.progressBar)
        
        btnAmount1 = findViewById(R.id.btnAmount1)
        btnAmount2 = findViewById(R.id.btnAmount2)
        btnAmount3 = findViewById(R.id.btnAmount3)
    }

    private fun setupAmountButtons() {
        btnAmount1.setOnClickListener { selectAmount(1000, btnAmount1) }
        btnAmount2.setOnClickListener { selectAmount(5000, btnAmount2) }
        btnAmount3.setOnClickListener { selectAmount(10000, btnAmount3) }

        etCustomAmount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                clearAmountSelection()
            }
        }
    }

    private fun selectAmount(amount: Int, selectedBtn: TextView) {
        selectedAmount = amount
        etCustomAmount.setText("")

        // Reset all buttons
        listOf(btnAmount1, btnAmount2, btnAmount3).forEach { btn ->
            btn.isSelected = false
            btn.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
        }

        // Highlight selected
        selectedBtn.isSelected = true
        selectedBtn.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    private fun clearAmountSelection() {
        selectedAmount = 0
        listOf(btnAmount1, btnAmount2, btnAmount3).forEach { btn ->
            btn.isSelected = false
            btn.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
        }
    }

    private fun loadCampaigns() {
        progressCampaigns.visibility = View.VISIBLE
        tvNoCampaigns.visibility = View.GONE
        rgCampaigns.removeAllViews()

        ApiClient.instance.getFunds().enqueue(object : Callback<FundListResponse> {
            override fun onResponse(call: Call<FundListResponse>, response: Response<FundListResponse>) {
                progressCampaigns.visibility = View.GONE
                val body = response.body()
                if (response.isSuccessful && body != null && body.status) {
                    campaigns = body.funds ?: emptyList()
                    if (campaigns.isEmpty()) {
                        tvNoCampaigns.visibility = View.VISIBLE
                    } else {
                        populateCampaigns(campaigns)
                    }
                } else {
                    tvNoCampaigns.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<FundListResponse>, t: Throwable) {
                progressCampaigns.visibility = View.GONE
                tvNoCampaigns.visibility = View.VISIBLE
                Toast.makeText(this@DonateActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateCampaigns(campaigns: List<FundModel>) {
        campaigns.forEachIndexed { index, campaign ->
            val radioButton = RadioButton(this).apply {
                id = View.generateViewId()
                text = campaign.title
                textSize = 15f
                setTextColor(ContextCompat.getColor(this@DonateActivity, R.color.text_primary))
                setPadding(16, 32, 16, 32)
                buttonTintList = ContextCompat.getColorStateList(this@DonateActivity, R.color.colorPrimary)
                
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedCampaign = campaign
                    }
                }
            }
            
            rgCampaigns.addView(radioButton)
            
            // Add divider after each item except last
            if (index < campaigns.size - 1) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    ).apply {
                        setMargins(0, 8, 0, 8)
                    }
                    setBackgroundColor(ContextCompat.getColor(this@DonateActivity, R.color.divider))
                }
                rgCampaigns.addView(divider)
            }

            // Select first by default
            if (index == 0) {
                radioButton.isChecked = true
                selectedCampaign = campaign
            }
        }

        // Animate
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale)
        rgCampaigns.startAnimation(fadeIn)
    }

    private fun proceedToPayment() {
        // Get final amount
        val customAmountStr = etCustomAmount.text.toString().trim()
        val finalAmount = if (customAmountStr.isNotEmpty()) {
            customAmountStr.toIntOrNull() ?: 0
        } else {
            selectedAmount
        }

        if (finalAmount <= 0) {
            Toast.makeText(this, "Please select or enter a donation amount", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedCampaign == null) {
            Toast.makeText(this, "Please select a campaign", Toast.LENGTH_SHORT).show()
            return
        }

        // Show payment options dialog or launch UPI intent
        showPaymentDialog(finalAmount)
    }

    private fun showPaymentDialog(amount: Int) {
        val options = arrayOf("UPI Payment", "Net Banking", "Card Payment")
        
        android.app.AlertDialog.Builder(this)
            .setTitle("Select Payment Method")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> launchUpiPayment(amount)
                    1 -> Toast.makeText(this, "Net Banking integration coming soon", Toast.LENGTH_SHORT).show()
                    2 -> Toast.makeText(this, "Card Payment integration coming soon", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun launchUpiPayment(amount: Int) {
        // Using a demo UPI ID format - for production, use verified UPI ID
        val upiUri = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", "anamalamuriumar@ybl") // Replace with actual verified UPI ID
            .appendQueryParameter("pn", "SIMATS Alumni Association")
            .appendQueryParameter("tn", "Donation - ${selectedCampaign?.title ?: "General Fund"}")
            .appendQueryParameter("am", amount.toString())
            .appendQueryParameter("cu", "INR")
            .build()

        val intent = Intent(Intent.ACTION_VIEW, upiUri).apply {
            setPackage(null) // Let user choose UPI app
        }

        try {
            startActivityForResult(intent, UPI_PAYMENT_REQUEST)
        } catch (e: Exception) {
            Toast.makeText(this, "No UPI apps installed. Please install a UPI app.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPI_PAYMENT_REQUEST) {
            val response = data?.getStringExtra("response") ?: ""
            
            when {
                response.contains("SUCCESS", ignoreCase = true) -> {
                    Toast.makeText(this, "Thank you for your generous donation!", Toast.LENGTH_LONG).show()
                    // TODO: Update backend with donation record
                    finish()
                }
                response.contains("FAILURE", ignoreCase = true) -> {
                    // Check for specific error messages
                    if (response.contains("invalid", ignoreCase = true) || 
                        response.contains("UPI", ignoreCase = true)) {
                        Toast.makeText(this, "Payment service temporarily unavailable. Please try again later.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Payment could not be completed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
                response.isEmpty() || resultCode == RESULT_CANCELED -> {
                    Toast.makeText(this, "Payment was cancelled.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Payment status unknown. Please check your payment app.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val UPI_PAYMENT_REQUEST = 1001
    }
}
