package com.example.stepattendanceapp.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stepattendanceapp.data.model.AttendanceRecord
import com.example.stepattendanceapp.ui.admin.AttendanceStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAttendanceDetailScreen(
    courseId: String,
    onNavigateBack: () -> Unit,
    viewModel: StudentAttendanceViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    LaunchedEffect(courseId) {
        viewModel.fetchAttendance(courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Report") },
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
                uiState.records.isEmpty() -> {
                    Text(
                        text = "No attendance records found for this course.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.records) { record ->
                            AttendanceItem(record = record)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceItem(record: AttendanceRecord) {
    // --- THIS IS THE FIX ---
    // We provide a default value "UNKNOWN" in case record.status is null
    val statusText = record.status ?: "UNKNOWN"
    // --- END FIX ---

    val statusColor = when (statusText) {
        "PRESENT" -> Color(0xFF4CAF50)
        "ONLINE" -> Color(0xFF0288D1)
        "LATE" -> Color(0xFFFFA000)
        "ABSENT" -> Color(0xFFD32F2F)
        "EXCUSED" -> Color(0xFF7B1FA2)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when(statusText) {
                    "PRESENT", "ONLINE", "LATE" -> Icons.Default.CheckCircle
                    "ABSENT" -> Icons.Default.DoNotDisturb
                    else -> Icons.Default.Help
                },
                contentDescription = statusText,
                tint = statusColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = statusText, // Pass the non-nullable statusText
                    style = MaterialTheme.typography.titleMedium,
                    color = statusColor
                )
                Text(
                    text = "Date: ${record.date}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!record.notes.isNullOrBlank()) {
                Text(
                    text = "Note: ${record.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}