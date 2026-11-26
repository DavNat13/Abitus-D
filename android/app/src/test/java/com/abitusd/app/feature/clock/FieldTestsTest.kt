package com.abitusd.app.feature.clock

import com.abitusd.app.feature.clock.domain.FieldTest
import com.abitusd.app.feature.clock.domain.FieldTests
import org.junit.Assert.assertEquals
import org.junit.Test

class FieldTestsTest {
    @Test
    fun shuttleRunFollowsTheLevelTable() {
        val beeps = FieldTests.schedule(FieldTest.SHUTTLE_RUN).filter { !it.start }
        assertEquals(247, beeps.size)
        assertEquals(7, beeps.count { it.level == 1 })
        // 20 m at 8.5 km/h takes 8.47 s.
        assertEquals(8470, beeps.first().atMs - 3000)
    }

    @Test
    fun yoYoHas91BoutsWithTenSecondRecovery() {
        val ends = FieldTests.schedule(FieldTest.YO_YO_IR1).filter { !it.start }
        assertEquals(91 * 2, ends.size)
        val starts = FieldTests.schedule(FieldTest.YO_YO_IR1).filter { it.start }
        assertEquals(10_000, starts[1].atMs - ends[1].atMs)
    }
}
