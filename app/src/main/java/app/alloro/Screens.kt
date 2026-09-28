package app.alloro

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OverviewScreen(exams: List<Exam>, settings: CareerSettings, onOpenSettings: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        AppScreenHeader(
            title = "Riepilogo",
            subtitle = "${settings.totalCredits} CFU previsti dal corso di laurea",
            actionIcon = Icons.Outlined.Settings,
            actionDescription = "Preferenze",
            onAction = onOpenSettings
        )
        if (exams.isEmpty()) {
            AppEmptyState(
                title = "Nessun progresso da mostrare",
                message = "Aggiungi i tuoi esami nel Libretto per vedere CFU e medie."
            )
        } else {
            // TODO: CFU acquisiti, medie e prossimi esami da careerSummary.
        }
    }
}

@Composable
fun StatisticsScreen(exams: List<Exam>, settings: CareerSettings) {
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        AppScreenHeader(title = "Statistiche", subtitle = "Medie, andamento dei voti e base di laurea.")
        if (exams.none { it.status == ExamStatus.PASSED }) {
            AppEmptyState(
                title = "Ancora nessun voto",
                message = "Le statistiche compaiono dopo il primo esame superato.",
                icon = Icons.Outlined.BarChart
            )
        } else {
            // TODO: medie e base di laurea da careerSummary(exams, settings).
        }
    }
}

@Composable
fun SettingsScreen(settings: CareerSettings, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        AppScreenHeader(
            title = "Preferenze",
            subtitle = "Parametri usati per il calcolo di CFU e medie.",
            actionIcon = Icons.Outlined.Close,
            actionDescription = "Chiudi",
            onAction = onBack
        )
        // TODO: rendere modificabili i valori e salvarli con LocalStore.
        Text("CFU totali: ${settings.totalCredits}", Modifier.padding(top = 20.dp))
        Text("Valore della lode: ${settings.honorsValue}", Modifier.padding(top = 8.dp))
    }
}
