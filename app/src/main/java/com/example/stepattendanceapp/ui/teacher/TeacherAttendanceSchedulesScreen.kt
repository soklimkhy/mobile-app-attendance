package com.example.stepattendanceapp.ui.teacher

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.stepattendanceapp.data.model.Schedule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherAttendanceSchedulesScreen(
    courseId: String,
    navController: NavHostController,
    onNavigateBack: () -> Unit,
    viewModel: TeacherSchedulesViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    LaunchedEffect(courseId) {
        viewModel.fetchSchedules(courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select a Schedule") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.errorMessage != null -> Text(uiState.errorMessage, modifier = Modifier.align(Alignment.Center))
                uiState.schedules.isEmpty() -> Text("No schedules found for this course.", modifier = Modifier.align(Alignment.Center))
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.schedules) { schedule ->
                            TeacherScheduleListItem(
                                schedule = schedule,
                                onClick = {
                                    // Navigate to the attendance screen
                                    schedule.id?.let {
                                        navController.navigate("attendance_records/$courseId/${schedule.id}")
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

@Composable
fun TeacherScheduleListItem(
    schedule: Schedule,
    onClick: () -> Unit
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val dayText = schedule.dayOfWeek?.let { if(it in 1..7) days[it-1] else "N/A" } ?: "N/A"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = when(schedule.status) {
                "CANCELED" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                "COMPLETED" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Schedule, contentDescription = "Schedule", modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${schedule.type ?: "CLASS"} at ${schedule.room ?: "N/A"}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$dayText, ${schedule.startTime} - ${schedule.endTime}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (schedule.specificDate != null) {
                    Text(
                        text = "Date: ${schedule.specificDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}