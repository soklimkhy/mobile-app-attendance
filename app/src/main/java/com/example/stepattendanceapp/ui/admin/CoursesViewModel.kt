package com.example.stepattendanceapp.ui.admin

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepattendanceapp.data.local.SessionManager // Make sure this is imported
import com.example.stepattendanceapp.data.model.CourseDetail
import com.example.stepattendanceapp.data.model.CourseRequest
import com.example.stepattendanceapp.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Response

data class CoursesUiState(
    val isLoading: Boolean = false,
    val courses: List<CourseDetail> = emptyList(),
    val errorMessage: String? = null
)

class CoursesViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(CoursesUiState())
        private set

    private val sessionManager = SessionManager(application)
    private fun getContext(): Context = getApplication<Application>().applicationContext

    init {
        fetchAllCourses()
    }

    fun fetchAllCourses() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                // --- THIS IS THE FIX ---
                // Admins must call the admin-specific endpoint
                val response: Response<List<CourseDetail>> =
                    RetrofitClient.getApiService(getContext()).getAllCoursesAsAdmin()
                // --- END FIX ---

                if (response.isSuccessful && response.body() != null) {
                    uiState = uiState.copy(isLoading = false, courses = response.body()!!)
                } else {
                    throw Exception("Failed to fetch courses. Status: ${response.code()}")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun createCourse(request: CourseRequest, onComplete: () -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.getApiService(getContext()).createCourse(request)
                if (response.isSuccessful) {
                    fetchAllCourses() // Refresh list
                    onComplete()
                } else {
                    throw Exception("Failed to create course")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun updateCourse(courseId: String, request: CourseRequest, onComplete: () -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.getApiService(getContext()).updateCourse(courseId, request)
                if (response.isSuccessful) {
                    fetchAllCourses() // Refresh list
                    onComplete()
                } else {
                    throw Exception("Failed to update course")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun deleteCourse(courseId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.getApiService(getContext()).deleteCourse(courseId)
                if (response.isSuccessful) {
                    fetchAllCourses() // Refresh list
                } else {
                    throw Exception("Failed to delete course")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}