package com.simats.aluminimanagement

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class SessionManager private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREF_NAME = "AlumniSession"
        private const val KEY_USER_JSON = "user_json"
        private const val KEY_IS_SUBSCRIBED = "is_subscribed"
        private const val KEY_SUBSCRIPTION_EXPIRY = "subscription_expiry"

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager = INSTANCE ?: synchronized(this) {
            INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
        }
    }

    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        prefs.edit().putString(KEY_USER_JSON, userJson).apply()
    }

    fun getUser(): User? {
        val userJson = prefs.getString(KEY_USER_JSON, null)
        return if (userJson != null) {
            try {
                gson.fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    // CORRECTED: Added the missing getUserType function
    fun getUserType(): String? {
        return getUser()?.userType
    }

    fun getRollNo(): String? {
        return getUser()?.rollNo
    }

    fun isLoggedIn(): Boolean {
        return prefs.contains(KEY_USER_JSON)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    // Subscription methods
    fun saveSubscriptionStatus(isSubscribed: Boolean) {
        prefs.edit().putBoolean(KEY_IS_SUBSCRIBED, isSubscribed).apply()
        if (isSubscribed) {
            // Set expiry to 1 year from now
            val expiryTime = System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)
            prefs.edit().putLong(KEY_SUBSCRIPTION_EXPIRY, expiryTime).apply()
        }
    }

    fun isSubscribed(): Boolean {
        val isSubscribed = prefs.getBoolean(KEY_IS_SUBSCRIBED, false)
        if (isSubscribed) {
            // Check if subscription has expired
            val expiryTime = prefs.getLong(KEY_SUBSCRIPTION_EXPIRY, 0L)
            if (System.currentTimeMillis() > expiryTime) {
                // Subscription expired
                saveSubscriptionStatus(false)
                return false
            }
        }
        return isSubscribed
    }

    fun getSubscriptionExpiry(): Long {
        return prefs.getLong(KEY_SUBSCRIPTION_EXPIRY, 0L)
    }
}