package app.alloro

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DATE_FORMAT = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ITALIAN)

fun formatDate(date: LocalDate): String = date.format(DATE_FORMAT)

/** Due decimali con la virgola, oppure un trattino quando il valore non è ancora calcolabile. */
fun formatDecimal(value: Double?, decimals: Int = 2): String =
    value?.let { String.format(Locale.ITALIAN, "%.${decimals}f", it) } ?: "—"

fun ExamResult.label(): String = when (this) {
    is ExamResult.Graded -> if (honors) "$grade e lode" else "$grade"
    ExamResult.Qualified -> "Idoneo"
}

fun CareerSettings.degreeLabel(): String =
    listOfNotNull(degreeName, degreeLevel?.label, "$totalCredits CFU").joinToString(" · ")

@Composable
fun AppScreenHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    actionIcon: ImageVector? = null,
    actionDescription: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, Modifier.padding(top = 4.dp), style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (actionIcon != null && onAction != null) {
            AppSurfaceIconButton(actionIcon, actionDescription, onAction, Modifier.padding(start = 12.dp))
        }
    }
}

@Composable
fun AppSurfaceIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier, shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = contentDescription)
        }
    }
}

@Composable
fun AppEmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.School,
    action: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
            Icon(icon, contentDescription = null, Modifier.padding(14.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Text(title, Modifier.padding(top = 12.dp), style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        Text(message, Modifier.padding(top = 4.dp), style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        if (action != null && onAction != null) {
            FilledTonalButton(onClick = onAction, modifier = Modifier.padding(top = 14.dp)) {
                Text(action)
            }
        }
    }
}

@Composable
fun AppSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier.padding(top = 24.dp, bottom = 8.dp), style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold)
}

/** Un valore calcolato con la sua etichetta, nei riquadri di Riepilogo e Statistiche. */
@Composable
fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier, shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
        Column(Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, Modifier.padding(top = 4.dp), style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Riga del libretto: nome, professore, data, CFU ed esito; il tocco apre la modifica. */
@Composable
fun ExamRow(exam: Exam, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(Modifier.clickable(onClick = onClick).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(exam.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                val details = listOfNotNull(
                    exam.professor.takeIf { it.isNotBlank() },
                    formatDate(exam.date),
                    "${exam.credits} CFU"
                ).joinToString(" · ")
                Text(details, Modifier.padding(top = 2.dp), style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Surface(
                Modifier.padding(start = 12.dp),
                shape = RoundedCornerShape(10.dp),
                color = if (exam.result is ExamResult.Graded) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(exam.result.label(), Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
