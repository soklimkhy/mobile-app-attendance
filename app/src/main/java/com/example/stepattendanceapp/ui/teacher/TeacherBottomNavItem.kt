package com.example.stepattendanceapp.ui.teacher

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector

// Defines the tabs for the Teacher dashboard
sealed class TeacherBottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Courses : TeacherBottomNavItem("courses", Icons.Default.School, "Courses")
    object Attendances : TeacherBottomNavItem("attendances", Icons.Default.Checklist, "Attendance")
    object Profile : TeacherBottomNavItem("profile", Icons.Default.Person, "Profile")
}