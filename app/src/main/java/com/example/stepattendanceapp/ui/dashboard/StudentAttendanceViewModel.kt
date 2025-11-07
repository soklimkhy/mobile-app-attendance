package com.example.stepattendanceapp.ui.dashboard

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepattendanceapp.data.model.AttendanceRecord
import com.example.stepattendanceapp.data.remote.RetrofitClient
import kotlinx.coroutines.launch

data class StudentAttendanceUiState(
    val isLoading: Boolean = false,
    val records: List<AttendanceRecord> = emptyList(),
    val errorMessage: String? = null
)

class StudentAttendanceViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(StudentAttendanceUiState())
        private set

    private fun getContext(): Context = getApplication<Application>().applicationContext

    fun fetchAttendance(courseId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.getApiService(getContext()).getStudentAttendanceForCourse(courseId)

                if (response.isSuccessful && response.body() != null) {
                    uiState = uiState.copy(isLoading = false, records = response.body()!!)
                } else {
                    throw Exception("Failed to fetch attendance. Status: ${response.code()}")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}