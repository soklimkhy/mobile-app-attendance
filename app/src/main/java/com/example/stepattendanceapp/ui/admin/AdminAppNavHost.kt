package com.example.stepattendanceapp.ui.admin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.stepattendanceapp.ui.dashboard.ProfileScreen

@Composable
fun AdminAppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AdminBottomNavItem.Users.route,
        modifier = modifier
    ) {
        // --- User Management ---
        composable(AdminBottomNavItem.Users.route) {
            ManageUsersScreen()
        }

        // --- Course Management Flow ---
        composable(AdminBottomNavItem.Courses.route) {
            ManageCoursesScreen(navController = navController)
        }

        composable(
            route = "course_details/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")
            if (courseId != null) {
                CourseDetailsScreen(
                    courseId = courseId,
                    navController = navController,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = "manage_schedules/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")
            if (courseId != null) {
                ManageSchedulesScreen(
                    courseId = courseId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        // --- Attendance Management Flow ---
        composable(AdminBottomNavItem.Attendances.route) {
            AttendanceCourseListScreen(navController = navController)
        }

        composable(
            route = "attendance_schedules/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")
            if (courseId != null) {
                AttendanceScheduleListScreen(
                    courseId = courseId,
                    navController = navController,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = "attendance_records/{courseId}/{scheduleId}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType },
                navArgument("scheduleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")
            val scheduleId = backStackEntry.arguments?.getString("scheduleId")
            if (courseId != null && scheduleId != null) {
                // This call now correctly passes all required parameters
                AttendanceRecordsScreen(
                    courseId = courseId,
                    scheduleId = scheduleId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        // --- Profile ---
        composable(AdminBottomNavItem.Profile.route) {
            ProfileScreen(onLogoutClick = onLogoutClick)
        }
    }
}