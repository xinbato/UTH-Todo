package com.example.uthtodo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = PureWhite,
    primaryContainer = PrimaryBlueDark,
    background = Color(0xFF121214),
    surface = Color(0xFF1C1C1E),
    surfaceVariant = Color(0xFF2C2C2E),
    outline = Color(0xFF38383A),
    onBackground = PureWhite,
    onSurface = PureWhite,
    onSurfaceVariant = Color(0xFFA1A1A6)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = PureWhite,
    primaryContainer = SoftBlueBg,
    background = Color(0xFFF5F5F7),
    surface = PureWhite,
    surfaceVariant = Color(0xFFE9E9EE),
    outline = BorderGray,
    onBackground = TextDark,
    onSurface = TextDark,
    onSurfaceVariant = TextSecondary
)

val PrimaryColors = listOf(
    Color(0xFF0066FF), // Index 0: Blue
    Color(0xFF0F7A4F), // Index 1: Green
    Color(0xFFEA580C), // Index 2: Orange
    Color(0xFF7C3AED)  // Index 3: Purple
)

@Composable
fun UTHTodoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    primaryColorIndex: Int = 0,
    content: @Composable () -> Unit
) {
    val selectedPrimary = PrimaryColors.getOrElse(primaryColorIndex) { PrimaryColors[0] }
    val baseColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val colorScheme = if (!dynamicColor) {
        baseColorScheme.copy(
            primary = selectedPrimary,
            primaryContainer = if (darkTheme) selectedPrimary.copy(alpha = 0.3f) else selectedPrimary.copy(alpha = 0.12f),
            onPrimaryContainer = if (darkTheme) selectedPrimary.copy(alpha = 0.9f) else selectedPrimary
        )
    } else {
        baseColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
