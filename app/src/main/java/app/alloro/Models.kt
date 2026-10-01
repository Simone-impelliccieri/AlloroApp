package app.alloro

import java.time.LocalDate

/** Esito di un esame sostenuto: un voto in trentesimi oppure la sola idoneità. */
sealed interface ExamResult {
    /** [grade] va da 18 a 30; [honors] è ammessa solo con 30. */
    data class Graded(val grade: Int, val honors: Boolean = false) : ExamResult

    data object Qualified : ExamResult
}

data class Exam(
    val id: String,
    val name: String,
    val professor: String,
    val credits: Int,
    val date: LocalDate,
    val result: ExamResult
)

enum class DegreeLevel(val label: String) {
    TRIENNALE("Triennale"),
    MAGISTRALE("Magistrale"),
    CICLO_UNICO("Ciclo unico")
}

/** [degreeLevel] è `null` per un corso inserito a mano ("Altro corso"). */
data class CareerSettings(
    val degreeName: String,
    val degreeLevel: DegreeLevel?,
    val totalCredits: Int,
    val honorsValue: Int = 30
)

data class CareerSummary(
    val earnedCredits: Int,
    val gradedCredits: Int,
    val totalCredits: Int,
    val remainingCredits: Int,
    val arithmeticMean: Double?,
    val weightedMean: Double?,
    val graduationBase: Double?,
    val honorsCount: Int,
    val qualifiedCount: Int
)

data class GraduationProjection(
    val projectedMean: Double,
    val projectedBase: Double,
    val finalGrade: Int,
    /** Base più punti tesi raggiungono 110: la lode dipende dalla commissione. */
    val reaches110: Boolean
)

/** [value] è il voto usato nelle medie: per un 30 e lode vale [CareerSettings.honorsValue]. */
data class MeanPoint(val date: LocalDate, val result: ExamResult.Graded, val value: Int, val weightedMean: Double)
