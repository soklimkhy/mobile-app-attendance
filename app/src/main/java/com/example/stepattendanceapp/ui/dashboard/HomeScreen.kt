package com.example.stepattendanceapp.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.errorMessage != null -> {
                Text(
                    text = "Error: ${uiState.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                DashboardContent(uiState = uiState)
            }
        }
    }
}

@Composable
private fun DashboardContent(uiState: HomeUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Learning Course Card ---
        SummaryCard(
            icon = Icons.Default.Class,
            title = "Learning Courses",
            value = uiState.enrolledCourses.size.toString(),
            color = MaterialTheme.colorScheme.primary
        )

        // --- Attendance Summary Title ---
        Text(
            text = "Attendance Summary",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        // --- Attendance Status Grid (Placeholders) ---
        // This Row contains two cards side-by-side
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryCard(
                icon = Icons.Default.CheckCircle,
                title = "Present",
                value = "0", // Placeholder
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                icon = Icons.Default.History,
                title = "Late",
                value = "0", // Placeholder
                color = Color(0xFFFFA000),
                modifier = Modifier.weight(1f)
            )
        }

        // This Row contains the other two cards
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryCard(
                icon = Icons.Default.DoNotDisturb,
                title = "Absent",
                value = "0", // Placeholder
                color = Color(0xFFD32F2F),
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                icon = Icons.Default.Info,
                title = "Other",
                value = "0", // Placeholder
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryCard(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}