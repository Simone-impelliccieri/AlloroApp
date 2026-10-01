package app.alloro

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StoreCodecTest {
    @Test fun examsSurviveARoundTrip() {
        val exams = listOf(
            Exam("a", "Analisi \"1\" – limiti", "Prof.ssa Rossi", 9, LocalDate.of(2026, 2, 3), ExamResult.Graded(30, honors = true)),
            Exam("b", "Inglese B2", "", 3, LocalDate.of(2025, 12, 31), ExamResult.Qualified),
            Exam("c", "Fisica", "Verdi, Bianchi", 6, LocalDate.of(2026, 7, 1), ExamResult.Graded(18))
        )

        assertEquals(exams, StoreCodec.decodeExams(StoreCodec.encodeExams(exams)))
    }

    @Test fun emptyTranscriptSurvivesARoundTrip() {
        assertEquals(emptyList<Exam>(), StoreCodec.decodeExams(StoreCodec.encodeExams(emptyList())))
    }

    @Test fun settingsSurviveARoundTrip() {
        val catalogued = CareerSettings("Psicologia", DegreeLevel.MAGISTRALE, 120, honorsValue = 31)
        val custom = CareerSettings("Scienze dell'Antichità", null, 180)

        assertEquals(catalogued, StoreCodec.decodeSettings(StoreCodec.encodeSettings(catalogued)))
        assertEquals(custom, StoreCodec.decodeSettings(StoreCodec.encodeSettings(custom)))
    }
}
