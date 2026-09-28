package app.alloro

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

private enum class RootTab(val label: String, val icon: ImageVector) {
    OVERVIEW("Riepilogo", Icons.Outlined.Home),
    TRANSCRIPT("Libretto", Icons.AutoMirrored.Outlined.MenuBook),
    STATISTICS("Statistiche", Icons.Outlined.BarChart)
}

@Composable
fun AlloroApp() {
    var tab by remember { mutableStateOf(RootTab.OVERVIEW) }
    var settingsOpen by remember { mutableStateOf(false) }
    // TODO: leggere libretto e impostazioni da LocalStore.
    val exams = emptyList<Exam>()
    val settings = CareerSettings()

    BackHandler(enabled = settingsOpen) { settingsOpen = false }

    Scaffold(
        bottomBar = {
            if (!settingsOpen) NavigationBar {
                RootTab.entries.forEach { entry ->
                    NavigationBarItem(
                        selected = tab == entry,
                        onClick = { tab = entry },
                        icon = { Icon(entry.icon, contentDescription = null) },
                        label = { Text(entry.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                settingsOpen -> SettingsScreen(settings = settings, onBack = { settingsOpen = false })
                tab == RootTab.OVERVIEW -> OverviewScreen(
                    exams = exams,
                    settings = settings,
                    onOpenSettings = { settingsOpen = true }
                )
                tab == RootTab.TRANSCRIPT -> TranscriptScreen(
                    exams = exams,
                    onAddExam = { /* TODO: aprire l'inserimento di un esame */ }
                )
                tab == RootTab.STATISTICS -> StatisticsScreen(exams = exams, settings = settings)
            }
        }
    }
}
