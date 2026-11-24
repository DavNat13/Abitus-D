package com.abitusd.app.feature.workout.ui.preview

import com.abitusd.app.core.database.planning.RoutineExerciseEntity
import com.abitusd.app.feature.train.domain.BlockWeek
import com.abitusd.app.feature.workout.data.SessionPlan

/** One planned exercise as the preview lists it, with the load the block suggests. */
data class PreviewLine(
    val name: String,
    val sets: Int,
    val loadKg: Double?,
)

/** Block context of today's session: week, length and the exercises swapped for it. */
data class PreviewBlock(
    val week: BlockWeek? = null,
    val totalWeeks: Int = 0,
    val swaps: List<Pair<String, String>> = emptyList(),
    val unfit: List<String> = emptyList(),
)

object PreviewPlan {
    fun lines(
        plan: SessionPlan,
        names: Map<String, String>,
    ): List<PreviewLine> =
        plan.exercises.map {
            PreviewLine(
                names[it.exerciseId].orEmpty(),
                it.targetSets ?: DEFAULT_SETS,
                it.targetLoadKg,
            )
        }

    fun block(
        plan: SessionPlan,
        names: Map<String, String>,
    ) = PreviewBlock(
        plan.week,
        plan.totalWeeks,
        plan.swaps.map { (from, to) -> names[from].orEmpty() to names[to].orEmpty() },
        plan.unfit.map { names[it].orEmpty() },
    )

    /** About 45 s per set plus its planned rest. */
    fun minutes(planned: List<RoutineExerciseEntity>): Int =
        planned.sumOf { (it.targetSets ?: DEFAULT_SETS) * (SET_SECONDS + (it.restSeconds ?: DEFAULT_REST)) } /
            SECONDS_PER_MIN

    private const val DEFAULT_SETS = 3
    private const val SET_SECONDS = 45
    private const val DEFAULT_REST = 90
    private const val SECONDS_PER_MIN = 60
}
