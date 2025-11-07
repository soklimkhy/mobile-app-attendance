package com.example.stepattendanceapp.ui.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.stepattendanceapp.ui.BottomNavItem

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Class.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Class.route) {
            HomeScreen()
        }
        composable(BottomNavItem.Class.route) {
            ClassScreen(navController = navController)
        }

        // --- THIS IS THE FIX ---
        // Change the route from BottomNavItem.Schedule.route to BottomNavItem.Attendance.route
        composable(BottomNavItem.Attendance.route) {
            StudentAttendanceScreen(navController = navController)
        }
        // --- END FIX ---

        composable(BottomNavItem.Profile.route) {
            ProfileScreen(onLogoutClick = onLogoutClick)
        }

        composable(
            route = "student_course_schedules/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")
            if (courseId != null) {
                StudentScheduleScreen(
                    courseId = courseId,
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = viewModel<StudentScheduleViewModel>()
                )
            }
        }

        composable(
            route = "student_attendance_detail/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")
            if (courseId != null) {
                StudentAttendanceDetailScreen(
                    courseId = courseId,
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = viewModel<StudentAttendanceViewModel>()
                )
            }
        }
    }
}