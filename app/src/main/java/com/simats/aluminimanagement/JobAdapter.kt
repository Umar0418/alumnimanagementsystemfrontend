package com.simats.aluminimanagement

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

interface JobClickListener {
    fun onApproveClicked(job: JobModel)
    fun onRejectClicked(job: JobModel)
    fun onEditClicked(job: JobModel)
    fun onDeleteClicked(job: JobModel)
}

class JobAdapter(
    private val jobs: List<JobModel>,
    private val listener: JobClickListener
) : RecyclerView.Adapter<JobAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvJobTitle)
        val company: TextView = view.findViewById(R.id.tvJobCompany)
        val postedBy: TextView = view.findViewById(R.id.tvPostedBy)
        val actionButtons: LinearLayout = view.findViewById(R.id.llJobActions)
        val statusBadge: TextView = view.findViewById(R.id.tvJobStatus)
        val approveButton: Button = view.findViewById(R.id.btnApproveJob)
        val rejectButton: Button = view.findViewById(R.id.btnRejectJob)
        val editButton: ImageView = view.findViewById(R.id.ivEditJob)
        val deleteButton: ImageView = view.findViewById(R.id.ivDeleteJob)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_job, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val job = jobs[position]

        holder.title.text = job.title
        holder.company.text = job.company

        val posterName = if (job.posted_by.isNullOrBlank()) "Admin" else job.posted_by
        holder.postedBy.text = "Posted by: $posterName"

        if (job.status.equals("pending", ignoreCase = true)) {
            holder.actionButtons.visibility = View.VISIBLE
            holder.statusBadge.visibility = View.GONE
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        } else {
            holder.actionButtons.visibility = View.GONE
            holder.statusBadge.visibility = View.VISIBLE
            holder.statusBadge.text = job.status?.replaceFirstChar { it.uppercase() }
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE
        }

        holder.approveButton.setOnClickListener { listener.onApproveClicked(job) }
        holder.rejectButton.setOnClickListener { listener.onRejectClicked(job) }
        holder.editButton.setOnClickListener { listener.onEditClicked(job) }
        holder.deleteButton.setOnClickListener { listener.onDeleteClicked(job) }

        // Make the whole card clickable
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, JobDetailsActivity::class.java)
            intent.putExtra("job_data", Gson().toJson(job))
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = jobs.size
}