package com.example.stepattendanceapp.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val USER_TOKEN = "user_token"
        private const val USER_FULL_NAME = "user_full_name"
        private const val USER_ROLE = "user_role" // <-- ADD THIS
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(USER_TOKEN, token).apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveFullName(name: String) {
        prefs.edit().putString(USER_FULL_NAME, name).apply()
    }

    fun fetchFullName(): String? {
        return prefs.getString(USER_FULL_NAME, null)
    }

    // --- ADD THESE TWO FUNCTIONS ---
    fun saveUserRole(role: String) {
        prefs.edit().putString(USER_ROLE, role).apply()
    }

    fun fetchUserRole(): String? {
        return prefs.getString(USER_ROLE, null)
    }

    // --- UPDATE clearSession ---
    fun clearSession() {
        prefs.edit()
            .remove(USER_TOKEN)
            .remove(USER_FULL_NAME)
            .remove(USER_ROLE) // <-- ADD THIS
            .apply()
    }
}