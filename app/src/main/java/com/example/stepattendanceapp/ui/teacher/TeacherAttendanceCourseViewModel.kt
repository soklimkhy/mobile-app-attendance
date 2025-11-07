package com.example.stepattendanceapp.ui.teacher

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

// This UI state is specific to the attendance course list
data class TeacherAttendanceCourseUiState(
    val isLoading: Boolean = false,
    val courses: List<CourseDetail> = emptyList(),
    val errorMessage: String? = null
)

class TeacherAttendanceCourseViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(TeacherAttendanceCourseUiState())
        private set

    private fun getContext(): Context = getApplication<Application>().applicationContext

    init {
        fetchAllCourses()
    }

    fun fetchAllCourses() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                // Calls the teacher-specific endpoint
                val response = RetrofitClient.getApiService(getContext()).getCoursesForTeacher()

                if (response.isSuccessful && response.body() != null) {
                    // Unwraps the list from [ { "course": {...} } ]
                    val unwrappedCourses = response.body()!!.map { it.course }
                    uiState = uiState.copy(isLoading = false, courses = unwrappedCourses)
                } else {
                    throw Exception("Failed to fetch courses. Status: ${response.code()}")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}