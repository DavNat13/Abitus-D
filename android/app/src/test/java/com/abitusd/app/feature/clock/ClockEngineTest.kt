package com.abitusd.app.feature.clock

import com.abitusd.app.feature.clock.domain.ClockActions
import com.abitusd.app.feature.clock.domain.ClockEngine
import com.abitusd.app.feature.clock.domain.ClockMode
import com.abitusd.app.feature.clock.domain.ClockPhase
import com.abitusd.app.feature.clock.domain.ClockPresets
import com.abitusd.app.feature.clock.domain.ClockRun
import com.abitusd.app.feature.clock.domain.ClockSpec
import com.abitusd.app.feature.clock.domain.PhaseKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ClockEngineTest {
    private fun run(key: String) =
        ClockPresets.all.first { it.key == key }.let { ClockRun(it.key, it.mode.key, it.spec, startedAt = 0) }

    @Test
    fun tabataWalksPhasesAndRoundsByWallClock() {
        val tabata = run("tabata")
        val s = ClockEngine.status(tabata, 25_000)
        assertEquals(PhaseKind.REST, s.kind)
        assertEquals(1, s.round)
        assertEquals(5_000, s.remainingMs)
        assertEquals(30_000L, s.boundary(25_000))
        assertEquals(8, ClockEngine.status(tabata, 239_000).round)
        assertTrue(ClockEngine.status(tabata, 240_000).done)
    }

    @Test
    fun pauseFreezesAndResumeContinues() {
        val paused = ClockEngine.pause(run("countdown"), 10_000)
        assertEquals(290_000, ClockEngine.status(paused, 99_000).remainingMs)
        val resumed = ClockEngine.resume(paused, 100_000)
        assertEquals(280_000, ClockEngine.status(resumed, 110_000).remainingMs)
    }

    @Test
    fun addAndSkipOnlyTouchTheCurrentPhase() {
        val added = ClockEngine.addTime(run("tabata"), 5_000, 15)
        assertEquals(30_000, ClockEngine.status(added, 5_000).remainingMs)
        assertEquals(10_000, ClockEngine.status(added, 35_000).remainingMs)
        val skipped = ClockEngine.skip(run("tabata"), 5_000)
        assertEquals(PhaseKind.REST, ClockEngine.status(skipped, 5_000).kind)
    }

    @Test
    fun stopwatchCountsUpWithLaps() {
        val sw = ClockActions.lap(run("stopwatch"), 12_345)
        assertEquals(listOf(12_345L), sw.lapsMs)
        assertNull(ClockEngine.status(sw, 50_000).boundary(50_000))
        assertTrue(ClockMode.of("for_time").countsUp)
        assertTrue(ClockEngine.status(run("for_time"), 1_200_000).done)
    }

    @Test
    fun repsPhaseWaitsForReadyAndLeadCountsDown() {
        val spec =
            ClockSpec(
                listOf(ClockPhase(PhaseKind.WORK, 1, reps = 8, label = "Push-up"), ClockPhase(PhaseKind.REST, 30)),
            )
        val run = ClockRun("x", ClockMode.CUSTOM.key, spec, startedAt = 10_000)
        assertEquals(PhaseKind.PREPARE, ClockEngine.status(run, 4_000).kind)
        val waiting = ClockEngine.status(run, 70_000)
        assertEquals(8, waiting.reps)
        assertNull(waiting.boundary(70_000))
        val ready = ClockActions.ready(run, 70_000)
        assertEquals(PhaseKind.REST, ClockEngine.status(ready, 70_000).kind)
        assertEquals(30_000, ClockEngine.status(ready, 70_000).remainingMs)
    }
}
