package com.stepan.neoroblox.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object NeoColors {
    val Bg = Color(0xFF070B14)
    val BgTop = Color(0xFF0D1330)
    val Surface = Color(0xFF121826)
    val Violet = Color(0xFF7C5CFF)
    val Blue = Color(0xFF2D7BFF)
    val Teal = Color(0xFF3DDBC9)
    val TextPrimary = Color(0xFFEAF0FF)
    val TextSecondary = Color(0xFF9AA6C4)
    val Danger = Color(0xFFFF6B7A)

    val accentBrush = Brush.horizontalGradient(listOf(Violet, Blue))
    val backgroundBrush = Brush.verticalGradient(listOf(BgTop, Bg))
}

private val NeoDarkScheme = darkColorScheme(
    primary = NeoColors.Violet,
    secondary = NeoColors.Blue,
    tertiary = NeoColors.Teal,
    background = NeoColors.Bg,
    surface = NeoColors.Surface,
    onPrimary = Color.White,
    onBackground = NeoColors.TextPrimary,
    onSurface = NeoColors.TextPrimary,
    error = NeoColors.Danger,
)

@Composable
fun NeoTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = NeoDarkScheme, content = content)
}
