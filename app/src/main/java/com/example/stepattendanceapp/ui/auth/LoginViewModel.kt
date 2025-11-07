package com.example.stepattendanceapp.ui.auth

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepattendanceapp.data.local.SessionManager
import com.example.stepattendanceapp.data.model.ApiErrorResponse
import com.example.stepattendanceapp.data.model.AuthRequest
import com.example.stepattendanceapp.data.model.AuthResponse
import com.example.stepattendanceapp.data.remote.RetrofitClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,
    val requiresMfa: Boolean = false // State to show/hide the OTP field
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(LoginUiState())
        private set

    // Store credentials temporarily for the 2FA step
    private var tempUsername by mutableStateOf("")
    private var tempPassword by mutableStateOf("")

    private fun getContext(): Context = getApplication<Application>().applicationContext

    // This is the first step of logging in
    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Username and password cannot be empty.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, requiresMfa = false)

            // Store credentials in case MFA is needed
            tempUsername = username
            tempPassword = password

            val request = AuthRequest(username, password) // No OTP yet

            try {
                val response = RetrofitClient.getApiService(getContext()).login(request)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!

                    // --- 2FA LOGIC ---
                    if (authResponse.message == "MFA required") {
                        // Show the OTP input screen
                        uiState = uiState.copy(isLoading = false, requiresMfa = true)
                    }
                    // --- Regular Login Success ---
                    else if (authResponse.accessToken != null) {
                        saveSession(authResponse)
                        uiState = uiState.copy(isLoading = false, loginSuccess = true)
                    } else {
                        throw Exception("Login failed: Unknown response from server.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    throw Exception(parseApiError(errorBody))
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message ?: "An unknown error occurred.")
            }
        }
    }

    // This is the second step, called after the user enters the OTP
    fun verifyMfa(otp: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            // Send the request again, this time WITH the OTP
            val request = AuthRequest(tempUsername, tempPassword, otp)

            try {
                val response = RetrofitClient.getApiService(getContext()).login(request)

                if (response.isSuccessful && response.body()?.accessToken != null) {
                    // 2FA successful, save session and proceed
                    saveSession(response.body()!!)
                    uiState = uiState.copy(isLoading = false, loginSuccess = true, requiresMfa = false)
                } else {
                    // Handle "Invalid verification code"
                    val errorBody = response.errorBody()?.string()
                    throw Exception(parseApiError(errorBody))
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    // Helper to go back from the MFA screen
    fun cancelMfa() {
        uiState = uiState.copy(requiresMfa = false, errorMessage = null)
        tempUsername = ""
        tempPassword = ""
    }

    // Helper to save token, name, and role
    private fun saveSession(authResponse: AuthResponse) {
        val sessionManager = SessionManager(getContext())
        sessionManager.saveAuthToken(authResponse.accessToken!!)

        authResponse.user?.fullName?.let {
            sessionManager.saveFullName(it)
        }

        authResponse.user?.role?.let {
            sessionManager.saveUserRole(it)
        }
    }

    // Helper to parse API errors
    private fun parseApiError(errorBody: String?): String {
        if (errorBody == null) return "Unknown error"
        return try {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(ApiErrorResponse::class.java)
            val errorResponse = adapter.fromJson(errorBody)

            if (errorResponse?.error != null) return errorResponse.error
            if (errorResponse?.errors != null && errorResponse.errors.isNotEmpty()) {
                return errorResponse.errors.joinToString(", ")
            }
            errorBody
        } catch (e: Exception) {
            errorBody
        }
    }
}