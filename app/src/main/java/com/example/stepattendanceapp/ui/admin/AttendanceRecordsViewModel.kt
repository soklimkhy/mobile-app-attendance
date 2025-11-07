package com.example.stepattendanceapp.ui.admin

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepattendanceapp.data.model.*
import com.example.stepattendanceapp.data.remote.RetrofitClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Wrapper class to hold the merged data
data class StudentAttendance(
    val student: AdminUser,
    val attendance: AttendanceRecord?
)

data class AttendanceRecordsUiState(
    val isLoading: Boolean = false,
    val schedule: Schedule? = null,
    val studentAttendanceList: List<StudentAttendance> = emptyList(),
    val savingStudentIds: Set<String> = emptySet(), // Tracks saving for individual rows
    val errorMessage: String? = null,
    val twoFaSuccessMessage: String? = null // Generic success message
)

class AttendanceRecordsViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(AttendanceRecordsUiState())
        private set

    private fun getContext(): Context = getApplication<Application>().applicationContext

    fun fetchAttendanceData(courseId: String, scheduleId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                // 1. Fetch the schedule details
                val scheduleResponse = RetrofitClient.getApiService(getContext()).getScheduleById(scheduleId)
                if (!scheduleResponse.isSuccessful) throw Exception("Failed to fetch schedule details")
                val schedule = scheduleResponse.body()

                // 2. Fetch the full student roster
                val studentsResponse = RetrofitClient.getApiService(getContext()).getStudentsForCourse(courseId)
                if (!studentsResponse.isSuccessful) throw Exception("Failed to fetch students")
                val studentList = studentsResponse.body()?.students ?: emptyList()

                // 3. Fetch the existing attendance records
                val recordsResponse = RetrofitClient.getApiService(getContext()).getAttendanceBySchedule(scheduleId)
                if (!recordsResponse.isSuccessful) throw Exception("Failed to fetch records")

                // --- THIS IS THE FIX ---
                // Safely unwrap the nullable list before calling .associateBy
                val recordsMap = (recordsResponse.body() ?: emptyList()).associateBy { it.studentId }
                // --- END FIX ---

                // 4. Merge the lists
                val mergedList = studentList.map { student ->
                    StudentAttendance(
                        student = student,
                        attendance = recordsMap[student.id]
                    )
                }

                uiState = uiState.copy(
                    isLoading = false,
                    schedule = schedule,
                    studentAttendanceList = mergedList
                )

            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun setAttendanceStatus(
        courseId: String,
        scheduleId: String,
        studentId: String,
        status: String,
        existingAttendance: AttendanceRecord?
    ) {
        viewModelScope.launch {
            uiState = uiState.copy(savingStudentIds = uiState.savingStudentIds + studentId, errorMessage = null)
            try {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val time = System.currentTimeMillis()

                val request = AttendanceRequest(
                    scheduleId = scheduleId,
                    courseId = courseId,
                    studentId = studentId,
                    date = date,
                    status = status,
                    time = time,
                    notes = "Set by admin"
                )

                val response = if (existingAttendance != null) {
                    RetrofitClient.getApiService(getContext()).updateAttendance(existingAttendance.id, request)
                } else {
                    RetrofitClient.getApiService(getContext()).saveAttendance(request)
                }

                if (response.isSuccessful && response.body() != null) {
                    val updatedRecord = response.body()
                    val newList = uiState.studentAttendanceList.map {
                        if (it.student.id == studentId) it.copy(attendance = updatedRecord) else it
                    }
                    uiState = uiState.copy(
                        studentAttendanceList = newList,
                        savingStudentIds = uiState.savingStudentIds - studentId
                    )
                } else {
                    throw Exception(parseApiError(response.errorBody()?.string()))
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    savingStudentIds = uiState.savingStudentIds - studentId,
                    errorMessage = e.message ?: "An unknown error occurred"
                )
            }
        }
    }

    fun createAttendance(scheduleId: String, courseId: String, studentId: String, status: String, notes: String?) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val time = System.currentTimeMillis()

                val request = AttendanceRequest(
                    scheduleId = scheduleId,
                    courseId = courseId,
                    studentId = studentId,
                    date = date,
                    status = status,
                    time = time,
                    notes = notes
                )

                val response = RetrofitClient.getApiService(getContext()).saveAttendance(request)
                if (response.isSuccessful) {
                    fetchAttendanceData(courseId, scheduleId)
                } else {
                    val errorBody = response.errorBody()?.string()
                    throw Exception(parseApiError(errorBody))
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
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

    fun clearTwoFaSuccessMessage() {
        uiState = uiState.copy(twoFaSuccessMessage = null)
    }
}