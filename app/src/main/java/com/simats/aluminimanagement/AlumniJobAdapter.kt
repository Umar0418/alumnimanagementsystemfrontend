package com.simats.aluminimanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface AlumniJobClickListener {
    fun onViewDetails(job: JobModel)
}

class AlumniJobAdapter(
    private var jobs: List<JobModel>,
    private val listener: AlumniJobClickListener
) : RecyclerView.Adapter<AlumniJobAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJobTitle: TextView = view.findViewById(R.id.tvJobTitle)
        val tvCompany: TextView = view.findViewById(R.id.tvCompany)
        val tvJobType: TextView = view.findViewById(R.id.tvJobType)
        val tvLocation: TextView = view.findViewById(R.id.tvLocation)
        val tvSalary: TextView = view.findViewById(R.id.tvSalary)
        val tvDeadline: TextView = view.findViewById(R.id.tvDeadline)
        val btnApply: Button = view.findViewById(R.id.btnApply)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumni_job, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val job = jobs[position]

        holder.tvJobTitle.text = job.title
        holder.tvCompany.text = job.company
        // CORRECTED: Use the correct snake_case property names from the JobModel
        holder.tvJobType.text = job.job_type
        holder.tvLocation.text = job.location ?: "Not specified"
        holder.tvSalary.text = job.salary
        holder.tvDeadline.text = "Apply by: ${job.last_date}"

        holder.btnApply.setOnClickListener {
            listener.onViewDetails(job)
        }

        holder.itemView.setOnClickListener {
            listener.onViewDetails(job)
        }
    }

    override fun getItemCount() = jobs.size

    fun updateList(newList: List<JobModel>) {
        jobs = newList
        notifyDataSetChanged()
    }
}