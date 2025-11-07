package com.example.stepattendanceapp.ui.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stepattendanceapp.data.model.AdminUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherCourseDetailScreen(
    courseId: String,
    onNavigateBack: () -> Unit,
    viewModel: TeacherCoursesViewModel = viewModel() // Reusing the same VM
) {
    val uiState = viewModel.uiState

    // Trigger the student detail fetch
    LaunchedEffect(courseId) {
        viewModel.fetchStudentDetailsForCourse(courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enrolled Students") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isDetailLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.errorMessage != null -> Text(uiState.errorMessage, modifier = Modifier.align(Alignment.Center))
                uiState.selectedCourseStudents.isEmpty() -> Text("No students are enrolled in this course.", modifier = Modifier.align(Alignment.Center))
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.selectedCourseStudents) { student ->
                            StudentDetailItem(student = student)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentDetailItem(student: AdminUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Person, contentDescription = "Student", modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.fullName ?: "N/A",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "@${student.username ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}