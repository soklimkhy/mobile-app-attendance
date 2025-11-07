package com.example.stepattendanceapp.ui.teacher

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepattendanceapp.data.model.*
import com.example.stepattendanceapp.data.remote.RetrofitClient
import com.example.stepattendanceapp.ui.admin.AttendanceRecordsUiState
import com.example.stepattendanceapp.ui.admin.StudentAttendance
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TeacherAttendanceViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(AttendanceRecordsUiState())
        private set

    private fun getContext(): Context = getApplication<Application>().applicationContext

    fun fetchAttendanceData(courseId: String, scheduleId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                // --- THIS IS THE FIX ---
                // We call the endpoint for getting attendance by *scheduleId*,
                // which is 'getAttendanceBySchedule' in your ApiService.
                val recordsResponse = RetrofitClient.getApiService(getContext()).getAttendanceBySchedule(scheduleId)
                // --- END FIX ---

                if (!recordsResponse.isSuccessful) {
                    throw Exception("Failed to fetch attendance records. Status: ${recordsResponse.code()}")
                }

                val scheduleRecords = recordsResponse.body() ?: emptyList()

                // Convert the list to the format the UI expects
                val studentAttendanceList = scheduleRecords.map { record ->
                    StudentAttendance(
                        student = AdminUser(
                            id = record.studentId,
                            username = record.username,
                            fullName = record.fullname,
                            email = null,
                            role = null,
                            active = null,
                            twoFactorEnabled = null
                        ),
                        attendance = record
                    )
                }

                uiState = uiState.copy(
                    isLoading = false,
                    schedule = null, // We don't have schedule-specific details, so this is null
                    studentAttendanceList = studentAttendanceList
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    // This function is for the "Save Changes" (batch update) button.
    fun saveChanges(courseId: String, scheduleId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                // 1. Convert the UI state into the API request format
                val recordsToSave = uiState.studentAttendanceList.map {
                    BatchAttendanceRecord(
                        studentId = it.student.id!!,
                        status = it.attendance?.status ?: "ABSENT",
                        notes = it.attendance?.notes ?: "Set by teacher"
                    )
                }

                val request = BatchAttendanceRequest(
                    scheduleId = scheduleId,
                    attendanceRecords = recordsToSave
                )

                // 2. Call the new batch endpoint
                val response = RetrofitClient.getApiService(getContext()).updateBatchAttendance(courseId, request)

                if (response.isSuccessful) {
                    uiState = uiState.copy(isLoading = false, errorMessage = null)
                    // Re-fetch the data to confirm the save
                    fetchAttendanceData(courseId, scheduleId)
                } else {
                    throw Exception(parseApiError(response.errorBody()?.string()))
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message ?: "An unknown error occurred")
            }
        }
    }

    // This only updates the state in the app, it does not call the API.
    fun updateLocalStatus(studentId: String, newStatus: String) {
        val newList = uiState.studentAttendanceList.map {
            if (it.student.id == studentId) {
                // Update the status in the attendance record
                it.copy(attendance = it.attendance?.copy(status = newStatus))
            } else {
                it
            }
        }
        uiState = uiState.copy(studentAttendanceList = newList)
    }

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

    fun clearErrorMessage() {
        uiState = uiState.copy(errorMessage = null)
    }
}