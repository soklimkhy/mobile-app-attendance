package com.example.stepattendanceapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = LightGrey,
    background = Black,
    surface = DarkGrey,
    surfaceTint = DarkGrey,
    onPrimary = Black,
    onBackground = White,
    onSurface = White,
    primaryContainer = DarkGrey,
    onPrimaryContainer = White
)

private val LightColorScheme = lightColorScheme(
    primary = Black,
    background = LightGrey,
    surface = White,
    surfaceTint = White,
    onPrimary = White,
    onBackground = Black,
    onSurface = Black,
    primaryContainer = LightGrey,
    onPrimaryContainer = Black
)

@Composable
fun StepAttendanceAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
