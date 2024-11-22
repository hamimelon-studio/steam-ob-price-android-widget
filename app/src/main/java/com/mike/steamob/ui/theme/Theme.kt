package com.mike.steamob.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberpunkDarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    secondary = NeonBlue,
    tertiary = NeonPink,
    background = DarkBackground,
    surface = CyberGray,
    onPrimary = NeonGreen,
    onSecondary = BrightAccent,
    onTertiary = Color.White,
    onBackground = NeonGreen,
    onSurface = Color.White
)

private val CyberpunkLightColorScheme = lightColorScheme(
    primary = NeonBlue,
    secondary = NeonPink,
    tertiary = BrightAccent,
    background = Color(0xFFF0F0F0),
    surface = Color.White,
    onPrimary = NeonPurple,
    onSecondary = NeonGreen,
    onTertiary = CyberGray,
    onBackground = DarkBackground,
    onSurface = CyberGray
)


@Composable
fun SteamObTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CyberpunkDarkColorScheme else CyberpunkLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Customize typography for modern fonts
        content = content
    )
}