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
    val count: Int = 0,
    val doneMs: Map<Int, Long> = emptyMap(),
    val lapLabels: Map<Int, String> = emptyMap(),
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
    val phaseElapsedMs: Long = 0,
    val reps: Int? = null,
    val label: String? = null,
    val nextLabel: String? = null,
) {
    /** Wall time of the next phase change while running, or null. */
    fun boundary(now: Long): Long? = if (done || remainingMs == Long.MAX_VALUE) null else now + remainingMs
}
