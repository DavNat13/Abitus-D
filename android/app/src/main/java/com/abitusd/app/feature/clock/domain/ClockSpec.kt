package com.abitusd.app.feature.clock.domain

import kotlinx.serialization.Serializable

/** One phase of a clock sequence; durations in seconds (SI). */
@Serializable
data class ClockPhase(
    val kind: String,
    val durationS: Int,
)

/** Phases repeated [rounds] times. Same JSON the server validates (clock-tables.ts). */
@Serializable
data class ClockSpec(
    val phases: List<ClockPhase>,
    val rounds: Int = 1,
)

/** Server keys of `timer_profiles.mode`. Count-up modes measure elapsed time, the rest count down. */
enum class ClockMode(
    val key: String,
    val countsUp: Boolean = false,
) {
    STOPWATCH("stopwatch", countsUp = true),
    COUNTDOWN("countdown"),
    INTERVALS("intervals"),
    TABATA("tabata"),
    EMOM("emom"),
    AMRAP("amrap"),
    FOR_TIME("for_time", countsUp = true),
    CUSTOM("custom"),
    ;

    companion object {
        fun of(key: String): ClockMode = entries.firstOrNull { it.key == key } ?: CUSTOM
    }
}

object PhaseKind {
    const val WORK = "work"
    const val REST = "rest"
    const val PREPARE = "prepare"
    const val INHALE = "inhale"
    const val HOLD = "hold"
    const val EXHALE = "exhale"
}

/** Built-in library (11e). Not synced: the user saves own copies as timer profiles. */
data class ClockPreset(
    val key: String,
    val mode: ClockMode,
    val spec: ClockSpec,
)

object ClockPresets {
    private const val W = PhaseKind.WORK
    private const val R = PhaseKind.REST
    private const val IN = PhaseKind.INHALE
    private const val HO = PhaseKind.HOLD
    private const val EX = PhaseKind.EXHALE

    private fun seq(
        rounds: Int,
        vararg phases: Pair<String, Int>,
    ) = ClockSpec(phases.map { ClockPhase(it.first, it.second) }, rounds)

    @Suppress("MagicNumber")
    val all =
        listOf(
            ClockPreset("stopwatch", ClockMode.STOPWATCH, seq(1)),
            ClockPreset("countdown", ClockMode.COUNTDOWN, seq(1, W to 300)),
            ClockPreset("intervals", ClockMode.INTERVALS, seq(5, W to 60, R to 30)),
            ClockPreset("tabata", ClockMode.TABATA, seq(8, W to 20, R to 10)),
            ClockPreset("emom", ClockMode.EMOM, seq(10, W to 60)),
            ClockPreset("e2mom", ClockMode.EMOM, seq(5, W to 120)),
            ClockPreset("amrap", ClockMode.AMRAP, seq(1, W to 720)),
            ClockPreset("for_time", ClockMode.FOR_TIME, seq(1, W to 1200)),
            ClockPreset("pyramid", ClockMode.CUSTOM, seq(1, W to 30, R to 15, W to 45, R to 15, W to 60)),
            ClockPreset("boxing", ClockMode.INTERVALS, seq(3, W to 180, R to 60)),
            ClockPreset("mobility", ClockMode.INTERVALS, seq(8, W to 45, R to 15)),
            ClockPreset("pool", ClockMode.INTERVALS, seq(4, W to 600, R to 60)),
            ClockPreset("pomodoro", ClockMode.INTERVALS, seq(4, W to 1500, R to 300)),
            ClockPreset("cooking", ClockMode.COUNTDOWN, seq(1, W to 600)),
            ClockPreset("pool_send", ClockMode.EMOM, seq(10, W to 90)),
            ClockPreset("track", ClockMode.STOPWATCH, seq(1)),
            ClockPreset("box_breathing", ClockMode.CUSTOM, seq(10, IN to 4, HO to 4, EX to 4, HO to 4)),
            ClockPreset("breathing_478", ClockMode.CUSTOM, seq(4, IN to 4, HO to 7, EX to 8)),
            ClockPreset("cold", ClockMode.COUNTDOWN, seq(1, W to 180)),
            ClockPreset("sauna", ClockMode.COUNTDOWN, seq(1, W to 900)),
        )
}
