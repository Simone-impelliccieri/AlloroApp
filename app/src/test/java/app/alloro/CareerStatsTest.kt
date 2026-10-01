package app.alloro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class CareerStatsTest {
    @Test fun weightedMeanUsesCreditsAsWeights() {
        val summary = careerSummary(listOf(graded(30, 6), graded(24, 12)), settings())

        assertEquals(26.0, summary.weightedMean!!, 1e-9)
        assertEquals(27.0, summary.arithmeticMean!!, 1e-9)
    }

    @Test fun qualifiedExamsCountForCreditsButNotForMeans() {
        val summary = careerSummary(listOf(graded(28, 6), qualified(3)), settings())

        assertEquals(9, summary.earnedCredits)
        assertEquals(6, summary.gradedCredits)
        assertEquals(28.0, summary.weightedMean!!, 1e-9)
        assertEquals(28.0, summary.arithmeticMean!!, 1e-9)
        assertEquals(1, summary.qualifiedCount)
    }

    @Test fun honorsUseConfiguredValue() {
        val summary = careerSummary(listOf(graded(30, 6, honors = true), graded(30, 6)), settings(honorsValue = 33))

        assertEquals(31.5, summary.weightedMean!!, 1e-9)
        assertEquals(1, summary.honorsCount)
    }

    @Test fun graduationBaseIsWeightedMeanOutOf110() {
        val summary = careerSummary(listOf(graded(27, 9)), settings())

        assertEquals(99.0, summary.graduationBase!!, 1e-9)
    }

    @Test fun emptyTranscriptHasNoMeans() {
        val summary = careerSummary(emptyList(), settings(totalCredits = 120))

        assertEquals(0, summary.earnedCredits)
        assertEquals(120, summary.remainingCredits)
        assertNull(summary.arithmeticMean)
        assertNull(summary.weightedMean)
        assertNull(summary.graduationBase)
    }

    @Test fun onlyQualifiedExamsHaveNoMeans() {
        val summary = careerSummary(listOf(qualified(3)), settings())

        assertEquals(3, summary.earnedCredits)
        assertNull(summary.weightedMean)
    }

    @Test fun remainingCreditsNeverGoBelowZero() {
        val summary = careerSummary(listOf(graded(25, 12)), settings(totalCredits = 6))

        assertEquals(0, summary.remainingCredits)
    }

    @Test fun historyIsChronologicalAndSkipsQualifiedExams() {
        val exams = listOf(
            graded(24, 6, date = LocalDate.of(2026, 6, 1)),
            qualified(3, date = LocalDate.of(2026, 3, 1)),
            graded(30, 6, date = LocalDate.of(2026, 2, 1))
        )

        val history = weightedMeanHistory(exams, settings())

        assertEquals(listOf(30, 24), history.map { it.value })
        assertEquals(listOf(30.0, 27.0), history.map { it.weightedMean })
        assertEquals(LocalDate.of(2026, 2, 1), history.first().date)
    }

    @Test fun historyPlotsHonorsWithTheirValueButKeepsTheResult() {
        val point = weightedMeanHistory(listOf(graded(30, 6, honors = true)), settings(honorsValue = 31)).single()

        assertEquals(31, point.value)
        assertEquals(ExamResult.Graded(30, honors = true), point.result)
    }
}
