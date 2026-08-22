package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = IndigoAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E1B4B),
    onPrimaryContainer = Color(0xFFC7D2FE),
    secondary = PurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2E1065),
    onSecondaryContainer = PurpleAccent,
    tertiary = BlueAccent,
    onTertiary = Color.White,
    background = DarkBg,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextLightGray,
    outline = DarkCardBorder,
    outlineVariant = DarkCardBorderSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Force consistent dark cyberpunk-sleek aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
