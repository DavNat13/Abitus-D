package com.abitusd.app.feature.workout.domain

import com.abitusd.app.core.database.session.SetEntity
import com.abitusd.app.core.database.session.SetType

/** A new best on an exercise: estimated 1RM above every previous completed set. */
data class PersonalRecord(
    val exerciseId: String,
    val loadKg: Double,
    val reps: Int,
)

data class SessionSummary(
    val durationMin: Long,
    val volumeKg: Double,
    val sets: Int,
    val records: List<PersonalRecord>,
)

/** Summary math (10f). Warm-up sets count as sets done but not as volume or records. */
object SessionStats {
    private const val MS_PER_MIN = 60_000L
    private const val EPLEY_DIVISOR = 30.0
    private const val MAX_REPS_FOR_RECORD = 12

    fun summary(
        startedAt: Long,
        endedAt: Long,
        done: List<SetEntity>,
        previousBest: Map<String, Double>,
    ): SessionSummary {
        val work = done.filter { it.setType != SetType.WARMUP }
        val volume = work.sumOf { (it.loadKg ?: 0.0) * (it.reps ?: 0) }
        return SessionSummary(
            durationMin = (endedAt - startedAt).coerceAtLeast(0) / MS_PER_MIN,
            volumeKg = volume,
            sets = done.size,
            records = records(work, previousBest),
        )
    }

    /** Epley estimated one-repetition maximum; null when the set has no load or reps. */
    fun estimatedMax(set: SetEntity): Double? {
        val load = set.loadKg ?: 0.0
        val reps = set.reps ?: 0
        val valid = reps in 1..MAX_REPS_FOR_RECORD && load > 0.0
        return if (valid) load * (1 + reps / EPLEY_DIVISOR) else null
    }

    private fun records(
        work: List<SetEntity>,
        previousBest: Map<String, Double>,
    ): List<PersonalRecord> =
        work
            .groupBy { it.exerciseId }
            .mapNotNull { (exerciseId, sets) ->
                val best = sets.maxByOrNull { estimatedMax(it) ?: 0.0 } ?: return@mapNotNull null
                val max = estimatedMax(best) ?: return@mapNotNull null
                val before = previousBest[exerciseId]
                if (before != null &&
                    max > before
                ) {
                    PersonalRecord(exerciseId, best.loadKg ?: 0.0, best.reps ?: 0)
                } else {
                    null
                }
            }
}
