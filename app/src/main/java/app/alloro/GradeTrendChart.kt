package app.alloro

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/** Spazio a sinistra per le etichette dell'asse e margine a destra per l'ultimo punto. */
private val CHART_LEFT = 28.dp
private val CHART_RIGHT = 8.dp

private fun nearestIndex(x: Float, left: Float, right: Float, count: Int): Int =
    if (count == 1) 0 else ((x - left) / ((right - left) / (count - 1))).roundToInt().coerceIn(0, count - 1)

/**
 * Andamento dei voti: ogni voto è un punto neutro, la media ponderata progressiva è la linea
 * d'accento. Un tocco o un trascinamento seleziona l'esame mostrato sopra il grafico.
 */
@Composable
fun GradeTrendChart(points: List<MeanPoint>, maxGrade: Int, modifier: Modifier = Modifier) {
    if (points.isEmpty()) return
    val colors = chartColors()
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val surface = MaterialTheme.colorScheme.surface
    val axisStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
    val textMeasurer = rememberTextMeasurer()
    var selected by rememberSaveable(points.size) { mutableStateOf(points.lastIndex) }
    val point = points[selected.coerceIn(points.indices)]
    val ticks = 18..30 step 3

    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LegendKey(colors.grade, line = false)
            Text("Voto", Modifier.padding(start = 6.dp, end = 16.dp), style = MaterialTheme.typography.bodySmall)
            LegendKey(colors.mean, line = true)
            Text("Media ponderata", Modifier.padding(start = 6.dp), style = MaterialTheme.typography.bodySmall)
        }
        Text(
            "${formatDate(point.date)} · voto ${point.result.label()} · media ${formatDecimal(point.weightedMean)}",
            Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Canvas(
            Modifier.fillMaxWidth().height(180.dp).padding(top = 8.dp)
                .semantics {
                    contentDescription = "Andamento di ${points.size} voti, media ponderata attuale " +
                        formatDecimal(points.last().weightedMean)
                }
                .pointerInput(points) {
                    detectTapGestures { selected = nearestIndex(it.x, CHART_LEFT.toPx(), size.width - CHART_RIGHT.toPx(), points.size) }
                }
                .pointerInput(points) {
                    detectHorizontalDragGestures { change, _ ->
                        selected = nearestIndex(change.position.x, CHART_LEFT.toPx(), size.width - CHART_RIGHT.toPx(), points.size)
                    }
                }
        ) {
            val top = 8.dp.toPx()
            val bottom = size.height - 8.dp.toPx()
            val left = CHART_LEFT.toPx()
            val right = size.width - CHART_RIGHT.toPx()
            fun y(value: Double) = (bottom - (value - 18) / (maxGrade - 18) * (bottom - top)).toFloat()
            fun x(index: Int) = if (points.size == 1) (left + right) / 2 else left + index * (right - left) / (points.size - 1)

            ticks.forEach { tick ->
                val ty = y(tick.toDouble())
                drawLine(gridColor, Offset(left, ty), Offset(right, ty), strokeWidth = 1.dp.toPx())
                val label = textMeasurer.measure("$tick", axisStyle)
                drawText(label, topLeft = Offset(0f, ty - label.size.height / 2f))
            }
            val selectedX = x(selected.coerceIn(points.indices))
            drawLine(gridColor, Offset(selectedX, top), Offset(selectedX, bottom), strokeWidth = 1.dp.toPx())

            if (points.size > 1) {
                val path = Path().apply {
                    points.forEachIndexed { i, p ->
                        if (i == 0) moveTo(x(i), y(p.weightedMean)) else lineTo(x(i), y(p.weightedMean))
                    }
                }
                drawPath(path, colors.mean, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            points.forEachIndexed { i, p ->
                val center = Offset(x(i), y(p.value.toDouble()))
                val radius = if (i == selected) 6.dp.toPx() else 4.dp.toPx()
                drawCircle(surface, radius + 2.dp.toPx(), center)
                drawCircle(colors.grade, radius, center)
            }
            val end = Offset(x(points.lastIndex), y(points.last().weightedMean))
            drawCircle(surface, 6.dp.toPx(), end)
            drawCircle(colors.mean, 4.dp.toPx(), end)
        }
        Row(Modifier.fillMaxWidth().padding(start = CHART_LEFT)) {
            Text(formatDate(points.first().date), Modifier.weight(1f), style = axisStyle)
            if (points.size > 1) Text(formatDate(points.last().date), style = axisStyle)
        }
    }
}

@Composable
private fun LegendKey(color: Color, line: Boolean) {
    Box(Modifier.width(if (line) 16.dp else 8.dp), contentAlignment = Alignment.Center) {
        Box(
            Modifier.then(if (line) Modifier.width(16.dp).height(2.dp) else Modifier.size(8.dp).clip(CircleShape))
                .background(color)
        )
    }
}
