package app.alloro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DegreeCatalogTest {
    @Test fun catalogHas35UniqueCourses() {
        assertEquals(35, DEGREE_COURSES.size)
        assertEquals(35, DEGREE_COURSES.map { it.name }.toSet().size)
    }

    @Test fun everyCourseHasValidCreditsForItsLevels() {
        val expected = mapOf(
            DegreeLevel.TRIENNALE to setOf(180),
            DegreeLevel.MAGISTRALE to setOf(120),
            DegreeLevel.CICLO_UNICO to setOf(300, 360)
        )
        DEGREE_COURSES.forEach { course ->
            assertTrue("${course.name} senza livelli", course.credits.isNotEmpty())
            course.credits.forEach { (level, credits) ->
                assertTrue("${course.name}: $credits CFU per $level", credits in expected.getValue(level))
            }
        }
    }

    @Test fun catalogIsSortedAlphabetically() {
        assertEquals(DEGREE_COURSES.map { it.name }.sorted(), DEGREE_COURSES.map { it.name })
    }
}
