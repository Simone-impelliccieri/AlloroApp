package app.alloro

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TranscriptScreen(exams: List<Exam>, onAddExam: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        AppScreenHeader(
            title = "Libretto",
            subtitle = if (exams.isEmpty()) "Gli esami del tuo piano di studi."
                else "${exams.size} ${if (exams.size == 1) "esame" else "esami"}",
            actionIcon = Icons.Outlined.Add,
            actionDescription = "Aggiungi esame",
            onAction = onAddExam
        )
        if (exams.isEmpty()) {
            AppEmptyState(
                title = "Libretto vuoto",
                message = "Aggiungi gli esami superati e quelli ancora da sostenere.",
                icon = Icons.AutoMirrored.Outlined.MenuBook,
                action = "Aggiungi esame",
                onAction = onAddExam
            )
        } else {
            // TODO: elenco degli esami raggruppati per anno di corso.
        }
    }
}
