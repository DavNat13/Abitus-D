package com.abitusd.app.feature.workout.domain

/** Plates per side for a target barbell weight (10f). Greedy with common gym plates, in kg. */
object PlateCalculator {
    val DEFAULT_PLATES = listOf(25.0, 20.0, 15.0, 10.0, 5.0, 2.5, 1.25)
    private const val SIDES = 2

    data class Result(
        val perSide: List<Double>,
        val reachedKg: Double,
    ) {
        fun exact(targetKg: Double) = kotlin.math.abs(reachedKg - targetKg) < EPSILON
    }

    fun perSide(
        targetKg: Double,
        barKg: Double,
        plates: List<Double> = DEFAULT_PLATES,
    ): Result {
        var remaining = (targetKg - barKg) / SIDES
        val chosen = mutableListOf<Double>()
        for (plate in plates.sortedDescending()) {
            while (remaining + EPSILON >= plate) {
                chosen += plate
                remaining -= plate
            }
        }
        return Result(chosen, barKg + chosen.sum() * SIDES)
    }

    private const val EPSILON = 1e-6
}
