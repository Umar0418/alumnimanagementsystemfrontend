package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlumniDirectoryActivity : AppCompatActivity() {

    private lateinit var recyclerAlumni: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var chipGroup: ChipGroup
    private var adapter: AlumniDirectoryAdapter? = null
    private var fullAlumniList = listOf<AlumniDirectoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumni_directory)

        recyclerAlumni = findViewById(R.id.recyclerAlumniDirectory)
        etSearch = findViewById(R.id.etSearchAlumni)
        chipGroup = findViewById(R.id.chipGroupDepartments)
        recyclerAlumni.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with empty list
        adapter = AlumniDirectoryAdapter(emptyList())
        recyclerAlumni.adapter = adapter

        // Back button
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        loadAlumniDirectory()

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        chipGroup.setOnCheckedChangeListener { _, _ ->
            filterList()
        }

        // Setup bottom navigation if exists
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        try {
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNav?.let { nav ->
                nav.selectedItemId = R.id.nav_directory
                
                nav.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.nav_home -> {
                            startActivity(Intent(this, AlumniDashboardActivity::class.java))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finish()
                            true
                        }
                        R.id.nav_directory -> true
                        R.id.nav_jobs -> {
                            startActivity(Intent(this, AlumniJobsActivity::class.java))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finish()
                            true
                        }
                        R.id.nav_events -> {
                            startActivity(Intent(this, AlumniEventsActivity::class.java))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finish()
                            true
                        }
                        R.id.nav_profile -> {
                            startActivity(Intent(this, AlumniProfileActivity::class.java))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finish()
                            true
                        }
                        else -> false
                    }
                }
            }
        } catch (e: Exception) {
            // Bottom nav doesn't exist in this layout, ignore
        }
    }

    private fun loadAlumniDirectory() {
        Log.d("AlumniDirectory", "Loading alumni directory...")
        
        // Use cached data for instant loading
        DataCache.getAlumniDirectory({ alumni ->
            runOnUiThread {
                Log.d("AlumniDirectory", "Alumni loaded: ${alumni.size}")
                fullAlumniList = alumni
                if (fullAlumniList.isEmpty()) {
                    Toast.makeText(this@AlumniDirectoryActivity, "No alumni found in directory", Toast.LENGTH_SHORT).show()
                }
                adapter?.filterList(fullAlumniList)
            }
        })
    }

    private fun filterList() {
        if (adapter == null || fullAlumniList.isEmpty()) return

        var filteredList = fullAlumniList

        // Filter by search query
        val searchQuery = etSearch.text.toString().lowercase()
        if (searchQuery.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.name.lowercase().contains(searchQuery) ||
                it.company?.lowercase()?.contains(searchQuery) == true
            }
        }

        // Filter by department chip
        val checkedChipId = chipGroup.checkedChipId
        if (checkedChipId != -1 && checkedChipId != R.id.chipAll) {
            val selectedDepartment = when (checkedChipId) {
                R.id.chipCSE -> "CSE"
                R.id.chipIT -> "IT"
                R.id.chipECE -> "ECE"
                R.id.chipMECH -> "MECH"
                R.id.chipCIVIL -> "CIVIL"
                else -> ""
            }
            if (selectedDepartment.isNotEmpty()) {
                filteredList = filteredList.filter { it.department.equals(selectedDepartment, ignoreCase = true) }
            }
        }

        adapter?.filterList(filteredList)
    }
}