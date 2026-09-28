package app.alloro

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF3F6B3A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6EBCF),
    onPrimaryContainer = Color(0xFF0F2A0C),
    secondary = Color(0xFF8A6D1F),
    secondaryContainer = Color(0xFFF3E3B5),
    background = Color(0xFFF7F8F3),
    surface = Color(0xFFF7F8F3),
    surfaceContainer = Color(0xFFEDEFE7),
    surfaceContainerLow = Color(0xFFF1F3EC),
    onSurface = Color(0xFF1C2419),
    onSurfaceVariant = Color(0xFF5A6356)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA6D39A),
    onPrimary = Color(0xFF123A10),
    primaryContainer = Color(0xFF2A5025),
    onPrimaryContainer = Color(0xFFD6EBCF),
    secondary = Color(0xFFE3C774),
    secondaryContainer = Color(0xFF5E4B12),
    background = Color(0xFF121711),
    surface = Color(0xFF121711),
    surfaceContainer = Color(0xFF1F261D),
    surfaceContainerLow = Color(0xFF192017),
    onSurface = Color(0xFFE3E8DE),
    onSurfaceVariant = Color(0xFFB3BCAD)
)

@Composable
fun AlloroTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors, content = content)
}
