package com.example.stepattendanceapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpOffset
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stepattendanceapp.R
import com.example.stepattendanceapp.ui.dashboard.AppNavHost

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
//    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Class : BottomNavItem("class", Icons.Default.Class, "Course")
    object Attendance : BottomNavItem("attendance", Icons.Default.Checklist, "Attendance")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    fullName: String,
    onLogoutClick: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val items = listOf(
//        BottomNavItem.Home,
        BottomNavItem.Class,
        BottomNavItem.Attendance,
        BottomNavItem.Profile
    )

    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("STEP IT", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { menuExpanded = true }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(text = fullName, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = painterResource(id = R.drawable.ic_github_logo),
                                contentDescription = "User Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                            offset = DpOffset(x = (-10).dp, y = 0.dp)
                        ) {
                            val itemColors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.onSurface,
                                leadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            DropdownMenuItem(
                                text = { Text("Profile", style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    navController.navigate(BottomNavItem.Profile.route)
                                    menuExpanded = false
                                },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                colors = itemColors
                            )

                            DropdownMenuItem(
                                text = { Text("Settings", style = MaterialTheme.typography.bodyMedium) },
                                onClick = { menuExpanded = false },
                                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                colors = itemColors
                            )

                            HorizontalDivider()

                            DropdownMenuItem(
                                text = { Text("Log Out", style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    menuExpanded = false
                                    onLogoutClick()
                                },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log Out") },
                                colors = itemColors
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item -> // This loop will now use the correct list
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            onLogoutClick = onLogoutClick
        )
    }
}