package com.abitusd.app.feature.clock.domain

/** Actions the athlete adds to a running clock: laps, AMRAP rounds and finished rep phases. */
object ClockActions {
    fun lap(
        run: ClockRun,
        now: Long,
    ) = run.copy(lapsMs = run.lapsMs + ClockEngine.elapsed(run, now))

    /** Ends a repetition phase, keeping the time it took so the timeline stays exact. */
    fun ready(
        run: ClockRun,
        now: Long,
    ): ClockRun {
        val s = ClockEngine.status(run, now)
        return if (s.reps == null ||
            s.done
        ) {
            run
        } else {
            run.copy(doneMs = run.doneMs + (s.index to maxOf(1, s.phaseElapsedMs)))
        }
    }

    /** Rounds completed in an AMRAP, counted by the athlete. */
    @Suppress("UnusedParameter")
    fun count(
        run: ClockRun,
        now: Long,
    ) = run.copy(count = run.count + 1)
}
