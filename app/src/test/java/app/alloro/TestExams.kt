package app.alloro

import java.time.LocalDate

private var nextId = 0

fun graded(grade: Int, credits: Int, honors: Boolean = false, date: LocalDate = LocalDate.of(2026, 1, 15)) =
    Exam("e${nextId++}", "Esame", "", credits, date, ExamResult.Graded(grade, honors))

fun qualified(credits: Int, date: LocalDate = LocalDate.of(2026, 1, 15)) =
    Exam("e${nextId++}", "Idoneità", "", credits, date, ExamResult.Qualified)

fun settings(totalCredits: Int = 180, honorsValue: Int = 30) =
    CareerSettings("Informatica", DegreeLevel.TRIENNALE, totalCredits, honorsValue)
