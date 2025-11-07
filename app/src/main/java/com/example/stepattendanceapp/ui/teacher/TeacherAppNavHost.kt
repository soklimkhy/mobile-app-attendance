package com.example.stepattendanceapp.ui.teacher

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.stepattendanceapp.ui.dashboard.ProfileScreen

@Composable
fun TeacherAppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit
) {
    // This instance is shared by the "Courses" tab and its detail screen
    val teacherCoursesViewModel: TeacherCoursesViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = TeacherBottomNavItem.Courses.route,
        modifier = modifier
    ) {

        composable(TeacherBottomNavItem.Courses.route) {
            TeacherCoursesScreen(
                navController = navController,
                viewModel = teacherCoursesViewModel
            )
        }

        composable(TeacherBottomNavItem.Attendances.route) {
            TeacherAttendanceCourseListScreen(
                navController = navController,
                viewModel = viewModel<TeacherAttendanceCourseViewModel>()
            )
        }

        composable(TeacherBottomNavItem.Profile.route) {
            ProfileScreen(onLogoutClick = onLogoutClick)
        }

        composable(
            route = "teacher_course_detail/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")
            if (courseId != null) {
                TeacherCourseDetailScreen(
                    courseId = courseId,
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = teacherCoursesViewModel
                )
            }
        }

        // Route from "Attendance" tab -> List of Schedules
        composable(
            route = "attendance_schedules/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")
            if (courseId != null) {
                TeacherAttendanceSchedulesScreen(
                    courseId = courseId,
                    navController = navController,
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = viewModel<TeacherSchedulesViewModel>()
                )
            }
        }

        // --- THIS IS THE FIX ---
        // This route was missing, causing the crash.
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
                TeacherAttendanceRecordsScreen(
                    courseId = courseId,
                    scheduleId = scheduleId,
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = viewModel<TeacherAttendanceViewModel>()
                )
            }
        }
        // --- END FIX ---
    }
}