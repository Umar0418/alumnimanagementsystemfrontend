package com.simats.aluminimanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(
    private val studentList: List<Student>
) : RecyclerView.Adapter<StudentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val initial: TextView = view.findViewById(R.id.tvStudentInitial)
        val name: TextView = view.findViewById(R.id.tvStudentName)
        val rollNo: TextView = view.findViewById(R.id.tvStudentRollNo)
        val department: TextView = view.findViewById(R.id.tvStudentDept)
        val year: TextView = view.findViewById(R.id.tvStudentYear)
        val email: TextView = view.findViewById(R.id.tvStudentEmail)
        val phone: TextView = view.findViewById(R.id.tvStudentPhone)
        val yearRow: LinearLayout = view.findViewById(R.id.yearRow)
        val emailRow: LinearLayout = view.findViewById(R.id.emailRow)
        val phoneRow: LinearLayout = view.findViewById(R.id.phoneRow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = studentList[position]
        
        // Initial letter
        holder.initial.text = student.name.firstOrNull()?.toString()?.uppercase() ?: "S"
        
        // Name
        holder.name.text = student.name
        
        // Roll No
        holder.rollNo.text = student.rollNo.ifBlank { "N/A" }
        
        // Department
        holder.department.text = student.department ?: "N/A"
        
        // Year
        if (!student.year.isNullOrBlank()) {
            holder.yearRow.visibility = View.VISIBLE
            holder.year.text = "Year: ${student.year}"
        } else {
            holder.yearRow.visibility = View.GONE
        }
        
        // Email
        if (!student.email.isNullOrBlank()) {
            holder.emailRow.visibility = View.VISIBLE
            holder.email.text = student.email
        } else {
            holder.emailRow.visibility = View.GONE
        }
        
        // Phone
        if (!student.phone.isNullOrBlank()) {
            holder.phoneRow.visibility = View.VISIBLE
            holder.phone.text = student.phone
        } else {
            holder.phoneRow.visibility = View.GONE
        }
    }

    override fun getItemCount() = studentList.size
}
