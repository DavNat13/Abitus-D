package com.abitusd.app.feature.train.domain

/** A planned workout of the week and the day index (0 Monday..6 Sunday) it sits on. */
data class PlannedDay<T>(
    val item: T,
    val dayIndex: Int,
)

/** Result of fitting a week: the moved items and the ones that found no free day. */
data class FitResult<T>(
    val placed: List<PlannedDay<T>>,
    val unplaced: List<T>,
)

/**
 * Weekly availability (10f Fase 4b): the week's workouts keep their order and move onto the
 * available days, one per day, earliest first. With fewer days than workouts the rest stay
 * unplaced so the screen can offer a double session or pushing them to next week.
 */
object WeekFit {
    fun fit(
        week: List<PlannedDay<*>>,
        availableDays: Set<Int>,
    ): FitResult<Any?> {
        val ordered = week.sortedBy { it.dayIndex }.map { it.item }
        val days = availableDays.filter { it in 0..LAST_DAY }.sorted()
        val placed = ordered.zip(days).map { (item, day) -> PlannedDay(item, day) }
        return FitResult(placed, ordered.drop(placed.size))
    }

    private const val LAST_DAY = 6
}

/** Average RPE of the last sessions at or above the threshold suggests a deload week. */
object DeloadAdvisor {
    fun suggest(recentRpe: List<Double>): Boolean {
        val last = recentRpe.takeLast(WINDOW)
        return last.size == WINDOW && last.average() >= THRESHOLD
    }

    private const val WINDOW = 3
    private const val THRESHOLD = 9.0
}
