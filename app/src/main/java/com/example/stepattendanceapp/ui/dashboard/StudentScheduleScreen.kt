package com.example.stepattendanceapp.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember // --- NEW ---
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // --- NEW ---
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign // --- NEW ---
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stepattendanceapp.data.model.Schedule
import java.time.LocalDate // --- NEW ---
import java.time.format.DateTimeFormatter // --- NEW ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScheduleScreen(
    courseId: String,
    onNavigateBack: () -> Unit,
    viewModel: StudentScheduleViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    // --- NEW: Get the current date in "yyyy-MM-dd" format ---
    val currentDate = remember {
        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    LaunchedEffect(courseId) {
        viewModel.fetchSchedules(courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Schedules") },
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
        // --- NEW: Column to hold the date and the list ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- NEW: Display the current date at the top ---
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
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    uiState.errorMessage != null -> {
                        Text(
                            text = "Error: ${uiState.errorMessage}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    uiState.schedules.isEmpty() -> {
                        Text(
                            text = "No schedules found for this course.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        // --- NEW: Sort the list by creation time (oldest first) ---
                        // This requires the 'createdAt' field in your Schedule data class
                        val sortedSchedules = uiState.schedules.sortedBy { it.specificDate }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // --- NEW: Use the sorted list ---
                            items(sortedSchedules) { schedule ->
                                ScheduleCard(
                                    schedule = schedule,
                                    currentDate = currentDate // --- NEW: Pass the date
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
private fun ScheduleCard(
    schedule: Schedule,
    currentDate: String // --- NEW: Added current date parameter
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val dayText = schedule.dayOfWeek?.let { if (it in 1..7) days[it - 1] else "N/A" } ?: "N/A"

    // --- NEW: Check if the schedule's specific date matches today ---
    val isToday = schedule.specificDate == currentDate
    val cardColor = if (isToday) {
        Color(0xFFC8E6C9) // Light green
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardColor // --- NEW: Use dynamic color
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Schedule,
                contentDescription = "Schedule",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${schedule.type ?: "CLASS"} in ${schedule.room ?: "N/A"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$dayText, ${schedule.startTime} - ${schedule.endTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
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