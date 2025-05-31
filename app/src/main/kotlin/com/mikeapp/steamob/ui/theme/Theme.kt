package com.mikeapp.steamob.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CyberpunkLightColorScheme = lightColorScheme(
    primary = GameExtremeDarkBlue,
    onPrimary = GameWhite,
    secondary = GameExtremeDarkBlue,
    onSecondary = GameGlowingWhite,
    tertiary = GameDarkRed,
    onTertiary = GameGlowingRed,
    background = GameWhite,
    onBackground = GameExtremeDarkBlue,
    surface = GameWhite,
    onSurface = GameDarkBlue,
    primaryContainer = GameDarkBlue, // Game card bg + fab bg
    onPrimaryContainer = GameWhite,
    secondaryContainer = GameWhite, // dialog bg
    tertiaryContainer = GameWhite, // Fab bg
    surfaceContainer = GameWhite, // nav bottom bar bg
)

private val CyberpunkDarkColorScheme = darkColorScheme(
    primary = GameGlowingWhite,
    onPrimary = GameExtremeDarkBlue,
    secondary = GameGlowingWhite,
    onSecondary = GameExtremeDarkBlue,
    tertiary = GameGlowingRed,
    onTertiary = GameDarkRed,
    background = GameExtremeDarkBlue,
    onBackground = GameWhite,
    surface = GameExtremeDarkBlue,
    onSurface = GameWhite,
    primaryContainer = GameGlowingWhite,
    onPrimaryContainer = GameExtremeDarkBlue,
    secondaryContainer = GameDarkBlue,
    tertiaryContainer = GameGlowingWhite,
    surfaceContainer = GameExtremeDarkBlue
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