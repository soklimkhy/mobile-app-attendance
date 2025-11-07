package com.example.stepattendanceapp.ui.auth

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepattendanceapp.data.local.SessionManager
import com.example.stepattendanceapp.data.model.AuthRequest
import com.example.stepattendanceapp.data.remote.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registerSuccess: Boolean = false
)

class RegisterViewModel : ViewModel() {

    var uiState by mutableStateOf(RegisterUiState())
        private set

    fun registerAndLogin(username: String, password: String, context: Context) {
        if (username.isBlank() || password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Username and password cannot be empty.")
            return
        }
        if (password.length < 6) {
            uiState = uiState.copy(errorMessage = "Password must be at least 6 characters long.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val request = AuthRequest(username, password)
                // CORRECTED: Use getApiService(context)
                val registerResponse = RetrofitClient.getApiService(context).register(request)

                if (registerResponse.isSuccessful) {
                    delay(500) // Keep the small delay for database sync

                    // CORRECTED: Use getApiService(context) again for the login call
                    val loginResponse = RetrofitClient.getApiService(context).login(request)

                    if (loginResponse.isSuccessful && loginResponse.body() != null) {
                        val authResponse = loginResponse.body()!!

                        if (authResponse.error != null) {
                            throw Exception(authResponse.error)
                        }

                        if (authResponse.accessToken != null) {
                            val sessionManager = SessionManager(context)
                            sessionManager.saveAuthToken(authResponse.accessToken)

                            authResponse.user?.fullName?.let {
                                sessionManager.saveFullName(it)
                            }

                            uiState = uiState.copy(isLoading = false, registerSuccess = true)
                        } else {
                            throw Exception("Auto-login failed: The server did not return an access token.")
                        }
                    } else {
                        val errorBody = loginResponse.errorBody()?.string() ?: "Auto-login failed"
                        throw Exception(errorBody)
                    }
                } else {
                    val errorBody = registerResponse.errorBody()?.string() ?: "Registration failed"
                    throw Exception(errorBody)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message ?: "An unknown error occurred.")
            }
        }
    }

    fun clearErrorMessage() {
        uiState = uiState.copy(errorMessage = null)
    }
}