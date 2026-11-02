package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PythonBlue,
    onPrimary = BackgroundDark,
    primaryContainer = PythonBlueDark,
    onPrimaryContainer = Color.White,
    secondary = PythonYellow,
    onSecondary = BackgroundDark,
    secondaryContainer = PythonYellowDark,
    onSecondaryContainer = Color.White,
    tertiary = SnakeGreen,
    onTertiary = BackgroundDark,
    tertiaryContainer = SnakeGreenDark,
    onTertiaryContainer = Color.White,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorderDark,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Keep developer dark aesthetic consistent
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
