package com.example.stepattendanceapp.ui.teacher

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun TeacherAttendanceCourseListScreen(
    navController: NavHostController,
    viewModel: TeacherAttendanceCourseViewModel = viewModel() // Uses the new ViewModel
) {
    val uiState = viewModel.uiState // uiState is now resolved
    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select a Course",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Please choose a course to manage attendance.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.courses.isEmpty() -> {
                    Text("No courses found.", modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.courses) { course -> // Type is now CourseDetail
                            TeacherCourseListItem(
                                course = course, // This is now correct
                                onClick = {
                                    // Navigate to the attendance schedule list
                                    course.id?.let {
                                        navController.navigate("attendance_schedules/$it")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}