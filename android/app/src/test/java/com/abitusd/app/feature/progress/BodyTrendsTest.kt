package com.abitusd.app.feature.progress

import com.abitusd.app.feature.progress.domain.BodyTrends
import com.abitusd.app.feature.progress.domain.DayValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BodyTrendsTest {
    @Test
    fun smoothingAndWeeklyRate() {
        // -0.1 kg per day for two weeks: -0.7 kg per week.
        val points = (0L..14L).map { DayValue(it, 80.0 - it * 0.1) }
        assertEquals(79.7, BodyTrends.smoothed(points)[6].value, 1e-9)
        assertEquals(-0.7, BodyTrends.weeklyRate(points)!!, 0.05)
        assertFalse(BodyTrends.tooFast(-0.7, 80.0))
        assertTrue(BodyTrends.tooFast(-1.2, 80.0))
    }

    @Test
    fun goalAndIndices() {
        assertEquals(10.0, BodyTrends.weeksToGoal(80.0, 75.0, -0.5)!!, 1e-9)
        assertNull(BodyTrends.weeksToGoal(80.0, 75.0, 0.5))
        assertEquals(24.26, BodyTrends.bmi(74.3, 1.75)!!, 0.01)
        assertEquals(0.4686, BodyTrends.waistToHeight(0.82, 1.75)!!, 0.0001)
        assertEquals(1.5, BodyTrends.relativeStrength(120.0, 80.0)!!, 1e-9)
    }
}
