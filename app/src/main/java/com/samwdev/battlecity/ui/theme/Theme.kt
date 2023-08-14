package com.samwdev.battlecity.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = Color.White,
    tertiary = Color(145, 79, 26),
    secondary = Color(97, 20, 9),
)

private val LightColorPalette = lightColorScheme(
    primary = Purple500,
    tertiary = Purple700,
    secondary = Teal200
)

@Composable
fun BattleCityTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}