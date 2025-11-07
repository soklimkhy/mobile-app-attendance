package com.example.stepattendanceapp.ui.admin

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepattendanceapp.data.model.NotesRequest
import com.example.stepattendanceapp.data.model.Schedule
import com.example.stepattendanceapp.data.model.ScheduleRequest
import com.example.stepattendanceapp.data.model.UpdateScheduleRequest
import com.example.stepattendanceapp.data.remote.RetrofitClient
import kotlinx.coroutines.launch

data class ManageSchedulesUiState(
    val isLoading: Boolean = false,
    val schedules: List<Schedule> = emptyList(),
    val errorMessage: String? = null
)

class ManageSchedulesViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(ManageSchedulesUiState())
        private set

    private fun getContext(): Context = getApplication<Application>().applicationContext

    fun fetchSchedules(courseId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.getApiService(getContext()).getSchedulesForCourse(courseId)
                if (response.isSuccessful && response.body() != null) {
                    uiState = uiState.copy(isLoading = false, schedules = response.body()!!)
                } else {
                    throw Exception("Failed to fetch schedules")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    // --- ALL FUNCTIONS BELOW ARE CORRECTED ---

    fun createSchedule(courseId: String, request: ScheduleRequest, onComplete: () -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.getApiService(getContext()).createSchedule(courseId, request)
                if (response.isSuccessful) {
                    onComplete()
                    fetchSchedules(courseId) // Refresh list (this will set isLoading = false)
                } else {
                    throw Exception("Failed to create schedule")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun updateSchedule(courseId: String, scheduleId: String, request: UpdateScheduleRequest, onComplete: () -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.getApiService(getContext()).updateSchedule(scheduleId, request)
                if (response.isSuccessful) {
                    onComplete()
                    fetchSchedules(courseId) // Refresh list
                } else {
                    throw Exception("Failed to update schedule")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun deleteSchedule(courseId: String, scheduleId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.getApiService(getContext()).deleteSchedule(scheduleId)
                if (response.isSuccessful) {
                    fetchSchedules(courseId) // Refresh list
                } else {
                    throw Exception("Failed to delete schedule")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun cancelSchedule(courseId: String, scheduleId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val request = NotesRequest(notes = "Canceled by admin")
                val response = RetrofitClient.getApiService(getContext()).cancelSchedule(scheduleId, request)
                if (response.isSuccessful) {
                    fetchSchedules(courseId) // Refresh list
                } else {
                    throw Exception("Failed to cancel schedule")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun completeSchedule(courseId: String, scheduleId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val request = NotesRequest(notes = "Completed by admin")
                val response = RetrofitClient.getApiService(getContext()).completeSchedule(scheduleId, request)
                if (response.isSuccessful) {
                    fetchSchedules(courseId) // Refresh list
                } else {
                    throw Exception("Failed to complete schedule")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}