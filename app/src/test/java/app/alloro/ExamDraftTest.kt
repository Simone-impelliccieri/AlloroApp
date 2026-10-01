package app.alloro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ExamDraftTest {
    private val today = LocalDate.of(2026, 10, 1)
    private val valid = ExamDraft(name = " Basi di dati ", professor = " Neri ", date = today, credits = "9", grade = 30, honors = true)

    @Test fun validGradedDraftBecomesAnExam() {
        val exam = valid.toExam("id", today)!!

        assertEquals("Basi di dati", exam.name)
        assertEquals("Neri", exam.professor)
        assertEquals(9, exam.credits)
        assertEquals(ExamResult.Graded(30, honors = true), exam.result)
    }

    @Test fun qualifiedDraftIgnoresTheGrade() {
        val exam = valid.copy(qualified = true, grade = null, honors = false).toExam("id", today)!!

        assertEquals(ExamResult.Qualified, exam.result)
    }

    @Test fun professorIsOptional() {
        assertTrue(valid.copy(professor = "").errors(today).isEmpty())
    }

    @Test fun examRoundTripsThroughTheDraft() {
        val exam = valid.toExam("id", today)!!

        assertEquals(exam, exam.toDraft().toExam("id", today))
    }

    @Test fun eachInvalidFieldIsReported() {
        assertEquals(setOf(ExamField.NAME), valid.copy(name = "  ").errors(today).keys)
        assertEquals(setOf(ExamField.DATE), valid.copy(date = today.plusDays(1)).errors(today).keys)
        assertEquals(setOf(ExamField.CREDITS), valid.copy(credits = "").errors(today).keys)
        assertEquals(setOf(ExamField.CREDITS), valid.copy(credits = "0").errors(today).keys)
        assertEquals(setOf(ExamField.CREDITS), valid.copy(credits = "31").errors(today).keys)
        assertEquals(setOf(ExamField.GRADE), valid.copy(grade = null, honors = false).errors(today).keys)
        assertEquals(setOf(ExamField.GRADE), valid.copy(grade = 17, honors = false).errors(today).keys)
        assertEquals(setOf(ExamField.GRADE), valid.copy(grade = 29, honors = true).errors(today).keys)
    }

    @Test fun invalidDraftGivesNoExam() {
        assertNull(valid.copy(name = "").toExam("id", today))
    }
}
