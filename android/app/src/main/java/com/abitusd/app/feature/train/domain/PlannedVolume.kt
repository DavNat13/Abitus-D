package com.abitusd.app.feature.train.domain

/** Muscle contribution of a catalog exercise (1.0 primary, 0.5 secondary by default). */
data class MuscleShare(
    val exerciseId: String,
    val muscleGroupId: Int,
    val contribution: Double,
)

/** Planned exercise reduced to what the volume needs. */
data class PlannedSets(
    val exerciseId: String,
    val sets: Int,
)

/**
 * Planned volume per muscle: sets times contribution (02b: 1.0 primary, 0.5 secondary).
 * Used for a routine, a day load and a week. Pure; tested on the JVM.
 */
object PlannedVolume {
    fun perMuscle(
        planned: List<PlannedSets>,
        shares: List<MuscleShare>,
    ): Map<Int, Double> {
        val byExercise = shares.groupBy { it.exerciseId }
        val totals = mutableMapOf<Int, Double>()
        for (p in planned) {
            for (share in byExercise[p.exerciseId].orEmpty()) {
                totals[share.muscleGroupId] = (totals[share.muscleGroupId] ?: 0.0) + p.sets * share.contribution
            }
        }
        return totals
    }

    fun totalSets(planned: List<PlannedSets>): Int = planned.sumOf { it.sets }
}
