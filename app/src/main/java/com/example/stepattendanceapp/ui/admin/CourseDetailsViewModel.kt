package com.example.stepattendanceapp.ui.admin

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepattendanceapp.data.model.AdminUser
import com.example.stepattendanceapp.data.model.StudentIdRequest
import com.example.stepattendanceapp.data.remote.RetrofitClient
import kotlinx.coroutines.launch

data class CourseDetailsUiState(
    val isLoading: Boolean = false,
    val students: List<AdminUser> = emptyList(),
    val errorMessage: String? = null
)

class CourseDetailsViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(CourseDetailsUiState())
        private set

    private fun getContext(): Context = getApplication<Application>().applicationContext

    fun fetchStudents(courseId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.getApiService(getContext()).getStudentsForCourse(courseId)
                if (response.isSuccessful && response.body() != null) {
                    // --- UPDATED THIS LINE ---
                    // Access the list via the .students property
                    uiState = uiState.copy(isLoading = false, students = response.body()!!.students)
                } else {
                    throw Exception("Failed to fetch students")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun addStudent(courseId: String, studentId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val request = StudentIdRequest(studentIds = listOf(studentId))
                // Your API uses POST to add, so we call that
                val response = RetrofitClient.getApiService(getContext()).addStudentsToCourse(courseId, request)
                if (response.isSuccessful) {
                    fetchStudents(courseId) // Refresh the list
                } else {
                    throw Exception("Failed to add student")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun removeStudent(courseId: String, studentId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.getApiService(getContext()).removeStudentFromCourse(courseId, studentId)
                if (response.isSuccessful) {
                    fetchStudents(courseId) // Refresh the list
                } else {
                    throw Exception("Failed to remove student")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}