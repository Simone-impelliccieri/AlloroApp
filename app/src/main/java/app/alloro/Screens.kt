package app.alloro

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private const val MAX_THESIS_POINTS = 11

@Composable
fun OverviewScreen(
    exams: List<Exam>,
    settings: CareerSettings,
    onOpenSettings: () -> Unit,
    onAddExam: () -> Unit,
    onOpenExam: (Exam) -> Unit
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp)) {
        AppScreenHeader(
            title = "Riepilogo",
            subtitle = settings.degreeLabel(),
            actionIcon = Icons.Outlined.Settings,
            actionDescription = "Preferenze",
            onAction = onOpenSettings
        )
        if (exams.isEmpty()) {
            AppEmptyState(
                title = "Nessun progresso da mostrare",
                message = "Aggiungi i tuoi esami per vedere CFU, medie e base di laurea.",
                action = "Aggiungi esame",
                onAction = onAddExam
            )
            return@Column
        }
        val summary = careerSummary(exams, settings)
        CreditsProgress(summary, Modifier.padding(top = 20.dp))
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile("Media ponderata", formatDecimal(summary.weightedMean), Modifier.weight(1f))
            StatTile("Base di laurea", formatDecimal(summary.graduationBase), Modifier.weight(1f))
        }
        AppSectionTitle("Ultimi esami")
        exams.sortedByDescending { it.date }.take(3).forEach { exam ->
            ExamRow(exam, onClick = { onOpenExam(exam) })
        }
    }
}

@Composable
private fun CreditsProgress(summary: CareerSummary, modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
        Column(Modifier.padding(16.dp)) {
            Text("CFU acquisiti", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${summary.earnedCredits} / ${summary.totalCredits}", Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            LinearProgressIndicator(
                progress = { (summary.earnedCredits.toFloat() / summary.totalCredits).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )
            Text(
                if (summary.remainingCredits == 0) "Hai raggiunto tutti i CFU del corso."
                    else "Mancano ${summary.remainingCredits} CFU.",
                Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatisticsScreen(exams: List<Exam>, settings: CareerSettings) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp)) {
        AppScreenHeader(title = "Statistiche", subtitle = "Medie, andamento dei voti e proiezione del voto di laurea.")
        if (exams.isEmpty()) {
            AppEmptyState(
                title = "Ancora nessun esame",
                message = "Le statistiche compaiono dopo il primo esame inserito nel Libretto.",
                icon = Icons.Outlined.BarChart
            )
            return@Column
        }
        val summary = careerSummary(exams, settings)
        val tiles = listOf(
            "CFU acquisiti" to "${summary.earnedCredits} / ${summary.totalCredits}",
            "Esami" to "${exams.size}",
            "Media aritmetica" to formatDecimal(summary.arithmeticMean),
            "Media ponderata" to formatDecimal(summary.weightedMean),
            "Base di laurea" to formatDecimal(summary.graduationBase),
            "Lodi" to "${summary.honorsCount}",
            "Idoneità" to "${summary.qualifiedCount}"
        )
        Column(Modifier.padding(top = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            tiles.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { (label, value) -> StatTile(label, value, Modifier.weight(1f)) }
                    if (row.size == 1) Column(Modifier.weight(1f)) {}
                }
            }
        }

        val history = weightedMeanHistory(exams, settings)
        if (history.isNotEmpty()) {
            AppSectionTitle("Andamento")
            GradeTrendChart(history, maxGrade = maxOf(30, settings.honorsValue))
        }

        AppSectionTitle("Proiezione voto di laurea")
        GraduationProjectionCard(exams, settings, summary)
    }
}

@Composable
private fun GraduationProjectionCard(exams: List<Exam>, settings: CareerSettings, summary: CareerSummary) {
    val maxMean = maxOf(30, settings.honorsValue).toFloat()
    var expectedMean by rememberSaveable {
        mutableFloatStateOf((summary.weightedMean ?: 25.0).toFloat().coerceIn(18f, maxMean))
    }
    var thesisPoints by rememberSaveable { mutableIntStateOf(0) }
    val projection = projectGraduation(exams, settings, expectedMean.toDouble(), thesisPoints)

    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
        Column(Modifier.padding(16.dp)) {
            if (projection == null) {
                Text("Servono esami con voto o CFU ancora da acquisire per stimare il voto di laurea.",
                    style = MaterialTheme.typography.bodyMedium)
                return@Column
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text("${projection.finalGrade}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.SemiBold)
                Text(" / 110", Modifier.padding(bottom = 8.dp), style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                "Base proiettata ${formatDecimal(projection.projectedBase)} + $thesisPoints punti tesi",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (projection.reaches110) {
                Text("Arrivi a 110: la lode è a discrezione della commissione.", Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }

            if (summary.remainingCredits > 0) {
                Text("Media prevista negli esami mancanti: ${formatDecimal(expectedMean.toDouble(), 1)}",
                    Modifier.padding(top = 16.dp), style = MaterialTheme.typography.bodyMedium)
                Slider(
                    value = expectedMean,
                    onValueChange = { expectedMean = (it * 2).roundToInt() / 2f },
                    valueRange = 18f..maxMean,
                    steps = ((maxMean - 18f) * 2).toInt() - 1
                )
            }
            Text("Punti tesi: $thesisPoints", Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = thesisPoints.toFloat(),
                onValueChange = { thesisPoints = it.roundToInt() },
                valueRange = 0f..MAX_THESIS_POINTS.toFloat(),
                steps = MAX_THESIS_POINTS - 1
            )
            Text(
                "Stima: i ${summary.remainingCredits} CFU mancanti (comprese prova finale e idoneità) sono " +
                    "contati come esami con la media prevista. Punti tesi e arrotondamento cambiano da ateneo ad ateneo.",
                Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsScreen(settings: CareerSettings, onChange: (CareerSettings) -> Unit, onBack: () -> Unit) {
    var changingDegree by rememberSaveable { mutableStateOf(false) }

    if (changingDegree) {
        BackHandler { changingDegree = false }
        DegreePicker(
            onChoose = { chosen ->
                onChange(chosen.copy(honorsValue = settings.honorsValue))
                changingDegree = false
            },
            onDismiss = { changingDegree = false }
        )
        return
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp)) {
        AppScreenHeader(
            title = "Preferenze",
            subtitle = "Parametri usati per il calcolo di CFU e medie.",
            actionIcon = Icons.Outlined.Close,
            actionDescription = "Chiudi",
            onAction = onBack
        )
        AppSectionTitle("Corso di laurea")
        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
            Row(Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(settings.degreeName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(
                        listOfNotNull(settings.degreeLevel?.label, "${settings.totalCredits} CFU").joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = { changingDegree = true }) { Text("Cambia") }
            }
        }
        AppSectionTitle("Valore della lode")
        Text("Quanto vale un 30 e lode nel calcolo delle medie.", style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (30..33).forEach { value ->
                FilterChip(
                    selected = settings.honorsValue == value,
                    onClick = { onChange(settings.copy(honorsValue = value)) },
                    label = { Text("$value") }
                )
            }
        }
    }
}
