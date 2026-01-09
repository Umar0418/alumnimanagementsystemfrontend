package com.simats.aluminimanagement

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * In-memory cache for frequently accessed data to reduce API calls
 * and provide instant loading on subsequent visits.
 */
object DataCache {
    
    // Cache data with timestamps
    private var eventsCache: List<EventModel>? = null
    private var eventsCacheTime: Long = 0
    
    private var jobsCache: List<JobModel>? = null
    private var jobsCacheTime: Long = 0
    
    private var mentorsCache: List<MentorModel>? = null
    private var mentorsCacheTime: Long = 0
    
    private var announcementsCache: List<AnnouncementModel>? = null
    private var announcementsCacheTime: Long = 0
    
    private var alumniCache: List<AlumniDirectoryItem>? = null
    private var alumniCacheTime: Long = 0
    
    // Cache expiry time (5 minutes)
    private const val CACHE_EXPIRY_MS = 5 * 60 * 1000L
    
    // Events
    fun getEvents(callback: (List<EventModel>) -> Unit, forceRefresh: Boolean = false) {
        try {
            val now = System.currentTimeMillis()
            
            // Return cached data immediately if valid
            if (!forceRefresh && eventsCache != null && (now - eventsCacheTime) < CACHE_EXPIRY_MS) {
                callback(eventsCache!!)
                return
            }
            
            // Return cached data first if available (instant UI), then refresh
            if (eventsCache != null) {
                callback(eventsCache!!)
            }
            
            // Fetch fresh data
            ApiClient.instance.getEvents().enqueue(object : Callback<EventListResponse> {
                override fun onResponse(call: Call<EventListResponse>, response: Response<EventListResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        eventsCache = response.body()?.events ?: emptyList()
                        eventsCacheTime = System.currentTimeMillis()
                        callback(eventsCache!!)
                    } else if (eventsCache == null) {
                        // No cache available and API failed - return empty list
                        callback(emptyList())
                    }
                }
                override fun onFailure(call: Call<EventListResponse>, t: Throwable) {
                    if (eventsCache == null) {
                        callback(emptyList())
                    }
                }
            })
        } catch (e: Exception) {
            callback(eventsCache ?: emptyList())
        }
    }
    
    // Jobs
    fun getJobs(callback: (List<JobModel>) -> Unit, forceRefresh: Boolean = false) {
        try {
            val now = System.currentTimeMillis()
            
            if (!forceRefresh && jobsCache != null && (now - jobsCacheTime) < CACHE_EXPIRY_MS) {
                callback(jobsCache!!)
                return
            }
            
            if (jobsCache != null) {
                callback(jobsCache!!)
            }
            
            ApiClient.instance.getJobs().enqueue(object : Callback<JobListResponse> {
                override fun onResponse(call: Call<JobListResponse>, response: Response<JobListResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        jobsCache = response.body()?.jobs ?: emptyList()
                        jobsCacheTime = System.currentTimeMillis()
                        callback(jobsCache!!)
                    } else if (jobsCache == null) {
                        callback(emptyList())
                    }
                }
                override fun onFailure(call: Call<JobListResponse>, t: Throwable) {
                    if (jobsCache == null) {
                        callback(emptyList())
                    }
                }
            })
        } catch (e: Exception) {
            callback(jobsCache ?: emptyList())
        }
    }
    
    // Mentors
    fun getMentors(callback: (List<MentorModel>) -> Unit, forceRefresh: Boolean = false) {
        try {
            val now = System.currentTimeMillis()
            
            if (!forceRefresh && mentorsCache != null && (now - mentorsCacheTime) < CACHE_EXPIRY_MS) {
                callback(mentorsCache!!)
                return
            }
            
            if (mentorsCache != null) {
                callback(mentorsCache!!)
            }
            
            ApiClient.instance.getAvailableMentors().enqueue(object : Callback<MentorListResponse> {
                override fun onResponse(call: Call<MentorListResponse>, response: Response<MentorListResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        mentorsCache = response.body()?.getMentorList() ?: emptyList()
                        mentorsCacheTime = System.currentTimeMillis()
                        callback(mentorsCache!!)
                    } else if (mentorsCache == null) {
                        callback(emptyList())
                    }
                }
                override fun onFailure(call: Call<MentorListResponse>, t: Throwable) {
                    if (mentorsCache == null) {
                        callback(emptyList())
                    }
                }
            })
        } catch (e: Exception) {
            callback(mentorsCache ?: emptyList())
        }
    }
    
    // Announcements
    fun getAnnouncements(userType: String?, callback: (List<AnnouncementModel>) -> Unit, forceRefresh: Boolean = false) {
        try {
            val now = System.currentTimeMillis()
            
            if (!forceRefresh && announcementsCache != null && (now - announcementsCacheTime) < CACHE_EXPIRY_MS) {
                callback(announcementsCache!!)
                return
            }
            
            if (announcementsCache != null) {
                callback(announcementsCache!!)
            }
            
            ApiClient.instance.getAnnouncements(userType).enqueue(object : Callback<AnnouncementListResponse> {
                override fun onResponse(call: Call<AnnouncementListResponse>, response: Response<AnnouncementListResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        announcementsCache = response.body()?.announcements ?: emptyList()
                        announcementsCacheTime = System.currentTimeMillis()
                        callback(announcementsCache!!)
                    } else if (announcementsCache == null) {
                        callback(emptyList())
                    }
                }
                override fun onFailure(call: Call<AnnouncementListResponse>, t: Throwable) {
                    if (announcementsCache == null) {
                        callback(emptyList())
                    }
                }
            })
        } catch (e: Exception) {
            callback(announcementsCache ?: emptyList())
        }
    }
    
    // Alumni Directory
    fun getAlumniDirectory(callback: (List<AlumniDirectoryItem>) -> Unit, forceRefresh: Boolean = false) {
        try {
            val now = System.currentTimeMillis()
            
            if (!forceRefresh && alumniCache != null && (now - alumniCacheTime) < CACHE_EXPIRY_MS) {
                callback(alumniCache!!)
                return
            }
            
            if (alumniCache != null) {
                callback(alumniCache!!)
            }
            
            ApiClient.instance.getAlumniDirectory().enqueue(object : Callback<AlumniDirectoryResponse> {
                override fun onResponse(call: Call<AlumniDirectoryResponse>, response: Response<AlumniDirectoryResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        alumniCache = response.body()?.alumni ?: emptyList()
                        alumniCacheTime = System.currentTimeMillis()
                        callback(alumniCache!!)
                    } else if (alumniCache == null) {
                        callback(emptyList())
                    }
                }
                override fun onFailure(call: Call<AlumniDirectoryResponse>, t: Throwable) {
                    if (alumniCache == null) {
                        callback(emptyList())
                    }
                }
            })
        } catch (e: Exception) {
            callback(alumniCache ?: emptyList())
        }
    }
    
    // Preload for specific user types (safe version - catches all exceptions)
    fun preloadForStudent() {
        try {
            getEvents({}, false)
            getJobs({}, false)
            getMentors({}, false)
            getAnnouncements("student", {}, false)
        } catch (e: Exception) {
            // Silent fail on preload
        }
    }
    
    fun preloadForAlumni() {
        try {
            getEvents({}, false)
            getJobs({}, false)
            getAnnouncements("alumni", {}, false)
            getAlumniDirectory({}, false)
        } catch (e: Exception) {
            // Silent fail on preload
        }
    }
    
    fun preloadForAdmin() {
        try {
            getEvents({}, false)
            getJobs({}, false)
            getAnnouncements(null, {}, false)
            getMentors({}, false)
        } catch (e: Exception) {
            // Silent fail on preload
        }
    }
    
    // Clear all cache
    fun clearAll() {
        eventsCache = null
        jobsCache = null
        mentorsCache = null
        announcementsCache = null
        alumniCache = null
    }
}
