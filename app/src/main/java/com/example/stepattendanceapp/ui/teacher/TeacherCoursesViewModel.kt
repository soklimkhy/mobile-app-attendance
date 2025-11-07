package com.example.stepattendanceapp.ui.teacher

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepattendanceapp.data.model.AdminUser
import com.example.stepattendanceapp.data.model.CourseDetail
import com.example.stepattendanceapp.data.remote.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

data class TeacherCoursesUiState(
    val isLoading: Boolean = false,
    val courses: List<CourseDetail> = emptyList(),
    val errorMessage: String? = null,

    // State for the detail screen
    val isDetailLoading: Boolean = false,
    val selectedCourseStudents: List<AdminUser> = emptyList()
)

class TeacherCoursesViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(TeacherCoursesUiState())
        private set

    private fun getContext(): Context = getApplication<Application>().applicationContext

    init {
        fetchAllCourses()
    }

    fun fetchAllCourses() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                // 1. Fetch the wrapped list
                val response = RetrofitClient.getApiService(getContext()).getCoursesForTeacher()

                if (response.isSuccessful && response.body() != null) {
                    // 2. Unwrap the 'course' object from each item
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

    // This fetches all students for a selected course
    fun fetchStudentDetailsForCourse(courseId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isDetailLoading = true, selectedCourseStudents = emptyList())
            try {
                // 1. Find the course in the list we already fetched
                val course = uiState.courses.find { it.id == courseId }
                val studentIds = course?.studentIds ?: emptyList()

                if (studentIds.isEmpty()) {
                    uiState = uiState.copy(isDetailLoading = false)
                    return@launch
                }

                // 2. Launch N parallel API calls to get student details
                val studentDetailJobs = studentIds.map { id ->
                    async {
                        RetrofitClient.getApiService(getContext()).getStudentDetailsForTeacher(id)
                    }
                }

                // 3. Wait for all calls to complete
                val responses = studentDetailJobs.awaitAll()

                // 4. Collect and unwrap the results
                val studentList = responses
                    .filter { it.isSuccessful && it.body() != null }
                    .flatMap { it.body()!!.students } // flatMap joins all lists together

                uiState = uiState.copy(isDetailLoading = false, selectedCourseStudents = studentList)

            } catch (e: Exception) {
                uiState = uiState.copy(isDetailLoading = false, errorMessage = e.message)
            }
        }
    }
}