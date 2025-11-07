package com.example.stepattendanceapp.ui.admin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist // <-- ADD IMPORT
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AdminBottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Users : AdminBottomNavItem("users", Icons.Default.Group, "Users")
    object Courses : AdminBottomNavItem("courses", Icons.Default.School, "Courses")
    object Attendances : AdminBottomNavItem("attendances", Icons.Default.Checklist, "Attendance") // <-- ADD THIS
    object Profile : AdminBottomNavItem("profile", Icons.Default.Person, "Profile")
}