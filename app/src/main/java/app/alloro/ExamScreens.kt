package app.alloro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.UUID

@Composable
fun TranscriptScreen(exams: List<Exam>, onAddExam: () -> Unit, onOpenExam: (Exam) -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        AppScreenHeader(
            title = "Libretto",
            subtitle = if (exams.isEmpty()) "Gli esami che hai sostenuto."
                else "${exams.size} ${if (exams.size == 1) "esame" else "esami"} · ${exams.sumOf { it.credits }} CFU",
            actionIcon = Icons.Outlined.Add,
            actionDescription = "Aggiungi esame",
            onAction = onAddExam
        )
        if (exams.isEmpty()) {
            AppEmptyState(
                title = "Libretto vuoto",
                message = "Aggiungi gli esami superati, con il voto o come idoneità.",
                icon = Icons.AutoMirrored.Outlined.MenuBook,
                action = "Aggiungi esame",
                onAction = onAddExam
            )
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(top = 12.dp)) {
                items(exams.sortedWith(compareByDescending<Exam> { it.date }.thenBy { it.name }), key = { it.id }) { exam ->
                    ExamRow(exam, onClick = { onOpenExam(exam) })
                }
            }
        }
    }
}

/** Inserimento ([initial] `null`) o modifica di un esame. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExamEditorScreen(
    initial: Exam?,
    onSave: (Exam) -> Unit,
    onDelete: (Exam) -> Unit,
    onClose: () -> Unit
) {
    val today = LocalDate.now()
    var draft by rememberSaveable { mutableStateOf(initial?.toDraft() ?: ExamDraft(date = today)) }
    var showErrors by rememberSaveable { mutableStateOf(false) }
    var pickingDate by rememberSaveable { mutableStateOf(false) }
    var confirmingDelete by rememberSaveable { mutableStateOf(false) }
    val errors = if (showErrors) draft.errors(today) else emptyMap()
    val focusManager = LocalFocusManager.current

    Column(
        Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        AppScreenHeader(
            title = if (initial == null) "Nuovo esame" else "Modifica esame",
            subtitle = "Nome, professore, data, CFU ed esito.",
            actionIcon = Icons.Outlined.Close,
            actionDescription = "Chiudi senza salvare",
            onAction = onClose
        )
        OutlinedTextField(
            value = draft.name,
            onValueChange = { draft = draft.copy(name = it) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            label = { Text("Nome dell'esame") },
            isError = ExamField.NAME in errors,
            supportingText = errors[ExamField.NAME]?.let { { Text(it) } },
            singleLine = true
        )
        OutlinedTextField(
            value = draft.professor,
            onValueChange = { draft = draft.copy(professor = it) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            label = { Text("Professore (facoltativo)") },
            singleLine = true
        )
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = draft.credits,
                onValueChange = { value -> draft = draft.copy(credits = value.filter(Char::isDigit).take(2)) },
                modifier = Modifier.weight(1f),
                label = { Text("CFU") },
                isError = ExamField.CREDITS in errors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedButton(onClick = { focusManager.clearFocus(); pickingDate = true }, modifier = Modifier.weight(1.4f).padding(top = 8.dp)) {
                Icon(Icons.Outlined.CalendarToday, contentDescription = null)
                Text(formatDate(draft.date), Modifier.padding(start = 8.dp))
            }
        }
        listOfNotNull(errors[ExamField.CREDITS], errors[ExamField.DATE]).forEach { FieldError(it) }

        AppSectionTitle("Esito")
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            listOf(false to "Voto", true to "Idoneo").forEachIndexed { index, (qualified, label) ->
                SegmentedButton(
                    selected = draft.qualified == qualified,
                    onClick = { draft = draft.copy(qualified = qualified) },
                    shape = SegmentedButtonDefaults.itemShape(index, 2)
                ) { Text(label) }
            }
        }
        if (!draft.qualified) {
            FlowRow(
                Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (18..30).forEach { grade ->
                    FilterChip(
                        selected = draft.grade == grade,
                        onClick = { draft = draft.copy(grade = grade, honors = draft.honors && grade == 30) },
                        label = { Text("$grade") }
                    )
                }
            }
            Row(
                Modifier.padding(top = 8.dp)
                    .toggleable(
                        value = draft.honors,
                        enabled = draft.grade == 30,
                        role = Role.Checkbox,
                        onValueChange = { draft = draft.copy(honors = it) }
                    )
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = draft.honors, onCheckedChange = null, enabled = draft.grade == 30,
                    modifier = Modifier.padding(12.dp))
                Text("Lode", color = if (draft.grade == 30) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            errors[ExamField.GRADE]?.let { FieldError(it) }
        } else {
            Text("L'esame conta per i CFU ma non entra nelle medie.", Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Button(
            onClick = {
                showErrors = true
                draft.toExam(initial?.id ?: UUID.randomUUID().toString(), today)?.let(onSave)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        ) { Text("Salva") }
        if (initial != null) {
            TextButton(
                onClick = { confirmingDelete = true },
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text("Elimina esame") }
        }
    }

    if (pickingDate) {
        ExamDatePicker(
            initial = draft.date,
            today = today,
            onPick = { draft = draft.copy(date = it); pickingDate = false },
            onDismiss = { pickingDate = false }
        )
    }
    if (confirmingDelete && initial != null) {
        AlertDialog(
            onDismissRequest = { confirmingDelete = false },
            title = { Text("Eliminare l'esame?") },
            text = { Text("\"${initial.name}\" verrà tolto dal libretto.") },
            confirmButton = {
                TextButton(onClick = { confirmingDelete = false; onDelete(initial) }) { Text("Elimina") }
            },
            dismissButton = { TextButton(onClick = { confirmingDelete = false }) { Text("Annulla") } }
        )
    }
}

@Composable
private fun FieldError(message: String) {
    Text(message, Modifier.padding(start = 16.dp, top = 4.dp), style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error)
}

private fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toUtcDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamDatePicker(initial: LocalDate, today: LocalDate, onPick: (LocalDate) -> Unit, onDismiss: () -> Unit) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initial.toUtcMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= today.toUtcMillis()
            override fun isSelectableYear(year: Int) = year <= today.year
        }
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { state.selectedDateMillis?.let { onPick(it.toUtcDate()) } ?: onDismiss() }
            ) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annulla") } }
    ) {
        DatePicker(state)
    }
}
