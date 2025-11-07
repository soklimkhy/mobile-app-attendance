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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.stepattendanceapp.data.model.CourseDetail
import com.example.stepattendanceapp.data.model.CourseRequest
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCoursesScreen(
    viewModel: CoursesViewModel = viewModel(),
    navController: NavHostController
) {
    val uiState = viewModel.uiState
    var showDialog by remember { mutableStateOf(false) }
    var selectedCourse by remember { mutableStateOf<CourseDetail?>(null) }
    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedCourse = null
                    showDialog = true
                },
                // Apply theme colors
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Course")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading && uiState.courses.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.courses.isEmpty() -> {
                    Text("No courses found.", modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.courses) { course ->
                            CourseListItem(
                                course = course,
                                onManageStudents = {
                                    course.id?.let {
                                        navController.navigate("course_details/$it")
                                    }
                                },
                                onEdit = {
                                    selectedCourse = course
                                    showDialog = true
                                },
                                onDelete = {
                                    course.id?.let { viewModel.deleteCourse(it) }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        CourseEditDialog(
            course = selectedCourse,
            onDismiss = { showDialog = false },
            onSave = { request ->
                if (selectedCourse == null) {
                    viewModel.createCourse(request) { showDialog = false }
                } else {
                    selectedCourse?.id?.let {
                        viewModel.updateCourse(it, request) { showDialog = false }
                    }
                }
            }
        )
    }
}

@Composable
fun CourseListItem(
    course: CourseDetail,
    onManageStudents: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onManageStudents() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        // Apply theme color
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.School,
                contentDescription = "Course",
                modifier = Modifier.size(40.dp),
                // Apply theme color
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${course.name ?: "N/A"} (${course.code ?: "N/A"})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Teacher ID: ${course.teacherId ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${course.academicYear ?: ""} - ${course.semester ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
                    DropdownMenuItem(text = { Text("Edit Course Info") }, onClick = { onEdit(); menuExpanded = false })
                    DropdownMenuItem(text = { Text("Delete Course") }, onClick = { onDelete(); menuExpanded = false })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseEditDialog(
    course: CourseDetail?,
    onDismiss: () -> Unit,
    onSave: (CourseRequest) -> Unit
) {
    // --- Input field states ---
    var code by remember { mutableStateOf(course?.code ?: "") }
    var name by remember { mutableStateOf(course?.name ?: "") }
    var description by remember { mutableStateOf(course?.description ?: "") }
    var teacherId by remember { mutableStateOf(course?.teacherId ?: "") }
    var academicYear by remember { mutableStateOf(course?.academicYear ?: "") }
    var semester by remember { mutableStateOf(course?.semester ?: "") }

    // --- Validation error states ---
    var codeError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var teacherIdError by remember { mutableStateOf<String?>(null) }
    var academicYearError by remember { mutableStateOf<String?>(null) }
    var semesterError by remember { mutableStateOf<String?>(null) }

    val isEditMode = course != null

    // Regex for YYYY-YYYY format
    val yearRegex = Pattern.compile("^\\d{4}-\\d{4}$")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditMode) "Edit Course" else "Create Course") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it; codeError = null },
                    label = { Text("Course Code (e.g., CS101)") },
                    isError = codeError != null,
                    supportingText = { if (codeError != null) Text(codeError!!) }
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = null },
                    label = { Text("Course Name") },
                    isError = nameError != null,
                    supportingText = { if (nameError != null) Text(nameError!!) }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") }
                )
                OutlinedTextField(
                    value = teacherId,
                    onValueChange = { teacherId = it; teacherIdError = null },
                    label = { Text("Teacher ID") },
                    isError = teacherIdError != null,
                    supportingText = { if (teacherIdError != null) Text(teacherIdError!!) }
                )
                OutlinedTextField(
                    value = academicYear,
                    onValueChange = { academicYear = it; academicYearError = null },
                    label = { Text("Academic Year (YYYY-YYYY)") },
                    isError = academicYearError != null,
                    supportingText = { if (academicYearError != null) Text(academicYearError!!) }
                )
                OutlinedTextField(
                    value = semester,
                    onValueChange = { semester = it; semesterError = null },
                    label = { Text("Semester (e.g., Fall)") },
                    isError = semesterError != null,
                    supportingText = { if (semesterError != null) Text(semesterError!!) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // --- VALIDATION LOGIC ---
                    var isValid = true
                    if (code.isBlank()) {
                        codeError = "Code is required"
                        isValid = false
                    }
                    if (name.isBlank()) {
                        nameError = "Name is required"
                        isValid = false
                    }
                    if (teacherId.isBlank()) {
                        teacherIdError = "Teacher ID is required"
                        isValid = false
                    }
                    if (semester.isBlank()) {
                        semesterError = "Semester is required"
                        isValid = false
                    }
                    if (!yearRegex.matcher(academicYear).matches()) {
                        academicYearError = "Format must be YYYY-YYYY"
                        isValid = false
                    }

                    if (isValid) {
                        val request = CourseRequest(code, name, description, teacherId, academicYear, semester)
                        onSave(request)
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}