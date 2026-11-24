package com.abitusd.app.feature.train.domain

import com.abitusd.app.core.database.planning.BlockPhase
import com.abitusd.app.core.database.planning.Progression
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PeriodizationTest {
    private val accumulation = BlockSpec("a", 0, BlockPhase.ACCUMULATION, 3, Progression.PERCENT, 2.5)
    private val deload = BlockSpec("d", 1, BlockPhase.DELOAD, 1, Progression.NONE, null)
    private val mesocycle = listOf(deload, accumulation)
    private val base = Target(sets = 5, repsMin = 6, repsMax = 8, loadKg = 100.0, rpe = 8.0)

    @Test
    fun fourWeekMesocycleMapsWeeksToBlocks() {
        assertEquals(4, BlockPlan.totalWeeks(mesocycle))
        assertEquals("a", BlockPlan.at(mesocycle, 0)?.block?.id)
        assertEquals(2, BlockPlan.at(mesocycle, 2)?.weekInBlock)
        assertEquals("d", BlockPlan.at(mesocycle, 3)?.block?.id)
        assertEquals("d", BlockPlan.at(mesocycle, 9)?.block?.id)
        assertNull(BlockPlan.at(mesocycle, -1))
        assertEquals(listOf(0, 3), BlockPlan.starts(mesocycle).map { it.second })
    }

    @Test
    fun programWeekCountsFromStart() {
        assertEquals(0, BlockPlan.programWeek(100, 106))
        assertEquals(1, BlockPlan.programWeek(100, 107))
        assertEquals(-1, BlockPlan.programWeek(100, 99))
    }

    @Test
    fun progressionWeekByWeek() {
        val loads = (0..2).map { ProgressionRule.apply(base, BlockPlan.at(mesocycle, it)!!).loadKg }
        assertEquals(listOf(100.0, 100.0, 105.0), loads)
        val deloaded = ProgressionRule.apply(base, BlockPlan.at(mesocycle, 3)!!)
        assertEquals(3, deloaded.sets)
        assertEquals(85.0, deloaded.loadKg!!, 0.0)
    }

    @Test
    fun rpeProgressionCapsAtTen() {
        val block = BlockSpec("r", 0, BlockPhase.INTENSIFICATION, 6, Progression.RPE, null)
        assertEquals(9.0, ProgressionRule.apply(base, BlockWeek(block, 2, 2)).rpe!!, 0.0)
        assertEquals(10.0, ProgressionRule.apply(base, BlockWeek(block, 5, 5)).rpe!!, 0.0)
    }

    @Test
    fun doubleProgressionNeedsEveryTopSet() {
        assertEquals(102.5, ProgressionRule.doubleProgression(listOf(8, 8, 9), 8, 100.0, 2.5)!!, 0.0)
        assertEquals(100.0, ProgressionRule.doubleProgression(listOf(8, 7, 8), 8, 100.0, 2.5)!!, 0.0)
    }

    @Test
    fun testWeekRecalculatesLoad() {
        assertEquals(102.5, ProgressionRule.fromTest(120.0, 5), 0.0)
        assertEquals(85.0, ProgressionRule.fromTest(120.0, 5, 85.0), 0.0)
        assertEquals(102.5, ProgressionRule.afterTest(listOf(5 to 100.0, 3 to 110.0), 5)!!, 0.0)
        assertNull(ProgressionRule.afterTest(listOf(8 to 0.0), 5))
    }

    @Test
    fun weekFitKeepsOrderOnAvailableDays() {
        val week = listOf(PlannedDay("B", 2), PlannedDay("A", 0), PlannedDay("C", 4))
        val result = WeekFit.fit(week, setOf(5, 1))
        assertEquals(listOf("A" to 1, "B" to 5), result.placed.map { it.item to it.dayIndex })
        assertEquals(listOf("C"), result.unplaced)
    }

    @Test
    fun deloadSuggestedAfterThreeHardSessions() {
        assertTrue(DeloadAdvisor.suggest(listOf(7.0, 9.0, 9.5, 9.0)))
        assertFalse(DeloadAdvisor.suggest(listOf(9.0, 9.0)))
        assertFalse(DeloadAdvisor.suggest(listOf(9.0, 8.0, 9.0)))
    }

    @Test
    fun swapPrefersBlockThenFittingAlternative() {
        val catalog =
            mapOf(
                "bench" to SwapCandidate("bench", setOf("barbell", "bench")),
                "db" to SwapCandidate("db", setOf("dumbbell")),
                "pushup" to SwapCandidate("pushup", emptySet()),
                "row" to SwapCandidate("row", setOf("barbell")),
                "invrow" to SwapCandidate("invrow", setOf("bar")),
            )
        val related = mapOf("bench" to listOf("db", "pushup"), "row" to listOf("invrow"))
        val travel = ExerciseSwap.resolve(listOf("bench", "row"), emptyMap(), catalog, related, emptySet())
        assertEquals(mapOf("bench" to "pushup"), travel)
        val block = ExerciseSwap.resolve(listOf("bench"), mapOf("bench" to "db"), catalog, related, null)
        assertEquals(mapOf("bench" to "db"), block)
        val gym = ExerciseSwap.resolve(listOf("bench"), emptyMap(), catalog, related, setOf("barbell"), excludes = true)
        assertEquals(mapOf("bench" to "db"), gym)
    }
}
