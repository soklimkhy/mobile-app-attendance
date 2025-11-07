package com.example.stepattendanceapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import com.example.stepattendanceapp.ui.auth.RegisterScreen
import com.example.stepattendanceapp.ui.auth.RegisterViewModel
import com.example.stepattendanceapp.ui.theme.StepAttendanceAppTheme

class RegisterActivity : ComponentActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StepAttendanceAppTheme {
                val uiState = viewModel.uiState

                RegisterScreen(
                    uiState = uiState,
                    onRegisterClick = { username, password ->
                        // Pass the context to the updated ViewModel function
                        viewModel.registerAndLogin(username, password, applicationContext)
                    },
                    onNavigateToLogin = {
                        finish() // Go back to the Login screen
                    }
                )

                LaunchedEffect(uiState.registerSuccess) {
                    if (uiState.registerSuccess) {
                        Toast.makeText(this@RegisterActivity, "Registration Successful!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@RegisterActivity, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}