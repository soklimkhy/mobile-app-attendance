package com.example.stepattendanceapp.ui.dashboard

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepattendanceapp.data.model.ApiErrorResponse
import com.example.stepattendanceapp.data.model.ChangePasswordRequest
import com.example.stepattendanceapp.data.model.TwoFaSetupResponse
import com.example.stepattendanceapp.data.model.TwoFaVerifyRequest
import com.example.stepattendanceapp.data.model.UpdateProfileRequest
import com.example.stepattendanceapp.data.model.UserDetail
import com.example.stepattendanceapp.data.remote.RetrofitClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false, // For fetching profile or updating profile
    val user: UserDetail? = null,
    val errorMessage: String? = null,
    val updateSuccess: Boolean = false,

    // State for password change card
    val isPasswordLoading: Boolean = false,
    val passwordSuccessMessage: String? = null,
    val passwordErrorMessage: String? = null,

    // State for 2FA
    val isTwoFaLoading: Boolean = false,
    val twoFaSetupInfo: TwoFaSetupResponse? = null,
    val twoFaErrorMessage: String? = null,
    val twoFaSuccessMessage: String? = null
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    // Helper to get context safely
    private fun getContext(): Context = getApplication<Application>().applicationContext

    fun fetchUserProfile() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, updateSuccess = false)
            try {
                val response = RetrofitClient.getApiService(getContext()).getProfile()
                if (response.isSuccessful && response.body() != null) {
                    uiState = uiState.copy(isLoading = false, user = response.body()!!.user)
                } else {
                    throw Exception("Failed to fetch profile: ${response.message()}")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message ?: "An error occurred")
            }
        }
    }

    fun updateProfile(
        fullName: String,
        email: String,
        phoneNumber: String,
        gender: String,
        dateOfBirth: String
    ) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null, updateSuccess = false)

            val request = UpdateProfileRequest(
                fullName = fullName,
                email = email,
                phoneNumber = phoneNumber,
                gender = gender,
                dateOfBirth = dateOfBirth
            )

            try {
                val response = RetrofitClient.getApiService(getContext()).updateProfile(request)

                if (response.isSuccessful && response.body() != null) {
                    uiState = uiState.copy(
                        isLoading = false,
                        user = response.body()!!.user,
                        updateSuccess = true
                    )
                } else {
                    val errorBody = response.errorBody()?.string()
                    throw Exception(parseApiError(errorBody))
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message ?: "An unknown error occurred")
            }
        }
    }

    fun changePassword(
        current: String,
        new: String,
        confirm: String
    ) {
        viewModelScope.launch {
            uiState = uiState.copy(isPasswordLoading = true, passwordErrorMessage = null, passwordSuccessMessage = null)

            val request = ChangePasswordRequest(
                currentPassword = current,
                newPassword = new,
                confirmPassword = confirm
            )

            try {
                val response = RetrofitClient.getApiService(getContext()).changePassword(request)

                if (response.isSuccessful && response.body() != null) {
                    uiState = uiState.copy(
                        isPasswordLoading = false,
                        passwordSuccessMessage = response.body()?.message ?: "Password updated!"
                    )
                } else {
                    val errorBody = response.errorBody()?.string()
                    throw Exception(parseApiError(errorBody))
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isPasswordLoading = false, passwordErrorMessage = e.message)
            }
        }
    }

    // --- 2FA Logic ---
    fun setupTwoFa() {
        viewModelScope.launch {
            uiState = uiState.copy(isTwoFaLoading = true, twoFaErrorMessage = null)
            try {
                val response = RetrofitClient.getApiService(getContext()).setupTwoFa()
                if (response.isSuccessful && response.body() != null) {
                    uiState = uiState.copy(isTwoFaLoading = false, twoFaSetupInfo = response.body())
                } else {
                    throw Exception("Failed to start 2FA setup")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isTwoFaLoading = false, twoFaErrorMessage = e.message)
            }
        }
    }

    fun verifyTwoFa(code: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isTwoFaLoading = true, twoFaErrorMessage = null)
            try {
                val response = RetrofitClient.getApiService(getContext()).verifyTwoFa(TwoFaVerifyRequest(code))
                if (response.isSuccessful && response.body() != null) {
                    uiState = uiState.copy(
                        isTwoFaLoading = false,
                        twoFaSuccessMessage = response.body()?.message ?: "2FA enabled!",
                        twoFaSetupInfo = null // Clear setup info, it's done
                    )
                    // Re-fetch user profile to show updated 2FA status (if your API changes it)
                    fetchUserProfile()
                } else {
                    val errorBody = response.errorBody()?.string()
                    throw Exception(parseApiError(errorBody))
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isTwoFaLoading = false, twoFaErrorMessage = e.message)
            }
        }
    }

    fun cancelTwoFaSetup() {
        uiState = uiState.copy(twoFaSetupInfo = null, twoFaErrorMessage = null)
    }

    private fun parseApiError(errorBody: String?): String {
        if (errorBody == null) return "Unknown error"
        return try {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(ApiErrorResponse::class.java)
            val errorResponse = adapter.fromJson(errorBody)

            if (errorResponse?.error != null) {
                return errorResponse.error
            }
            if (errorResponse?.errors != null && errorResponse.errors.isNotEmpty()) {
                return errorResponse.errors.joinToString(", ")
            }
            errorBody
        } catch (e: Exception) {
            errorBody
        }
    }

    // --- Message Clearing Functions ---
    fun clearErrorMessage() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun clearUpdateSuccess() {
        uiState = uiState.copy(updateSuccess = false)
    }

    fun clearPasswordSuccessMessage() {
        uiState = uiState.copy(passwordSuccessMessage = null)
    }
    fun clearPasswordErrorMessage() {
        uiState = uiState.copy(passwordErrorMessage = null)
    }

    fun clearTwoFaSuccessMessage() {
        uiState = uiState.copy(twoFaSuccessMessage = null)
    }
    fun clearTwoFaErrorMessage() {
        uiState = uiState.copy(twoFaErrorMessage = null)
    }
}