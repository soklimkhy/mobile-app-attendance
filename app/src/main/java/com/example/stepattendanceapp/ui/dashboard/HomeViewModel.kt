package com.example.stepattendanceapp.ui.dashboard

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepattendanceapp.data.model.CourseDetail
import com.example.stepattendanceapp.data.remote.RetrofitClient
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val enrolledCourses: List<CourseDetail> = emptyList(),
    // Add summary data here when the API is ready
    // val attendanceSummary: AttendanceSummary? = null,
    val errorMessage: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(HomeUiState())
        private set

    private fun getContext(): Context = getApplication<Application>().applicationContext

    init {
        fetchDashboardData()
    }

    fun fetchDashboardData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                // We only fetch enrolled courses for now
                val courseResponse = RetrofitClient.getApiService(getContext()).getEnrolledCourses()

                if (courseResponse.isSuccessful && courseResponse.body() != null) {
                    uiState = uiState.copy(
                        isLoading = false,
                        enrolledCourses = courseResponse.body()!!
                    )
                } else {
                    throw Exception("Failed to fetch dashboard data")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}