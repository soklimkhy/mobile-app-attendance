package com.example.stepattendanceapp.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.stepattendanceapp.data.model.Schedule
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScheduleListScreen(
    courseId: String,
    navController: NavHostController,
    onNavigateBack: () -> Unit,
    viewModel: ManageSchedulesViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    // Get the current date in "yyyy-MM-dd" format
    val currentDate = remember {
        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    LaunchedEffect(Unit) {
        viewModel.fetchSchedules(courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select a Schedule") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        // Added a Column to hold the date and the list
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Display the current date at the top
            Text(
                text = "Today: $currentDate",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    uiState.errorMessage != null -> Text(uiState.errorMessage, modifier = Modifier.align(Alignment.Center))
                    else -> {
                        // Sort the list by creation time (oldest first)
                        // This line will now work after you update your Schedule.kt
                        val sortedSchedules = uiState.schedules.sortedBy { it.specificDate }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            // Adjusted padding to be inside the column
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Use the sorted list
                            items(sortedSchedules) { schedule ->
                                ScheduleRow(
                                    schedule = schedule,
                                    currentDate = currentDate, // Pass the current date
                                    onClick = {
                                        schedule.id?.let { scheduleId ->
                                            navController.navigate("attendance_records/$courseId/$scheduleId")
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
}

@Composable
private fun ScheduleRow(
    schedule: Schedule,
    currentDate: String, // Added current date parameter
    onClick: () -> Unit
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val dayText = schedule.dayOfWeek?.let { if(it in 1..7) days[it-1] else "N/A" } ?: "N/A"

    // Check if the schedule's specific date matches today
    val isToday = schedule.specificDate == currentDate
    val cardColor = if (isToday) {
        // A light, theme-friendly green. You can change this color.
        Color(0xFFC8E6C9)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardColor), // Use dynamic color
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${schedule.type} at ${schedule.room}", style = MaterialTheme.typography.titleMedium)
            Text("$dayText, ${schedule.startTime} - ${schedule.endTime}", style = MaterialTheme.typography.bodyMedium)
            if (schedule.specificDate != null) {
                Text("Date: ${schedule.specificDate}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}