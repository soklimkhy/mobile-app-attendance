package com.example.stepattendanceapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import com.example.stepattendanceapp.data.local.SessionManager
import com.example.stepattendanceapp.ui.auth.LoginScreen
import com.example.stepattendanceapp.ui.auth.LoginViewModel
import com.example.stepattendanceapp.ui.theme.StepAttendanceAppTheme

class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StepAttendanceAppTheme {
                val uiState = viewModel.uiState

                LoginScreen(
                    uiState = uiState,
                    onLoginClick = { username, password ->
                        viewModel.login(username, password)
                    },
                    onVerifyClick = { otp ->
                        viewModel.verifyMfa(otp)
                    },
                    onCancelMfa = {
                        viewModel.cancelMfa()
                    },
                    onNavigateToRegister = {
                        val intent = Intent(this, RegisterActivity::class.java)
                        startActivity(intent)
                    }
                )

                // --- UPDATED: Role-based Navigation ---
                LaunchedEffect(uiState.loginSuccess) {
                    if (uiState.loginSuccess) {
                        Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()

                        val sessionManager = SessionManager(applicationContext)
                        val role = sessionManager.fetchUserRole()

                        // Use a when-expression to route based on role
                        val intent = when (role) {
                            "ADMIN" -> Intent(this@LoginActivity, AdminDashboardActivity::class.java)
                            "TEACHER" -> Intent(this@LoginActivity, TeacherDashboardActivity::class.java)
                            else -> Intent(this@LoginActivity, DashboardActivity::class.java) // Student/Default
                        }

                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}