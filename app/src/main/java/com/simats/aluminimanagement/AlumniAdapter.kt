package com.simats.aluminimanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlumniAdapter(
    private val alumniList: List<Alumni>
) : RecyclerView.Adapter<AlumniAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val initial: TextView = view.findViewById(R.id.tvAlumniInitial)
        val name: TextView = view.findViewById(R.id.tvAlumniName)
        val company: TextView = view.findViewById(R.id.tvAlumniCompany)
        val department: TextView = view.findViewById(R.id.tvAlumniDept)
        val batch: TextView = view.findViewById(R.id.tvAlumniBatch)
        val email: TextView = view.findViewById(R.id.tvAlumniEmail)
        val phone: TextView = view.findViewById(R.id.tvAlumniPhone)
        val emailRow: LinearLayout = view.findViewById(R.id.emailRow)
        val phoneRow: LinearLayout = view.findViewById(R.id.phoneRow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumni, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alumni = alumniList[position]
        
        // Initial letter
        holder.initial.text = alumni.name.firstOrNull()?.toString()?.uppercase() ?: "A"
        
        // Name
        holder.name.text = alumni.name
        
        // Company with designation
        val companyText = buildString {
            if (!alumni.designation.isNullOrBlank()) {
                append(alumni.designation)
                if (!alumni.company.isNullOrBlank()) {
                    append(" at ")
                }
            }
            if (!alumni.company.isNullOrBlank()) {
                append(alumni.company)
            }
        }
        holder.company.text = if (companyText.isNotBlank()) companyText else "Not specified"
        
        // Department
        holder.department.text = alumni.department ?: "N/A"
        
        // Batch year
        holder.batch.text = if (!alumni.batchYear.isNullOrBlank()) "Batch ${alumni.batchYear}" else "N/A"
        
        // Email
        if (!alumni.email.isNullOrBlank()) {
            holder.emailRow.visibility = View.VISIBLE
            holder.email.text = alumni.email
        } else {
            holder.emailRow.visibility = View.GONE
        }
        
        // Phone
        if (!alumni.phone.isNullOrBlank()) {
            holder.phoneRow.visibility = View.VISIBLE
            holder.phone.text = alumni.phone
        } else {
            holder.phoneRow.visibility = View.GONE
        }
    }

    override fun getItemCount() = alumniList.size
}
