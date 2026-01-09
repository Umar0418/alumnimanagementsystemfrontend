package com.simats.aluminimanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JobListAdapter(
    private var jobs: List<JobModel>,
    private val listener: StudentJobClickListener
) : RecyclerView.Adapter<JobListAdapter.JobViewHolder>() {

    private var filteredJobs: List<JobModel> = jobs

    inner class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCompanyInitial: TextView = itemView.findViewById(R.id.tvCompanyInitial)
        val tvJobTitle: TextView = itemView.findViewById(R.id.tvJobTitle)
        val tvCompanyName: TextView = itemView.findViewById(R.id.tvCompanyName)
        val tvJobTypeBadge: TextView = itemView.findViewById(R.id.tvJobTypeBadge)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvSalary: TextView = itemView.findViewById(R.id.tvSalary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_job_list, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = filteredJobs[position]

        // Company initial
        holder.tvCompanyInitial.text = job.company.firstOrNull()?.uppercase() ?: "C"

        // Job info
        holder.tvJobTitle.text = job.title
        holder.tvCompanyName.text = job.company

        // Job type badge
        holder.tvJobTypeBadge.text = job.job_type

        // Location
        holder.tvLocation.text = job.location ?: "Remote"

        // Salary
        holder.tvSalary.text = job.salary

        // Click listener
        holder.itemView.setOnClickListener {
            listener.onJobClick(job)
        }
    }

    override fun getItemCount(): Int = filteredJobs.size

    fun updateList(newJobs: List<JobModel>) {
        jobs = newJobs
        filteredJobs = newJobs
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredJobs = if (query.isEmpty()) {
            jobs
        } else {
            jobs.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.company.contains(query, ignoreCase = true) ||
                it.location?.contains(query, ignoreCase = true) == true
            }
        }
        notifyDataSetChanged()
    }

    fun filterByType(type: String) {
        filteredJobs = when (type) {
            "All" -> jobs
            "Internships" -> jobs.filter { it.job_type.contains("Intern", ignoreCase = true) }
            "Full-time" -> jobs.filter { it.job_type.contains("Full", ignoreCase = true) }
            "Remote" -> jobs.filter { it.location?.contains("Remote", ignoreCase = true) == true }
            else -> jobs.filter { it.title.contains(type, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}
