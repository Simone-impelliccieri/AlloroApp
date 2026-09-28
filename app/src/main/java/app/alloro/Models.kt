package app.alloro

import java.time.LocalDate

enum class ExamStatus(val label: String) {
    PLANNED("Da sostenere"),
    PASSED("Superato"),
    QUALIFIED("Idoneità")
}

/** [grade] (18..30) e [honors] hanno senso solo per gli esami [ExamStatus.PASSED]. */
data class Exam(
    val id: String,
    val name: String,
    val credits: Int,
    val courseYear: Int,
    val status: ExamStatus = ExamStatus.PLANNED,
    val grade: Int? = null,
    val honors: Boolean = false,
    val date: LocalDate? = null
)

data class CareerSettings(
    val totalCredits: Int = 180,
    val honorsValue: Int = 30
)

data class CareerSummary(
    val earnedCredits: Int,
    val totalCredits: Int,
    val arithmeticMean: Double?,
    val weightedMean: Double?,
    val graduationBase: Double?
)
