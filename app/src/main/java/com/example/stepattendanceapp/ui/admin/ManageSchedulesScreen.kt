package com.example.stepattendanceapp.ui.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stepattendanceapp.data.model.Schedule
import com.example.stepattendanceapp.data.model.ScheduleRequest
import com.example.stepattendanceapp.data.model.UpdateScheduleRequest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSchedulesScreen(
    courseId: String,
    onNavigateBack: () -> Unit,
    viewModel: ManageSchedulesViewModel = viewModel()
) {
    val uiState = viewModel.uiState
    var showDialog by remember { mutableStateOf(false) }
    var selectedSchedule by remember { mutableStateOf<Schedule?>(null) }

    LaunchedEffect(courseId) {
        viewModel.fetchSchedules(courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Schedules") },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedSchedule = null // Clear for "Create" mode
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Schedule")
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            when {
                uiState.isLoading && uiState.schedules.isEmpty() -> {
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
                    Text("No schedules found for this course.", modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.schedules) { schedule ->
                            ScheduleListItem(
                                schedule = schedule,
                                onEdit = {
                                    selectedSchedule = schedule
                                    showDialog = true
                                },
                                onDelete = { viewModel.deleteSchedule(courseId, schedule.id!!) },
                                onCancel = { viewModel.cancelSchedule(courseId, schedule.id!!) },
                                onComplete = { viewModel.completeSchedule(courseId, schedule.id!!) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        ScheduleEditDialog(
            courseId = courseId,
            schedule = selectedSchedule,
            onDismiss = { showDialog = false },
            onCreate = { request ->
                viewModel.createSchedule(courseId, request) { showDialog = false }
            },
            onUpdate = { id, request ->
                viewModel.updateSchedule(courseId, id, request) { showDialog = false }
            }
        )
    }
}

@Composable
fun ScheduleListItem(
    schedule: Schedule,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val dayText = schedule.dayOfWeek?.let { if(it in 1..7) days[it-1] else "N/A" } ?: "N/A"

    Card(
        modifier = Modifier.fillMaxWidth(),
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
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    DropdownMenuItem(text = { Text("Edit") }, onClick = { onEdit(); menuExpanded = false })
                    DropdownMenuItem(text = { Text("Complete") }, onClick = { onComplete(); menuExpanded = false })
                    DropdownMenuItem(text = { Text("Cancel") }, onClick = { onCancel(); menuExpanded = false })
                    DropdownMenuItem(text = { Text("Delete") }, onClick = { onDelete(); menuExpanded = false })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleEditDialog(
    courseId: String,
    schedule: Schedule?,
    onDismiss: () -> Unit,
    onCreate: (ScheduleRequest) -> Unit,
    onUpdate: (String, UpdateScheduleRequest) -> Unit
) {
    val isEditMode = schedule != null

    // --- State for input fields ---
    var dayOfWeek by remember { mutableStateOf(schedule?.dayOfWeek?.toString() ?: "1") }
    var startTime by remember { mutableStateOf(schedule?.startTime ?: "09:00") }
    var endTime by remember { mutableStateOf(schedule?.endTime ?: "10:30") }
    var room by remember { mutableStateOf(schedule?.room ?: "") }
    var type by remember { mutableStateOf(schedule?.type ?: "REGULAR") }
    var specificDate by remember { mutableStateOf(schedule?.specificDate ?: "") }
    var status by remember { mutableStateOf(schedule?.status ?: "ACTIVE") }
    var notes by remember { mutableStateOf(schedule?.notes ?: "") }

    // --- State for validation errors ---
    var dayOfWeekError by remember { mutableStateOf<String?>(null) }
    var startTimeError by remember { mutableStateOf<String?>(null) }
    var endTimeError by remember { mutableStateOf<String?>(null) }
    var roomError by remember { mutableStateOf<String?>(null) }
    var specificDateError by remember { mutableStateOf<String?>(null) }

    // --- State for dropdowns and pickers ---
    val typeOptions = listOf("REGULAR", "MAKEUP", "SPECIAL")
    val statusOptions = listOf("ACTIVE", "CANCELLED", "COMPLETED")
    var typeMenuExpanded by remember { mutableStateOf(false) }
    var statusMenuExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Regex for HH:mm time format
    val timeRegex = Pattern.compile("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditMode) "Edit Schedule" else "Create Schedule") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = typeMenuExpanded,
                    onExpandedChange = { typeMenuExpanded = !typeMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        label = { Text("Type") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeMenuExpanded,
                        onDismissRequest = { typeMenuExpanded = false }
                    ) {
                        typeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    type = option
                                    typeMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = dayOfWeek,
                    onValueChange = { dayOfWeek = it; dayOfWeekError = null },
                    label = { Text("Day of Week (1=Mon)") },
                    isError = dayOfWeekError != null,
                    supportingText = { if (dayOfWeekError != null) Text(dayOfWeekError!!) },
                    enabled = (type == "REGULAR")
                )

                OutlinedTextField(
                    value = specificDate,
                    onValueChange = {},
                    label = { Text("Specific Date (YYYY-MM-DD)") },
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.CalendarToday, "Select Date", Modifier.clickable { showDatePicker = true }) },
                    modifier = Modifier.clickable { showDatePicker = true },
                    isError = specificDateError != null,
                    supportingText = { if (specificDateError != null) Text(specificDateError!!) },
                    enabled = (type != "REGULAR")
                )

                if (isEditMode) {
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
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
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
                }

                OutlinedTextField(value = startTime, onValueChange = { startTime = it; startTimeError = null }, label = { Text("Start Time (HH:mm)") }, isError = startTimeError != null, supportingText = { if (startTimeError != null) Text(startTimeError!!) })
                OutlinedTextField(value = endTime, onValueChange = { endTime = it; endTimeError = null }, label = { Text("End Time (HH:mm)") }, isError = endTimeError != null, supportingText = { if (endTimeError != null) Text(endTimeError!!) })
                OutlinedTextField(value = room, onValueChange = { room = it; roomError = null }, label = { Text("Room") }, isError = roomError != null, supportingText = { if (roomError != null) Text(roomError!!) })

                // --- THIS IS THE CORRECTED LINE ---
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (Optional)") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var isValid = true
                    dayOfWeekError = null
                    specificDateError = null
                    startTimeError = null
                    endTimeError = null
                    roomError = null

                    if (type == "REGULAR") {
                        if (dayOfWeek.toIntOrNull() == null || dayOfWeek.toInt() !in 1..7) {
                            dayOfWeekError = "Must be 1-7"
                            isValid = false
                        }
                    } else {
                        if (specificDate.isBlank()) {
                            specificDateError = "Required for this type"
                            isValid = false
                        }
                    }
                    if (!timeRegex.matcher(startTime).matches()) {
                        startTimeError = "Invalid format (HH:mm)"
                        isValid = false
                    }
                    if (!timeRegex.matcher(endTime).matches()) {
                        endTimeError = "Invalid format (HH:mm)"
                        isValid = false
                    }
                    if (room.isBlank()) {
                        roomError = "Cannot be empty"
                        isValid = false
                    }

                    if (isValid) {
                        val finalSpecificDate = specificDate.ifBlank { null }
                        val finalDayOfWeek = dayOfWeek.toIntOrNull()
                        val finalNotes = notes.ifBlank { null }

                        if (isEditMode) {
                            val request = UpdateScheduleRequest(
                                dayOfWeek = if (type == "REGULAR") finalDayOfWeek else null,
                                startTime = startTime,
                                endTime = endTime,
                                room = room,
                                type = type,
                                specificDate = if (type != "REGULAR") finalSpecificDate else null,
                                status = status,
                                notes = finalNotes
                            )
                            onUpdate(schedule!!.id!!, request)
                        } else {
                            val request = ScheduleRequest(
                                courseId = courseId,
                                dayOfWeek = if (type == "REGULAR") finalDayOfWeek else null,
                                startTime = startTime,
                                endTime = endTime,
                                room = room,
                                type = type,
                                specificDate = if (type != "REGULAR") finalSpecificDate else null,
                                notes = finalNotes
                            )
                            onCreate(request)
                        }
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = Instant.ofEpochMilli(millis)
                        val localDate = instant.atZone(ZoneId.of("UTC")).toLocalDate()
                        specificDate = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        specificDateError = null
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        specificDate = ""
                        showDatePicker = false
                    }) { Text("Clear") }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}