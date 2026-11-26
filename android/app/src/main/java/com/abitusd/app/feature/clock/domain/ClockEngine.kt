package com.abitusd.app.feature.clock.domain

object ClockEngine {
    private const val MS = 1000L
    private const val OPEN = Long.MAX_VALUE / 4

    fun elapsed(
        run: ClockRun,
        now: Long,
    ): Long = maxOf(0, (run.pausedAt ?: now) - run.startedAt - run.pausedTotalMs + run.skipMs)

    private fun duration(
        run: ClockRun,
        index: Int,
    ): Long {
        val phase = run.spec.phases[index % run.spec.phases.size]
        if (phase.reps != null) return run.doneMs[index] ?: OPEN
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
        val lead = run.startedAt - (run.pausedAt ?: now)
        return if (lead >
            0
        ) {
            ClockStatus(-1, 1, run.spec.rounds, PhaseKind.PREPARE, 0, lead, lead, false)
        } else {
            timed(run, now)
        }
    }

    private fun timed(
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
        val phase = run.spec.phases[i % n]
        val waiting = d == OPEN
        val remaining =
            if (waiting) {
                Long.MAX_VALUE
            } else if (done) {
                0
            } else {
                d - left
            }
        val next =
            run.spec.phases
                .getOrNull((i + 1) % n)
                ?.label
                .takeIf { i + 1 < total }
        return ClockStatus(
            i,
            i / n + 1,
            run.spec.rounds,
            phase.kind,
            elapsed,
            remaining,
            d,
            done,
            left,
            phase.reps,
            phase.label,
            next,
        )
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
}
