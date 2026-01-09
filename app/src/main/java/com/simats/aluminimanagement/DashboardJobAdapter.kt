package com.simats.aluminimanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DashboardJobAdapter(private val jobs: List<JobModel>) : RecyclerView.Adapter<DashboardJobAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logo: ImageView = view.findViewById(R.id.ivJobLogo)
        val title: TextView = view.findViewById(R.id.tvJobTitle)
        val company: TextView = view.findViewById(R.id.tvJobCompany)
        val type: TextView = view.findViewById(R.id.tvJobType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dashboard_job, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val job = jobs[position]
        holder.title.text = job.title
        holder.company.text = job.company
        // CORRECTED: Changed from the non-existent 'jobType' to the correct 'job_type' property
        holder.type.text = job.job_type
        // You would use a library like Glide or Picasso to load the company logo
        // holder.logo.setImageResource(R.drawable.ic_jobs)
    }

    override fun getItemCount() = jobs.size
}
