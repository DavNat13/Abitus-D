package com.abitusd.app.feature.workout.domain

import com.abitusd.app.core.database.planning.RoutineExerciseEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionFactoryTest {
    private var counter = 0
    private val factory = SessionFactory { "id${counter++}" }

    private fun planned(
        exerciseId: String,
        position: Int,
        sets: Int?,
    ) = RoutineExerciseEntity(
        id = "r$position",
        routineId = "routine",
        exerciseId = exerciseId,
        position = position,
        targetSets = sets,
        targetRepsMin = 8,
        targetRepsMax = 12,
        targetLoadKg = 60.0,
        restSeconds = 90,
        updatedAt = 0,
    )

    @Test
    fun copiesOrderAndTargetsIntoPendingSets() {
        val built =
            factory.fromRoutine(
                "Torso",
                "routine",
                null,
                listOf(planned("bench", 1, 2), planned("squat", 0, 3)),
                now = 100,
            )
        assertEquals(listOf("squat", "bench"), built.exercises.map { it.exerciseId })
        assertEquals(5, built.sets.size)
        val first = built.sets.first()
        assertEquals(12, first.reps)
        assertEquals(60.0, first.loadKg)
        assertEquals(90, first.restS)
        assertNull(first.completedAt)
        assertTrue(built.sets.all { it.dirty })
    }

    @Test
    fun missingSetCountUsesThree() {
        val built = factory.fromRoutine("Libre", null, null, listOf(planned("row", 0, null)), now = 0)
        assertEquals(3, built.sets.size)
        assertEquals(listOf(0, 1, 2), built.sets.map { it.setIndex })
    }
}
