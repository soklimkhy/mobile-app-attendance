package com.example.stepattendanceapp.ui.dashboard

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepattendanceapp.data.model.Schedule
import com.example.stepattendanceapp.data.remote.RetrofitClient
import kotlinx.coroutines.launch

data class StudentScheduleUiState(
    val isLoading: Boolean = false,
    val schedules: List<Schedule> = emptyList(),
    val errorMessage: String? = null
)

class StudentScheduleViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(StudentScheduleUiState())
        private set

    private fun getContext(): Context = getApplication<Application>().applicationContext

    fun fetchSchedules(courseId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.getApiService(getContext()).getSchedulesForStudentCourse(courseId)

                if (response.isSuccessful && response.body() != null) {
                    uiState = uiState.copy(isLoading = false, schedules = response.body()!!)
                } else {
                    throw Exception("Failed to fetch schedules. Status: ${response.code()}")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}