package com.abitusd.app.core.sync

import com.abitusd.app.core.database.planning.RoutineExerciseEntity
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

class PlanningCodecTest {
    private val entity =
        RoutineExerciseEntity(
            id = "e1",
            routineId = "r1",
            exerciseId = "x1",
            position = 2,
            targetLoadKg = 62.5,
            updatedAt = 1_700_000_000_000,
            dirty = true,
            serverSeq = 9,
        )

    @Test
    fun wireRowHasNoLocalFieldsAndIsoTime() {
        val wire = PlanningCodec.toWire(RoutineExerciseEntity.serializer(), entity)
        assertFalse("dirty" in wire || "serverSeq" in wire || "deletedAt" in wire)
        assertEquals("2023-11-14T22:13:20Z", wire.getValue("updatedAt").jsonPrimitive.content)
        assertEquals("null", wire.getValue("targetSets").toString())
    }

    @Test
    fun roundTripKeepsValuesAndMarksClean() {
        val wire = PlanningCodec.toWire(RoutineExerciseEntity.serializer(), entity)
        val back = PlanningCodec.fromWire(RoutineExerciseEntity.serializer(), wire, serverSeq = 42, deleted = false)
        assertEquals(entity.copy(dirty = false, serverSeq = 42), back)
    }

    @Test
    fun deletedRowsBecomeTombstones() {
        val wire = PlanningCodec.toWire(RoutineExerciseEntity.serializer(), entity)
        val back = PlanningCodec.fromWire(RoutineExerciseEntity.serializer(), wire, serverSeq = 1, deleted = true)
        assertNotNull(back.deletedAt)
    }
}
