package com.simats.aluminimanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

interface FundClickListener {
    fun onEditClicked(fund: FundModel)
    fun onDeleteClicked(fund: FundModel)
}

class FundAdapter(
    private val funds: List<FundModel>,
    private val listener: FundClickListener
) : RecyclerView.Adapter<FundAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvFundTitle)
        val status: TextView = view.findViewById(R.id.tvFundStatus)
        val collectedAmount: TextView = view.findViewById(R.id.tvCollectedAmount)
        val targetAmount: TextView = view.findViewById(R.id.tvTargetAmount)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        val donorCount: TextView = view.findViewById(R.id.tvDonorCount)
        val percentage: TextView? = view.findViewById(R.id.tvPercentage)
        val editButton: ImageView = view.findViewById(R.id.ivEditFund)
        val deleteButton: ImageView = view.findViewById(R.id.ivDeleteFund)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fund_campaign, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fund = funds[position]
        holder.title.text = fund.title

        // Format currency for Indian Rupees
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        format.maximumFractionDigits = 0
        
        val collectedFormatted = format.format(fund.collectedAmount)
        val targetFormatted = format.format(fund.targetAmount)
        
        holder.collectedAmount.text = collectedFormatted
        holder.targetAmount.text = "of $targetFormatted"

        // Calculate progress percentage
        val progress = if (fund.targetAmount > 0) {
            ((fund.collectedAmount / fund.targetAmount) * 100).toInt()
        } else {
            0
        }
        
        holder.progressBar.progress = progress
        holder.percentage?.text = "$progress% funded"

        holder.donorCount.text = "${fund.donors ?: 0} Donors"
        
        // Set status based on progress
        holder.status.text = when {
            progress >= 100 -> "Completed"
            progress > 0 -> "Active"
            else -> "New"
        }

        holder.editButton.setOnClickListener {
            listener.onEditClicked(fund)
        }

        holder.deleteButton.setOnClickListener {
            listener.onDeleteClicked(fund)
        }
    }

    override fun getItemCount() = funds.size
}
