package com.example.stepattendanceapp.ui.admin

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepattendanceapp.data.model.AdminUser
import com.example.stepattendanceapp.data.model.UpdateRoleRequest
import com.example.stepattendanceapp.data.remote.RetrofitClient
import kotlinx.coroutines.launch

data class ManageUsersUiState(
    val isLoading: Boolean = false,
    val users: List<AdminUser> = emptyList(), // This will be the filtered list
    val errorMessage: String? = null,
    // NEW: State for search and filter
    val searchQuery: String = "",
    val selectedRole: String = "All" // "All", "ADMIN", "TEACHER", "STUDENT"
)

class ManageUsersViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(ManageUsersUiState())
        private set

    // This will hold the complete list from the API
    private var originalUserList: List<AdminUser> = emptyList()

    private fun getContext(): Context = getApplication<Application>().applicationContext

    fun fetchAllUsers() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.getApiService(getContext()).getAllUsers()
                if (response.isSuccessful && response.body() != null) {
                    originalUserList = response.body()!!
                    // Initially, the filtered list is the complete list
                    filterUsers()
                } else {
                    throw Exception("Failed to fetch users")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    // NEW: Called when the search text changes
    fun onSearchQueryChanged(query: String) {
        uiState = uiState.copy(searchQuery = query)
        filterUsers()
    }

    // NEW: Called when the role filter changes
    fun onRoleFilterChanged(role: String) {
        uiState = uiState.copy(selectedRole = role)
        filterUsers()
    }

    // NEW: Private function to apply filters
    private fun filterUsers() {
        var filtered = originalUserList

        // 1. Filter by Role
        if (uiState.selectedRole != "All") {
            filtered = filtered.filter { it.role == uiState.selectedRole }
        }

        // 2. Filter by Search Query
        if (uiState.searchQuery.isNotBlank()) {
            val query = uiState.searchQuery.lowercase().trim()
            filtered = filtered.filter {
                it.username?.lowercase()?.contains(query) == true ||
                        it.fullName?.lowercase()?.contains(query) == true ||
                        it.email?.lowercase()?.contains(query) == true ||
                        it.id?.lowercase()?.contains(query) == true
            }
        }

        // Update the UI state with the new filtered list
        uiState = uiState.copy(users = filtered, isLoading = false)
    }

    fun updateUserRole(userId: String, newRole: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val request = UpdateRoleRequest(role = newRole)
                val response = RetrofitClient.getApiService(getContext()).updateUserRole(userId, request)

                if (response.isSuccessful) {
                    // Success! Refresh the user list to show the change.
                    fetchAllUsers()
                } else {
                    throw Exception("Failed to update role")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}