package com.abitusd.app.feature.clock.domain

import kotlinx.serialization.Serializable

/**
 * A running clock as wall-clock marks (09c): never counts ticks, so it survives process death and
 * reboots. [skipMs] moves the timeline forward; [extraS] lengthens single phases (+15 s).
 */
@Serializable
data class ClockRun(
    val name: String,
    val mode: String,
    val spec: ClockSpec,
    val startedAt: Long,
    val pausedAt: Long? = null,
    val pausedTotalMs: Long = 0,
    val skipMs: Long = 0,
    val extraS: Map<Int, Int> = emptyMap(),
    val lapsMs: List<Long> = emptyList(),
)

data class ClockStatus(
    val index: Int,
    val round: Int,
    val rounds: Int,
    val kind: String,
    val elapsedMs: Long,
    val remainingMs: Long,
    val phaseMs: Long,
    val done: Boolean,
) {
    /** Wall time of the next phase change while running, or null. */
    fun boundary(now: Long): Long? = if (done || remainingMs == Long.MAX_VALUE) null else now + remainingMs
}

object ClockEngine {
    private const val MS = 1000L

    fun elapsed(
        run: ClockRun,
        now: Long,
    ): Long = maxOf(0, (run.pausedAt ?: now) - run.startedAt - run.pausedTotalMs + run.skipMs)

    private fun duration(
        run: ClockRun,
        index: Int,
    ): Long {
        val phase = run.spec.phases[index % run.spec.phases.size]
        return (phase.durationS + (run.extraS[index] ?: 0)) * MS
    }

    private fun countUp(
        run: ClockRun,
        elapsed: Long,
    ): ClockStatus {
        val open = run.spec.phases.isEmpty()
        val cap = if (open) Long.MAX_VALUE else duration(run, 0)
        val shown = minOf(elapsed, cap)
        val remaining = if (open) Long.MAX_VALUE else cap - shown
        return ClockStatus(0, 1, 1, PhaseKind.WORK, shown, remaining, cap, elapsed >= cap)
    }

    fun status(
        run: ClockRun,
        now: Long,
    ): ClockStatus {
        val elapsed = elapsed(run, now)
        val n = run.spec.phases.size
        if (ClockMode.of(run.mode).countsUp || n == 0) return countUp(run, elapsed)
        val total = n * run.spec.rounds
        var left = elapsed
        var i = 0
        while (i < total - 1 && left >= duration(run, i)) {
            left -= duration(run, i)
            i++
        }
        val d = duration(run, i)
        val done = left >= d
        val remaining = if (done) 0 else d - left
        return ClockStatus(i, i / n + 1, run.spec.rounds, run.spec.phases[i % n].kind, elapsed, remaining, d, done)
    }

    fun pause(
        run: ClockRun,
        now: Long,
    ) = if (run.pausedAt == null) run.copy(pausedAt = now) else run

    fun resume(
        run: ClockRun,
        now: Long,
    ) = run.pausedAt?.let { run.copy(pausedAt = null, pausedTotalMs = run.pausedTotalMs + now - it) } ?: run

    fun addTime(
        run: ClockRun,
        now: Long,
        seconds: Int,
    ): ClockRun {
        val i = status(run, now).index
        return run.copy(extraS = run.extraS + (i to (run.extraS[i] ?: 0) + seconds))
    }

    fun skip(
        run: ClockRun,
        now: Long,
    ): ClockRun {
        val s = status(run, now)
        return if (s.done || s.remainingMs == Long.MAX_VALUE) run else run.copy(skipMs = run.skipMs + s.remainingMs)
    }

    fun lap(
        run: ClockRun,
        now: Long,
    ) = run.copy(lapsMs = run.lapsMs + elapsed(run, now))
}
