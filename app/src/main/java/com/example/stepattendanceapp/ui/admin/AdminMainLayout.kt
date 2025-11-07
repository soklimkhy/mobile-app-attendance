package com.example.stepattendanceapp.ui.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stepattendanceapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainLayout(
    fullName: String,
    onLogoutClick: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    // --- THIS IS THE FIX ---
    // Add "Courses" to the list of items
    val items = listOf(
        AdminBottomNavItem.Users,
        AdminBottomNavItem.Courses,
        AdminBottomNavItem.Attendances,
        AdminBottomNavItem.Profile
    )
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("STEP IT (Admin)", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { menuExpanded = true }.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(text = fullName, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = painterResource(id = R.drawable.ic_github_logo),
                                contentDescription = "User Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(32.dp).clip(CircleShape)
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
                                    navController.navigate(AdminBottomNavItem.Profile.route)
                                    menuExpanded = false
                                },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
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

                // The loop will now correctly create all three tabs
                items.forEach { item ->
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
        AdminAppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            onLogoutClick = onLogoutClick
        )
    }
}