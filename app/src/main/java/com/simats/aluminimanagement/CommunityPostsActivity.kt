package com.simats.aluminimanagement

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityPostsActivity : AppCompatActivity(), CommunityPostActionListener {

    private lateinit var recyclerPosts: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var adapter: CommunityPostAdapter
    private var communityId: Int = 0
    private var communityName: String = "Community"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_posts)

        // Get community info from intent
        communityId = intent.getIntExtra("community_id", 0)
        communityName = intent.getStringExtra("community_name") ?: "Batch 2024 Community"

        // Setup views
        recyclerPosts = findViewById(R.id.recyclerPosts)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)

        findViewById<TextView>(R.id.tvCommunityTitle).text = communityName
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        // Setup RecyclerView
        recyclerPosts.layoutManager = LinearLayoutManager(this)
        adapter = CommunityPostAdapter(mutableListOf(), this)
        recyclerPosts.adapter = adapter

        // New Post FAB
        findViewById<ExtendedFloatingActionButton>(R.id.fabNewPost).setOnClickListener {
            showNewPostDialog()
        }

        // Setup bottom navigation
        setupBottomNavigation()

        // Load posts
        loadPosts()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
        
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, StudentDashboardActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                    true
                }
                R.id.nav_alumni -> {
                    startActivity(Intent(this, AlumniDirectoryActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.nav_jobs -> {
                    startActivity(Intent(this, StudentJobsActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.nav_events -> {
                    startActivity(Intent(this, AlumniEventsActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Profile coming soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadPosts() {
        progressBar.visibility = View.VISIBLE
        recyclerPosts.visibility = View.GONE
        emptyState.visibility = View.GONE

        val sessionManager = SessionManager.getInstance(this)
        val rollNo = sessionManager.getRollNo() ?: ""

        ApiClient.instance.getCommunityPosts(communityId, rollNo).enqueue(object : Callback<CommunityPostsResponse> {
            override fun onResponse(call: Call<CommunityPostsResponse>, response: Response<CommunityPostsResponse>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.status == true) {
                    val posts = response.body()?.posts ?: emptyList()

                    if (posts.isEmpty()) {
                        emptyState.visibility = View.VISIBLE
                        recyclerPosts.visibility = View.GONE
                    } else {
                        emptyState.visibility = View.GONE
                        recyclerPosts.visibility = View.VISIBLE
                        adapter.updateList(posts)
                    }
                } else {
                    emptyState.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<CommunityPostsResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                emptyState.visibility = View.VISIBLE
                Toast.makeText(this@CommunityPostsActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showNewPostDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_new_post, null)
        val etPostContent = dialogView.findViewById<EditText>(R.id.etPostContent)

        AlertDialog.Builder(this)
            .setTitle("New Post")
            .setView(dialogView)
            .setPositiveButton("Post") { _, _ ->
                val content = etPostContent.text.toString().trim()
                if (content.isNotEmpty()) {
                    createPost(content)
                } else {
                    Toast.makeText(this, "Please write something", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createPost(content: String) {
        val sessionManager = SessionManager.getInstance(this)
        val rollNo = sessionManager.getRollNo() ?: ""

        ApiClient.instance.createCommunityPost(communityId, rollNo, content)
            .enqueue(object : Callback<CreatePostResponse> {
                override fun onResponse(call: Call<CreatePostResponse>, response: Response<CreatePostResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        Toast.makeText(this@CommunityPostsActivity, "Post created!", Toast.LENGTH_SHORT).show()
                        loadPosts() // Refresh the list
                    } else {
                        Toast.makeText(this@CommunityPostsActivity, 
                            response.body()?.message ?: "Failed to create post", 
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CreatePostResponse>, t: Throwable) {
                    Toast.makeText(this@CommunityPostsActivity, "Network Error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onLikePost(post: CommunityPost, position: Int) {
        val sessionManager = SessionManager.getInstance(this)
        val rollNo = sessionManager.getRollNo() ?: ""

        ApiClient.instance.likePost(post.id, rollNo).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    adapter.updateLike(position, !post.isLiked, post.likesCount + if (post.isLiked) -1 else 1)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@CommunityPostsActivity, "Failed to like post", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCommentPost(post: CommunityPost) {
        Toast.makeText(this, "Comments coming soon!", Toast.LENGTH_SHORT).show()
    }

    override fun onSharePost(post: CommunityPost) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "${post.userName} shared:\n\n${post.content}")
        startActivity(Intent.createChooser(shareIntent, "Share Post"))
    }
}
