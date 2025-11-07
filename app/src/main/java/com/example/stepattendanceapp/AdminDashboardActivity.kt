package com.example.stepattendanceapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.stepattendanceapp.data.local.SessionManager
import com.example.stepattendanceapp.ui.admin.AdminMainLayout
import com.example.stepattendanceapp.ui.theme.StepAttendanceAppTheme

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(applicationContext)
        val userName = sessionManager.fetchFullName() ?: "Admin"

        setContent {
            StepAttendanceAppTheme {
                AdminMainLayout(
                    fullName = userName,
                    onLogoutClick = {
                        sessionManager.clearSession()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}