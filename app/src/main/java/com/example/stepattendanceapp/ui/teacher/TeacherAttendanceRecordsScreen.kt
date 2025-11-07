package com.example.stepattendanceapp.ui.teacher

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stepattendanceapp.R
import com.example.stepattendanceapp.data.model.AdminUser
import com.example.stepattendanceapp.data.model.AttendanceRecord
import com.example.stepattendanceapp.data.model.Schedule
import com.example.stepattendanceapp.ui.admin.AttendanceStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherAttendanceRecordsScreen(
    courseId: String,
    scheduleId: String,
    onNavigateBack: () -> Unit,
    viewModel: TeacherAttendanceViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(scheduleId) {
        viewModel.fetchAttendanceData(courseId, scheduleId)
    }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_LONG).show()
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Take Attendance") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Save Changes") },
                icon = { Icon(Icons.Default.Save, contentDescription = "Save") },
                onClick = {
                    viewModel.saveChanges(courseId, scheduleId)
                },
                expanded = !uiState.isLoading,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when {
                uiState.isLoading && uiState.studentAttendanceList.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.errorMessage != null && uiState.studentAttendanceList.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
                uiState.studentAttendanceList.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No attendance records found for this schedule.", modifier = Modifier.padding(16.dp))
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp), // Padding at bottom for FAB
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            uiState.schedule?.let {
                                TeacherScheduleInfoCard(schedule = it)
                            }
                        }
                        items(uiState.studentAttendanceList) { item ->
                            TeacherStudentAttendanceRow(
                                record = item.attendance!!,
                                onStatusChange = { newStatus ->
                                    viewModel.updateLocalStatus(item.student.id!!, newStatus)
                                }
                            )
                        }
                    }
                }
            }
            // Show a semi-transparent overlay while saving
            if (uiState.isLoading && uiState.studentAttendanceList.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun TeacherScheduleInfoCard(schedule: Schedule) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val dayText = schedule.dayOfWeek?.let { if(it in 1..7) days[it-1] else "N/A" } ?: "N/A"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Schedule Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PinDrop, "Room", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Room: ${schedule.room ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, "Time", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (schedule.specificDate != null) "${schedule.specificDate}" else "$dayText, ${schedule.startTime} - ${schedule.endTime}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeacherStudentAttendanceRow(
    record: AttendanceRecord,
    onStatusChange: (String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var currentStatus by remember(record.status) { mutableStateOf(record.status ?: "ABSENT") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(record.fullname ?: "Unknown Student", style = MaterialTheme.typography.titleMedium)
                    Text("@${record.username ?: record.studentId}", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = menuExpanded,
                onExpandedChange = { menuExpanded = !menuExpanded }
            ) {
                OutlinedTextField(
                    value = currentStatus,
                    onValueChange = {},
                    label = { Text("Status") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(color = getStatusColor(currentStatus))
                )
                ExposedDropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    AttendanceStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.label) },
                            onClick = {
                                currentStatus = status.label // Update local UI first
                                onStatusChange(status.label) // Notify ViewModel
                                menuExpanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Circle,
                                    null,
                                    tint = getStatusColor(status.label),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getStatusColor(status: String?): Color {
    return when (status) {
        "PRESENT" -> Color(0xFF4CAF50)
        "ONLINE" -> Color(0xFF0288D1)
        "LATE" -> Color(0xFFFFA000)
        "ABSENT" -> Color(0xFFD32F2F)
        "EXCUSED" -> Color(0xFF7B1FA2)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}