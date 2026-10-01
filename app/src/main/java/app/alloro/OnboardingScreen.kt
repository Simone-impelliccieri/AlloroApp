package app.alloro

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private const val OTHER_COURSE = "Altro corso"
private val OTHER_CREDITS_RANGE = 1..400

/** Prima schermata dell'app: senza corso di laurea non si può calcolare nulla. */
@Composable
fun OnboardingScreen(onDone: (CareerSettings) -> Unit) {
    Scaffold { padding ->
        DegreePicker(onChoose = onDone, modifier = Modifier.padding(padding))
    }
}

/**
 * Scelta del corso in due passi: corso, poi livello con i CFU (saltato se il corso ne ha uno
 * solo). "Altro corso" chiede nome e CFU a mano. [onDismiss] mostra un pulsante per annullare.
 */
@Composable
fun DegreePicker(
    onChoose: (CareerSettings) -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    var chosenName by rememberSaveable { mutableStateOf<String?>(null) }
    val course = DEGREE_COURSES.find { it.name == chosenName }

    BackHandler(enabled = chosenName != null) { chosenName = null }

    Column(modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp)) {
        when {
            chosenName == OTHER_COURSE -> OtherCourseStep(onBack = { chosenName = null }, onChoose = onChoose)
            course != null -> LevelStep(course, onBack = { chosenName = null }, onChoose = onChoose)
            else -> CourseStep(onDismiss) { picked ->
                val only = picked?.credits?.entries?.singleOrNull()
                if (only != null) onChoose(CareerSettings(picked.name, only.key, only.value))
                else chosenName = picked?.name ?: OTHER_COURSE
            }
        }
    }
}

/** [onPick] riceve `null` per "Altro corso". */
@Composable
private fun CourseStep(onDismiss: (() -> Unit)?, onPick: (DegreeCourse?) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val matches = DEGREE_COURSES.filter { it.name.contains(query.trim(), ignoreCase = true) }

    AppScreenHeader(
        title = "Cosa studi?",
        subtitle = "Scegli il tuo corso di laurea: serve a calcolare i CFU totali.",
        actionIcon = Icons.Outlined.Close,
        actionDescription = "Annulla",
        onAction = onDismiss
    )
    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        placeholder = { Text("Cerca corso") },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        singleLine = true
    )
    LazyColumn(Modifier.fillMaxWidth().padding(top = 8.dp).imePadding()) {
        items(matches, key = { it.name }) { course ->
            ListItem(
                headlineContent = { Text(course.name) },
                supportingContent = { Text(course.credits.keys.joinToString(" · ") { it.label }) },
                modifier = Modifier.clickable { onPick(course) }
            )
            HorizontalDivider()
        }
        item {
            ListItem(
                headlineContent = { Text(OTHER_COURSE) },
                supportingContent = { Text("Il mio corso non è in elenco: inserisco nome e CFU") },
                modifier = Modifier.clickable { onPick(null) }
            )
        }
    }
}

@Composable
private fun LevelStep(course: DegreeCourse, onBack: () -> Unit, onChoose: (CareerSettings) -> Unit) {
    var level by rememberSaveable(course.name) { mutableStateOf<DegreeLevel?>(null) }

    AppScreenHeader(
        title = course.name,
        subtitle = "Quale livello stai frequentando?",
        actionIcon = Icons.AutoMirrored.Outlined.ArrowBack,
        actionDescription = "Indietro",
        onAction = onBack
    )
    Column(Modifier.padding(top = 16.dp)) {
        course.credits.forEach { (entry, credits) ->
            Row(
                Modifier.fillMaxWidth()
                    .selectable(selected = level == entry, onClick = { level = entry }, role = Role.RadioButton)
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = level == entry, onClick = null)
                Text(entry.label, Modifier.weight(1f).padding(start = 12.dp))
                Text("$credits CFU")
            }
        }
    }
    Button(
        onClick = { level?.let { onChoose(CareerSettings(course.name, it, course.credits.getValue(it))) } },
        enabled = level != null,
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
    ) { Text("Continua") }
}

@Composable
private fun OtherCourseStep(onBack: () -> Unit, onChoose: (CareerSettings) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var credits by rememberSaveable { mutableStateOf("") }
    val parsedCredits = credits.toIntOrNull()?.takeIf { it in OTHER_CREDITS_RANGE }

    AppScreenHeader(
        title = OTHER_COURSE,
        subtitle = "Scrivi il nome del corso e quanti CFU servono per laurearsi.",
        actionIcon = Icons.AutoMirrored.Outlined.ArrowBack,
        actionDescription = "Indietro",
        onAction = onBack
    )
    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        label = { Text("Nome del corso") },
        singleLine = true
    )
    OutlinedTextField(
        value = credits,
        onValueChange = { value -> credits = value.filter(Char::isDigit).take(3) },
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        label = { Text("CFU totali") },
        isError = credits.isNotEmpty() && parsedCredits == null,
        supportingText = { Text("Di solito 180 (triennale), 120 (magistrale), 300 o 360 (ciclo unico)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )
    Button(
        onClick = { parsedCredits?.let { onChoose(CareerSettings(name.trim(), null, it)) } },
        enabled = name.isNotBlank() && parsedCredits != null,
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
    ) { Text("Continua") }
}
