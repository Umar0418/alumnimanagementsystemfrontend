package com.simats.aluminimanagement

import android.app.Application

// This class is the first thing that runs when your app starts.
class AlumniManagementApp : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize the ApiClient here. This guarantees it is ready before any activity starts.
        ApiClient.init(this)
    }
}
