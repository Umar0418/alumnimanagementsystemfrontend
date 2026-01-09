package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityGroupsActivity : AppCompatActivity(), CommunityGroupListener {

    private lateinit var recyclerCommunities: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var etSearch: EditText
    private var adapter: CommunityGroupAdapter? = null
    private var allCommunities = mutableListOf<CommunityModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_groups)

        // Setup views
        recyclerCommunities = findViewById(R.id.recyclerCommunities)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)
        etSearch = findViewById(R.id.etSearch)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
        
        // Add community button
        findViewById<ImageView>(R.id.ivAddCommunity).setOnClickListener {
            showCreateCommunityDialog()
        }

        // Setup RecyclerView
        recyclerCommunities.layoutManager = LinearLayoutManager(this)
        adapter = CommunityGroupAdapter(mutableListOf(), this)
        recyclerCommunities.adapter = adapter

        // Search functionality
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCommunities(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Load communities
        loadCommunities()
    }

    private fun loadCommunities() {
        progressBar.visibility = View.VISIBLE
        recyclerCommunities.visibility = View.GONE
        emptyState.visibility = View.GONE

        val sessionManager = SessionManager.getInstance(this)
        val rollNo = sessionManager.getRollNo() ?: ""

        ApiClient.instance.getCommunities(rollNo).enqueue(object : Callback<CommunityListResponse> {
            override fun onResponse(call: Call<CommunityListResponse>, response: Response<CommunityListResponse>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.status == true) {
                    allCommunities.clear()
                    allCommunities.addAll(response.body()?.communities ?: emptyList())

                    if (allCommunities.isEmpty()) {
                        emptyState.visibility = View.VISIBLE
                        recyclerCommunities.visibility = View.GONE
                    } else {
                        emptyState.visibility = View.GONE
                        recyclerCommunities.visibility = View.VISIBLE
                        adapter?.updateList(allCommunities)
                    }
                } else {
                    emptyState.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<CommunityListResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                emptyState.visibility = View.VISIBLE
                Toast.makeText(this@CommunityGroupsActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterCommunities(query: String) {
        if (query.isEmpty()) {
            adapter?.updateList(allCommunities)
        } else {
            val filtered = allCommunities.filter {
                it.name.lowercase().contains(query.lowercase())
            }
            adapter?.updateList(filtered)
        }
    }

    private fun showCreateCommunityDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_community, null)
        val etName = dialogView.findViewById<EditText>(R.id.etCommunityName)
        val etDescription = dialogView.findViewById<EditText>(R.id.etCommunityDescription)

        AlertDialog.Builder(this)
            .setTitle("Create Community")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val name = etName.text.toString().trim()
                val description = etDescription.text.toString().trim()

                if (name.isNotEmpty()) {
                    createCommunity(name, description)
                } else {
                    Toast.makeText(this, "Please enter a community name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createCommunity(name: String, description: String) {
        val sessionManager = SessionManager.getInstance(this)
        val rollNo = sessionManager.getRollNo() ?: ""

        ApiClient.instance.createCommunity(rollNo, name, description)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(this@CommunityGroupsActivity, "Community created!", Toast.LENGTH_SHORT).show()
                        loadCommunities() // Refresh list
                    } else {
                        Toast.makeText(this@CommunityGroupsActivity, 
                            response.body()?.message ?: "Failed to create community", 
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@CommunityGroupsActivity, "Network Error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onJoinCommunity(community: CommunityModel) {
        val sessionManager = SessionManager.getInstance(this)
        val rollNo = sessionManager.getRollNo() ?: ""

        ApiClient.instance.joinCommunity(rollNo, community.id)
            .enqueue(object : Callback<JoinCommunityResponse> {
                override fun onResponse(call: Call<JoinCommunityResponse>, response: Response<JoinCommunityResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(this@CommunityGroupsActivity, 
                            "Joined ${community.name}!", Toast.LENGTH_SHORT).show()
                        adapter?.updateMembership(community.id, true)
                        
                        // Open community posts
                        onOpenCommunity(community.copy(isMember = true))
                    } else {
                        Toast.makeText(this@CommunityGroupsActivity, 
                            response.body()?.message ?: "Failed to join", 
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<JoinCommunityResponse>, t: Throwable) {
                    Toast.makeText(this@CommunityGroupsActivity, "Network Error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onOpenCommunity(community: CommunityModel) {
        val intent = Intent(this, CommunityPostsActivity::class.java)
        intent.putExtra("community_id", community.id)
        intent.putExtra("community_name", community.name)
        startActivity(intent)
    }
}
