package com.abitusd.app.feature.library.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OwnExerciseMapperTest {
    private val input =
        OwnExerciseInput(
            id = "11111111-1111-4111-8111-111111111111",
            name = "  Remo con banda ",
            notes = "Banda roja",
            locale = "es",
            disciplineId = 3,
            metricType = "weight_reps",
            movementPattern = "pull",
            difficulty = 2,
            primaryMuscles = setOf(14),
            secondaryMuscles = setOf(14, 4),
            equipmentIds = setOf(7),
            defaultRestSeconds = 90,
        )

    @Test
    fun localRowsArePendingAndTrimmed() {
        val rows = OwnExerciseMapper.rows(input, updatedAt = 1_700_000_000_000, dirty = true)
        assertTrue(rows.exercise.isOwn)
        assertTrue(rows.exercise.dirty)
        assertEquals("Remo con banda", rows.text.name)
        assertEquals("remo con banda", rows.text.searchName)
    }

    @Test
    fun aMuscleIsNeverPrimaryAndSecondaryAtOnce() {
        val rows = OwnExerciseMapper.rows(input, updatedAt = 0, dirty = true)
        assertEquals(listOf(14 to "primary", 4 to "secondary"), rows.muscles.map { it.muscleGroupId to it.role })
    }

    @Test
    fun roundTripThroughTheSyncContract() {
        val rows = OwnExerciseMapper.rows(input, updatedAt = 1_700_000_000_000, dirty = true)
        val dto = OwnExerciseMapper.toDto(rows)
        assertEquals("2023-11-14T22:13:20Z", dto.updatedAt)
        val back = OwnExerciseMapper.fromDto(dto)
        assertEquals(setOf(14), back.primaryMuscles)
        assertEquals(setOf(4), back.secondaryMuscles)
        assertEquals(setOf(7), back.equipmentIds)
        assertEquals("Remo con banda", back.name)
    }
}
