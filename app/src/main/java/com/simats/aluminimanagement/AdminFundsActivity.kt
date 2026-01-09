package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminFundsActivity : AppCompatActivity(), FundClickListener {

    private lateinit var recyclerFunds: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_funds)

        recyclerFunds = findViewById(R.id.recyclerFunds)
        recyclerFunds.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        findViewById<ExtendedFloatingActionButton>(R.id.fabNewCampaign).setOnClickListener {
            startActivity(Intent(this, CreateCampaignActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadFunds()
    }

    private fun loadFunds() {
        ApiClient.instance.getFunds().enqueue(object : Callback<FundListResponse> {
            override fun onResponse(call: Call<FundListResponse>, response: Response<FundListResponse>) {
                val body = response.body()
                if (response.isSuccessful && body != null && body.status) {
                    if (!body.funds.isNullOrEmpty()) {
                        recyclerFunds.adapter = FundAdapter(body.funds, this@AdminFundsActivity)
                    } else {
                        Toast.makeText(this@AdminFundsActivity, "No fund campaigns found.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@AdminFundsActivity, "Failed to load campaigns.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FundListResponse>, t: Throwable) {
                Toast.makeText(this@AdminFundsActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onEditClicked(fund: FundModel) {
        val intent = Intent(this, CreateCampaignActivity::class.java)
        intent.putExtra("fund_data", Gson().toJson(fund))
        startActivity(intent)
    }

    override fun onDeleteClicked(fund: FundModel) {
        AlertDialog.Builder(this)
            .setTitle("Delete Campaign")
            .setMessage("Are you sure you want to delete '${fund.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                deleteFund(fund.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteFund(fundId: String) {
        ApiClient.instance.deleteFund(fundId).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@AdminFundsActivity, "Campaign deleted successfully", Toast.LENGTH_SHORT).show()
                    loadFunds() // Refresh the list
                } else {
                    Toast.makeText(this@AdminFundsActivity, "Failed to delete campaign", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@AdminFundsActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}