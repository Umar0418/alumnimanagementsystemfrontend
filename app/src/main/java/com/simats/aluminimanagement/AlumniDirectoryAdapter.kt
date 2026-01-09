package com.simats.aluminimanagement

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlumniDirectoryAdapter(
    private var alumniList: List<AlumniDirectoryItem>
) : RecyclerView.Adapter<AlumniDirectoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val initial: TextView = view.findViewById(R.id.tvAlumniInitial)
        val name: TextView = view.findViewById(R.id.tvAlumniName)
        val title: TextView = view.findViewById(R.id.tvAlumniTitle)
        val batch: TextView = view.findViewById(R.id.tvAlumniBatch)
        val department: TextView = view.findViewById(R.id.tvAlumniDepartment)
        val location: TextView = view.findViewById(R.id.tvAlumniLocation)
        val viewProfileButton: Button = view.findViewById(R.id.btnViewProfile)
        val connectButton: Button = view.findViewById(R.id.btnConnect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumni_directory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alumni = alumniList[position]

        holder.initial.text = alumni.name.firstOrNull()?.toString() ?: ""
        holder.name.text = alumni.name
        holder.title.text = alumni.company.ifEmpty { "Not specified" }
        holder.batch.text = alumni.batchYear
        holder.department.text = alumni.department
        holder.location.text = alumni.location.ifEmpty { "Not specified" }

        // View Profile button click - navigate to ViewAlumniProfileActivity
        holder.viewProfileButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ViewAlumniProfileActivity::class.java).apply {
                putExtra("roll_no", alumni.rollNo)
                putExtra("name", alumni.name)
                putExtra("department", alumni.department)
                putExtra("batch_year", alumni.batchYear)
                putExtra("company", alumni.company)
                putExtra("location", alumni.location)
                putExtra("mentorship", alumni.mentorship)
            }
            context.startActivity(intent)
        }

        // Connect button click
        holder.connectButton.setOnClickListener {
            // TODO: Implement connect functionality (e.g., send connection request)
        }
    }

    override fun getItemCount() = alumniList.size

    // Function to update the list with search results
    fun filterList(filteredList: List<AlumniDirectoryItem>) {
        alumniList = filteredList
        notifyDataSetChanged()
    }
}