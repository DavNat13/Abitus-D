package com.abitusd.app.feature.progress.domain

/** A dated value (epoch day, value). */
data class DayValue(
    val day: Long,
    val value: Double,
)

/**
 * Body trends (11c): smoothed weight, weekly rate toward a goal, and optional indices that are
 * always shown with their limits. Rates above [FAST_RATE_PCT] of body weight per week are flagged.
 */
object BodyTrends {
    const val FAST_RATE_PCT = 1.0
    private const val WEEK_DAYS = 7.0
    private const val PERCENT = 100.0

    /** Mean of the readings in the [windowDays] days up to each reading; smooths daily noise. */
    fun smoothed(
        points: List<DayValue>,
        windowDays: Int = 7,
    ): List<DayValue> {
        val sorted = points.sortedBy { it.day }
        return sorted.map { p ->
            val window = sorted.filter { it.day in (p.day - windowDays + 1)..p.day }
            DayValue(p.day, window.sumOf { it.value } / window.size)
        }
    }

    /** Robust weekly change (Theil-Sen slope of the readings), in kg; null with fewer than 2 days. */
    fun weeklyRate(points: List<DayValue>): Double? =
        if (points.map { it.day }.distinct().size < 2) {
            null
        } else {
            VerdictEngine.theilSen(points.map { it.day.toDouble() to it.value }) * WEEK_DAYS
        }

    /** True when the weekly change is faster than [FAST_RATE_PCT] of the current weight. */
    fun tooFast(
        rateKgPerWeek: Double,
        weightKg: Double,
    ): Boolean = weightKg > 0 && kotlin.math.abs(rateKgPerWeek) / weightKg * PERCENT > FAST_RATE_PCT

    /** Weeks to the goal at the current rate; null if the rate does not move toward it. */
    fun weeksToGoal(
        currentKg: Double,
        goalKg: Double,
        rateKgPerWeek: Double,
    ): Double? {
        val gap = goalKg - currentKg
        return if (rateKgPerWeek == 0.0 || gap * rateKgPerWeek <= 0) null else gap / rateKgPerWeek
    }

    /** Body mass index, kg/m2. Says nothing about body composition (shown with that warning). */
    fun bmi(
        weightKg: Double,
        heightM: Double,
    ): Double? = if (heightM > 0) weightKg / (heightM * heightM) else null

    /** Waist to height ratio. */
    fun waistToHeight(
        waistM: Double,
        heightM: Double,
    ): Double? = if (heightM > 0) waistM / heightM else null

    /** Estimated 1RM divided by body weight. */
    fun relativeStrength(
        e1rmKg: Double,
        weightKg: Double,
    ): Double? = if (weightKg > 0) e1rmKg / weightKg else null
}
