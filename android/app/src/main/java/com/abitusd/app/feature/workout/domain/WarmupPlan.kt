package com.abitusd.app.feature.workout.domain

/** Approach sets before the work sets: 50 percent for 8 reps and 70 percent for 5, in 2.5 kg steps. */
object WarmupPlan {
    private val STEPS = listOf(0.5 to 8, 0.7 to 5)
    private const val PLATE_STEP = 2.5

    fun sets(workKg: Double): List<Pair<Double, Int>> =
        STEPS
            .map { (fraction, reps) -> roundToStep(workKg * fraction) to reps }
            .filter { it.first > 0.0 && it.first < workKg }

    private fun roundToStep(kg: Double): Double = Math.round(kg / PLATE_STEP) * PLATE_STEP
}
