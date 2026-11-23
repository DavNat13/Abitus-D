package com.abitusd.app.feature.workout.domain

import com.abitusd.app.core.database.session.SetEntity
import com.abitusd.app.core.database.session.SetType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutDomainTest {
    private fun set(
        exercise: String,
        load: Double?,
        reps: Int?,
        type: String = SetType.NORMAL,
    ) = SetEntity(
        id = "$exercise$load$reps$type",
        sessionExerciseId = "e",
        exerciseId = exercise,
        performedAt = 0,
        setIndex = 0,
        setType = type,
        reps = reps,
        loadKg = load,
        completedAt = 1,
        updatedAt = 0,
    )

    @Test
    fun summaryIgnoresWarmupsForVolumeAndFindsRecords() {
        val done = listOf(set("bench", 40.0, 10, SetType.WARMUP), set("bench", 80.0, 8), set("row", 60.0, 10))
        val s = SessionStats.summary(0, 3_600_000, done, mapOf("bench" to 95.0, "row" to 90.0))
        assertEquals(60, s.durationMin)
        assertEquals(80.0 * 8 + 60.0 * 10, s.volumeKg, 0.001)
        assertEquals(3, s.sets)
        assertEquals(listOf("bench"), s.records.map { it.exerciseId })
    }

    @Test
    fun firstTimeExerciseIsNotARecordAndBodyweightHasNoMax() {
        val s = SessionStats.summary(0, 0, listOf(set("dip", 20.0, 8)), emptyMap())
        assertTrue(s.records.isEmpty())
        assertNull(SessionStats.estimatedMax(set("pullup", null, 10)))
    }

    @Test
    fun platesPerSideAndInexactTargets() {
        val r = PlateCalculator.perSide(100.0, 20.0)
        assertEquals(listOf(25.0, 15.0), r.perSide)
        assertTrue(r.exact(100.0))
        val odd = PlateCalculator.perSide(101.0, 20.0)
        assertFalse(odd.exact(101.0))
        assertEquals(100.0, odd.reachedKg, 0.001)
    }

    @Test
    fun undoAndRedoReturnTheRightRows() {
        val stack = UndoStack()
        val before = set("bench", 80.0, 8)
        val after = before.copy(completedAt = 5)
        stack.record(SetChange(before, after))
        assertEquals(before, stack.undo())
        assertEquals(after, stack.redo())
        assertTrue(stack.canUndo)
        assertFalse(stack.canRedo)
    }
}
