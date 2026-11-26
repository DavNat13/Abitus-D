package com.abitusd.app.feature.progress.domain

/** Estimated one-repetition maximum (09d). The athlete chooses the formula in the analysis settings. */
enum class OneRmFormula { EPLEY, BRZYCKI }

/** User adjustable analysis constants (09d: window and thresholds are configurable). */
data class AnalysisSettings(
    val formula: OneRmFormula = OneRmFormula.EPLEY,
    val windowWeeks: Int = 6,
    val upPct: Double = 1.0,
    val downPct: Double = -1.0,
    val minWeeklySets: Int = 10,
    val maxWeeklySets: Int = 20,
)

object OneRepMax {
    const val MIN_REPS = 1
    const val MAX_REPS = 10
    private const val EPLEY_DIVISOR = 30.0
    private const val BRZYCKI_A = 36.0
    private const val BRZYCKI_B = 37.0

    /** Null outside 1 to 10 reps or without load: those sets are excluded from the estimate. */
    fun estimate(
        loadKg: Double,
        reps: Int,
        formula: OneRmFormula,
    ): Double? {
        if (reps !in MIN_REPS..MAX_REPS || loadKg <= 0.0) return null
        return when (formula) {
            OneRmFormula.EPLEY -> loadKg * (1 + reps / EPLEY_DIVISOR)
            OneRmFormula.BRZYCKI -> loadKg * BRZYCKI_A / (BRZYCKI_B - reps)
        }
    }
}
