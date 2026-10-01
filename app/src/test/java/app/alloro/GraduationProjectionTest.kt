package app.alloro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GraduationProjectionTest {
    @Test fun expectedMeanEqualToCurrentKeepsTheBase() {
        val projection = projectGraduation(listOf(graded(27, 60)), settings(), expectedMean = 27.0, thesisPoints = 0)!!

        assertEquals(27.0, projection.projectedMean, 1e-9)
        assertEquals(99.0, projection.projectedBase, 1e-9)
        assertEquals(99, projection.finalGrade)
        assertFalse(projection.reaches110)
    }

    @Test fun remainingCreditsAreWeightedWithTheExpectedMean() {
        val projection = projectGraduation(listOf(graded(30, 90)), settings(), expectedMean = 18.0, thesisPoints = 0)!!

        assertEquals(24.0, projection.projectedMean, 1e-9)
        assertEquals(88.0, projection.projectedBase, 1e-9)
    }

    @Test fun thesisPointsAreAddedAndRoundedHalfUp() {
        // Media 28,5 su tutti i CFU: base 104,5.
        val projection = projectGraduation(listOf(graded(28, 90), graded(29, 90)), settings(), expectedMean = 30.0, thesisPoints = 3)!!

        assertEquals(104.5, projection.projectedBase, 1e-9)
        assertEquals(108, projection.finalGrade)
    }

    @Test fun finalGradeIsCappedAt110() {
        val projection = projectGraduation(listOf(graded(30, 60)), settings(), expectedMean = 30.0, thesisPoints = 5)!!

        assertEquals(110, projection.finalGrade)
        assertTrue(projection.reaches110)
    }

    @Test fun withNoRemainingCreditsTheExpectedMeanIsIgnored() {
        val projection = projectGraduation(listOf(graded(24, 12)), settings(totalCredits = 12), expectedMean = 30.0, thesisPoints = 0)!!

        assertEquals(24.0, projection.projectedMean, 1e-9)
    }

    @Test fun withoutGradesTheExpectedMeanDecides() {
        val projection = projectGraduation(emptyList(), settings(), expectedMean = 27.0, thesisPoints = 0)!!

        assertEquals(99.0, projection.projectedBase, 1e-9)
    }

    @Test fun nothingToWeighGivesNoProjection() {
        assertNull(projectGraduation(listOf(qualified(3)), settings(totalCredits = 3), expectedMean = 27.0, thesisPoints = 4))
    }
}
