package com.example.stepattendanceapp.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.stepattendanceapp.data.model.CourseDetail

@Composable
fun AttendanceCourseListScreen(
    navController: NavHostController,
    viewModel: CoursesViewModel = viewModel() // Reuse existing ViewModel
) {
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.fetchAllCourses()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            uiState.errorMessage != null -> Text(uiState.errorMessage, modifier = Modifier.align(Alignment.Center))
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.courses) { course ->
                        CourseRow(course = course) {
                            navController.navigate("attendance_schedules/${course.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseRow(course: CourseDetail, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("${course.name} (${course.code})", style = MaterialTheme.typography.titleMedium)
                Text("${course.academicYear} - ${course.semester}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}