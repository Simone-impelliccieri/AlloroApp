package app.alloro

import java.io.Serializable
import java.time.LocalDate

enum class ExamField { NAME, DATE, CREDITS, GRADE }

/** Valori del modulo d'inserimento così come li scrive l'utente, prima della validazione. */
data class ExamDraft(
    val name: String = "",
    val professor: String = "",
    val date: LocalDate,
    val credits: String = "",
    val qualified: Boolean = false,
    val grade: Int? = null,
    val honors: Boolean = false
) : Serializable {

    /** Messaggio d'errore per ogni campo non valido; vuota se la bozza si può salvare. */
    fun errors(today: LocalDate): Map<ExamField, String> = buildMap {
        if (name.isBlank()) put(ExamField.NAME, "Inserisci il nome dell'esame")
        if (date.isAfter(today)) put(ExamField.DATE, "La data non può essere nel futuro")
        if (credits.trim().toIntOrNull() !in 1..30) put(ExamField.CREDITS, "Inserisci un numero di CFU tra 1 e 30")
        if (!qualified) {
            when {
                grade == null || grade !in 18..30 -> put(ExamField.GRADE, "Scegli un voto tra 18 e 30")
                honors && grade != 30 -> put(ExamField.GRADE, "La lode è possibile solo con 30")
            }
        }
    }

    /** L'esame descritto dalla bozza, oppure `null` se ci sono [errors]. */
    fun toExam(id: String, today: LocalDate): Exam? {
        if (errors(today).isNotEmpty()) return null
        return Exam(
            id = id,
            name = name.trim(),
            professor = professor.trim(),
            credits = credits.trim().toInt(),
            date = date,
            result = if (qualified) ExamResult.Qualified else ExamResult.Graded(grade!!, honors)
        )
    }
}

fun Exam.toDraft(): ExamDraft {
    val graded = result as? ExamResult.Graded
    return ExamDraft(
        name = name,
        professor = professor,
        date = date,
        credits = credits.toString(),
        qualified = result == ExamResult.Qualified,
        grade = graded?.grade,
        honors = graded?.honors ?: false
    )
}
