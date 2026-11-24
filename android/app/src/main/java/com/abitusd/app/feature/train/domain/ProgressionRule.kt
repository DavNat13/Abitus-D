package com.abitusd.app.feature.train.domain

import com.abitusd.app.core.database.planning.BlockPhase
import com.abitusd.app.core.database.planning.Progression
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt

/** Planned target of one exercise, in SI units. */
data class Target(
    val sets: Int?,
    val repsMin: Int?,
    val repsMax: Int?,
    val loadKg: Double?,
    val rpe: Double?,
)

/**
 * Week-by-week progression of a block (10f Fase 4b). Deload cuts volume and load; percent adds
 * [BlockSpec.step] percent of load per week; RPE adds half a point per week up to 10. Double
 * progression depends on the last session and lives in [doubleProgression].
 */
object ProgressionRule {
    fun apply(
        base: Target,
        week: BlockWeek,
    ): Target {
        val block = week.block
        val n = week.weekInBlock
        return when {
            block.phase == BlockPhase.DELOAD -> {
                deload(base)
            }

            block.progression == Progression.PERCENT -> {
                base.copy(loadKg = base.loadKg?.let { round(it * (1 + (block.step ?: 0.0) / PERCENT * n)) })
            }

            block.progression == Progression.RPE -> {
                base.copy(rpe = base.rpe?.let { minOf(MAX_RPE, it + RPE_STEP * n) })
            }

            else -> {
                base
            }
        }
    }

    fun deload(base: Target): Target =
        base.copy(
            sets = base.sets?.let { max(1, (it * DELOAD_SETS).roundToInt()) },
            loadKg = base.loadKg?.let { round(it * DELOAD_LOAD) },
        )

    /** Adds [stepKg] when every set of the last session reached [repsMax]. */
    fun doubleProgression(
        lastReps: List<Int>,
        repsMax: Int?,
        loadKg: Double?,
        stepKg: Double?,
    ): Double? {
        val done = repsMax != null && lastReps.isNotEmpty() && lastReps.all { it >= repsMax }
        return if (done && loadKg != null && stepKg != null) loadKg + stepKg else loadKg
    }

    /** Working load from a tested max: Epley inverse for [reps] at the given [percent] of it. */
    fun fromTest(
        estimatedMaxKg: Double,
        reps: Int,
        percent: Double = PERCENT,
    ): Double = round(estimatedMaxKg / (1 + reps / EPLEY) * percent / PERCENT)

    /**
     * Load after a test block: the best Epley estimate of the last session ([reps] to [loadKg]
     * pairs) turned into the load for [targetReps]. Null when nothing was lifted with load.
     */
    fun afterTest(
        last: List<Pair<Int, Double>>,
        targetReps: Int,
    ): Double? =
        last
            .maxOfOrNull { (reps, kg) ->
                kg * (1 + reps / EPLEY)
            }?.takeIf { it > 0.0 }
            ?.let { fromTest(it, targetReps) }

    /** Rounds down to the nearest plate step so suggestions are loadable. */
    fun round(kg: Double): Double = floor(kg / PLATE_STEP) * PLATE_STEP

    private const val PERCENT = 100.0
    private const val MAX_RPE = 10.0
    private const val RPE_STEP = 0.5
    private const val DELOAD_SETS = 0.6
    private const val DELOAD_LOAD = 0.85
    private const val EPLEY = 30.0
    private const val PLATE_STEP = 2.5
}
