package com.simats.aluminimanagement

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Local storage for mentorship requests that can be used when network is unavailable
 */
object LocalMentorRequestStorage {
    
    private const val PREFS_NAME = "mentor_requests_prefs"
    private const val KEY_REQUESTS = "local_requests"
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveRequest(context: Context, request: MyMentorRequest) {
        val requests = getRequests(context).toMutableList()
        requests.add(0, request) // Add to beginning
        
        val json = Gson().toJson(requests)
        getPrefs(context).edit().putString(KEY_REQUESTS, json).apply()
    }
    
    fun getRequests(context: Context): List<MyMentorRequest> {
        val json = getPrefs(context).getString(KEY_REQUESTS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<MyMentorRequest>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun clearRequests(context: Context) {
        getPrefs(context).edit().remove(KEY_REQUESTS).apply()
    }
    
    fun getRequestsForStudent(context: Context, studentRollNo: String): List<MyMentorRequest> {
        // For local storage, we don't filter since all are for current student
        return getRequests(context)
    }
}
