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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext

private enum class RootTab(val label: String, val icon: ImageVector) {
    OVERVIEW("Riepilogo", Icons.Outlined.Home),
    TRANSCRIPT("Libretto", Icons.AutoMirrored.Outlined.MenuBook),
    STATISTICS("Statistiche", Icons.Outlined.BarChart)
}

@Composable
fun AlloroApp() {
    val context = LocalContext.current
    val store = remember { LocalStore(context.applicationContext) }
    var settings by remember { mutableStateOf(store.settings) }
    var exams by remember { mutableStateOf(store.exams()) }
    val saveSettings = { chosen: CareerSettings -> store.settings = chosen; settings = chosen }

    when (val current = settings) {
        null -> OnboardingScreen(onDone = saveSettings)
        else -> MainScaffold(
            exams = exams,
            settings = current,
            onSettingsChange = saveSettings,
            onSaveExam = { exam ->
                if (!store.add(exam)) store.update(exam)
                exams = store.exams()
            },
            onDeleteExam = { exam ->
                store.remove(exam)
                exams = store.exams()
            }
        )
    }
}

@Composable
private fun MainScaffold(
    exams: List<Exam>,
    settings: CareerSettings,
    onSettingsChange: (CareerSettings) -> Unit,
    onSaveExam: (Exam) -> Unit,
    onDeleteExam: (Exam) -> Unit
) {
    var tab by rememberSaveable { mutableStateOf(RootTab.OVERVIEW) }
    var settingsOpen by rememberSaveable { mutableStateOf(false) }
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    // Esame in modifica; `null` con l'editor aperto significa nuovo esame.
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    val openEditor = { exam: Exam? -> editingId = exam?.id; editorOpen = true }
    val closeEditor = { editorOpen = false; editingId = null }

    BackHandler(enabled = settingsOpen || editorOpen) {
        if (editorOpen) closeEditor() else settingsOpen = false
    }

    Scaffold(
        bottomBar = {
            if (!settingsOpen && !editorOpen) NavigationBar {
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
                editorOpen -> ExamEditorScreen(
                    initial = exams.find { it.id == editingId },
                    onSave = { onSaveExam(it); closeEditor() },
                    onDelete = { onDeleteExam(it); closeEditor() },
                    onClose = closeEditor
                )
                settingsOpen -> SettingsScreen(
                    settings = settings,
                    onChange = onSettingsChange,
                    onBack = { settingsOpen = false }
                )
                tab == RootTab.OVERVIEW -> OverviewScreen(
                    exams = exams,
                    settings = settings,
                    onOpenSettings = { settingsOpen = true },
                    onAddExam = { openEditor(null) },
                    onOpenExam = openEditor
                )
                tab == RootTab.TRANSCRIPT -> TranscriptScreen(
                    exams = exams,
                    onAddExam = { openEditor(null) },
                    onOpenExam = openEditor
                )
                tab == RootTab.STATISTICS -> StatisticsScreen(exams = exams, settings = settings)
            }
        }
    }
}
