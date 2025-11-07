package com.example.stepattendanceapp.ui.admin

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.stepattendanceapp.R
import com.example.stepattendanceapp.data.model.AdminUser
import com.example.stepattendanceapp.data.model.AttendanceRecord
import com.example.stepattendanceapp.data.model.Schedule

// Enum to define the statuses
enum class AttendanceStatus(val label: String) {
    PRESENT("PRESENT"),
    ONLINE("ONLINE"),
    LATE("LATE"),
    ABSENT("ABSENT"),
    EXCUSED("EXCUSED")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceRecordsScreen(
    courseId: String,
    scheduleId: String,
    onNavigateBack: () -> Unit,
    viewModel: AttendanceRecordsViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(scheduleId) {
        viewModel.fetchAttendanceData(courseId, scheduleId)
    }

    // --- Listen for messages ---
    val context = LocalContext.current
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
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Manually Add Attendance")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.errorMessage != null && uiState.studentAttendanceList.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            uiState.schedule?.let {
                                ScheduleInfoCard(schedule = it)
                            }
                        }
                        items(uiState.studentAttendanceList) { item ->
                            val isSaving = uiState.savingStudentIds.contains(item.student.id)
                            StudentAttendanceRow(
                                student = item.student,
                                currentStatus = item.attendance?.status,
                                isSaving = isSaving,
                                onStatusChange = { newStatus ->
                                    if (!isSaving) {
                                        viewModel.setAttendanceStatus(
                                            courseId,
                                            scheduleId,
                                            item.student.id!!,
                                            newStatus,
                                            item.attendance // Pass existing record
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddAttendanceDialog(
            onDismiss = { showAddDialog = false },
            onSave = { studentId, status, notes ->
                viewModel.createAttendance(scheduleId, courseId, studentId, status, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ScheduleInfoCard(schedule: Schedule) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val dayText = schedule.dayOfWeek?.let { if(it in 1..7) days[it-1] else "N/A" } ?: "N/A"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Schedule Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PinDrop, contentDescription = "Room", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Room: ${schedule.room ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = "Time", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (schedule.specificDate != null) "${schedule.specificDate}" else "$dayText, ${schedule.startTime} - ${schedule.endTime}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if(schedule.status != "ACTIVE") {
                Text(
                    text = "Status: ${schedule.status}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = getStatusColor(schedule.status ?: "")
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentAttendanceRow(
    student: AdminUser,
    currentStatus: String?,
    isSaving: Boolean,
    onStatusChange: (String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(student.fullName ?: "N/A", style = MaterialTheme.typography.titleMedium)
                    Text(student.username ?: "N/A", style = MaterialTheme.typography.bodyMedium)
                }
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- Dropdown Menu for Status ---
            ExposedDropdownMenuBox(
                expanded = menuExpanded && !isSaving,
                onExpandedChange = { if (!isSaving) menuExpanded = !menuExpanded }
            ) {
                OutlinedTextField(
                    value = currentStatus ?: "Select Status...",
                    onValueChange = {},
                    label = { Text("Status") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        color = getStatusColor(currentStatus)
                    )
                )
                ExposedDropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    AttendanceStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.label) },
                            onClick = {
                                onStatusChange(status.label)
                                menuExpanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Circle,
                                    contentDescription = null,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAttendanceDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String?) -> Unit
) {
    var studentId by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("PRESENT") }
    var notes by remember { mutableStateOf("") }
    var statusMenuExpanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("PRESENT", "ABSENT", "LATE", "ONLINE", "EXCUSED")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Attendance Record") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = studentId, onValueChange = { studentId = it }, label = { Text("Student ID") })

                ExposedDropdownMenuBox(
                    expanded = statusMenuExpanded,
                    onExpandedChange = { statusMenuExpanded = !statusMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        label = { Text("Status") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusMenuExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = statusMenuExpanded,
                        onDismissRequest = { statusMenuExpanded = false }
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    status = option
                                    statusMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (Optional)") })
            }
        },
        confirmButton = {
            Button(onClick = { onSave(studentId, status, notes.ifBlank { null }) }) { Text("Save") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
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