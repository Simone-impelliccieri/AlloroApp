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
    surfaceContainerHigh = Color(0xFFE8EBE2),
    surfaceContainerHighest = Color(0xFFE2E5DC),
    onSurface = Color(0xFF1C2419),
    onSurfaceVariant = Color(0xFF5A6356),
    outline = Color(0xFF737B6E),
    outlineVariant = Color(0xFFC3C9BC)
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
    surfaceContainerHigh = Color(0xFF283026),
    surfaceContainerHighest = Color(0xFF323A30),
    onSurface = Color(0xFFE3E8DE),
    onSurfaceVariant = Color(0xFFB3BCAD),
    outline = Color(0xFF8D9687),
    outlineVariant = Color(0xFF434B40)
)

/**
 * Colori del grafico dell'andamento, verificati per contrasto e daltonismo: l'accento per la
 * media ponderata, un neutro per i singoli voti che fanno da contesto.
 */
data class ChartColors(val mean: Color, val grade: Color)

private val LightChartColors = ChartColors(mean = Color(0xFF3A7A2F), grade = Color(0xFF848C7F))
private val DarkChartColors = ChartColors(mean = Color(0xFF5FA84F), grade = Color(0xFF6A7266))

@Composable
fun chartColors(): ChartColors = if (isSystemInDarkTheme()) DarkChartColors else LightChartColors

@Composable
fun AlloroTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors, content = content)
}
