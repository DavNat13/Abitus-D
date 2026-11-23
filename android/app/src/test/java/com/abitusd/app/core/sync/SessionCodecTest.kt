package com.abitusd.app.core.sync

import com.abitusd.app.core.database.session.SetEntity
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

class SessionCodecTest {
    private val set =
        SetEntity(
            id = "s1",
            sessionExerciseId = "e1",
            exerciseId = "x1",
            performedAt = 1_795_000_000_000,
            setIndex = 0,
            reps = 8,
            loadKg = 82.5,
            completedAt = 1_795_000_030_000,
            updatedAt = 1_795_000_030_000,
            dirty = true,
        )

    @Test
    fun instantsTravelAsIsoAndNullsStayNull() {
        val wire = PlanningCodec.toWire(SetEntity.serializer(), set)
        assertEquals("2026-11-18T11:06:40Z", wire.getValue("performedAt").jsonPrimitive.content)
        assertEquals("2026-11-18T11:07:10Z", wire.getValue("completedAt").jsonPrimitive.content)
        assertEquals(JsonNull, wire.getValue("rpe"))
    }

    @Test
    fun roundTripRestoresEpochMillis() {
        val wire = PlanningCodec.toWire(SetEntity.serializer(), set)
        val back = PlanningCodec.fromWire(SetEntity.serializer(), wire, serverSeq = 9, deleted = false)
        assertEquals(set.copy(dirty = false, serverSeq = 9), back)
    }
}
