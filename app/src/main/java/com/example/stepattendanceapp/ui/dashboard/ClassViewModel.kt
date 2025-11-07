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

data class ClassUiState(
    val isLoading: Boolean = false,
    val courses: List<CourseDetail> = emptyList(),
    val errorMessage: String? = null
)

class ClassViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(ClassUiState())
        private set

    private fun getContext(): Context = getApplication<Application>().applicationContext

    init {
        fetchEnrolledCourses()
    }

    fun fetchEnrolledCourses() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                // Call the new student-specific endpoint
                val response = RetrofitClient.getApiService(getContext()).getEnrolledCourses()

                if (response.isSuccessful && response.body() != null) {
                    uiState = uiState.copy(isLoading = false, courses = response.body()!!)
                } else {
                    throw Exception("Failed to fetch enrolled courses. Status: ${response.code()}")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}