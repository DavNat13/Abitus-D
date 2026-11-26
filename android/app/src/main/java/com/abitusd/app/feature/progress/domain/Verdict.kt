package com.abitusd.app.feature.progress.domain

/** One value per session: the best of the metric that represents the exercise. */
data class SessionPoint(
    val at: Long,
    val value: Double,
)

enum class VerdictKind { PROGRESS, STABLE, REGRESS, NO_DATA }

/** The verdict with the data that supports it ("e1RM from 92 to 97 kg in 6 weeks"). */
data class Verdict(
    val kind: VerdictKind,
    val changePct4w: Double = 0.0,
    val first: Double = 0.0,
    val last: Double = 0.0,
    val points: Int = 0,
    val spanDays: Int = 0,
)

/**
 * Progress verdict (09d): the last [AnalysisSettings.windowWeeks] weeks, at least [MIN_POINTS]
 * sessions, robust Theil-Sen slope expressed as percent change per four weeks of the median value.
 */
object VerdictEngine {
    const val MIN_POINTS = 3
    private const val DAY_MS = 86_400_000.0
    private const val WEEK_DAYS = 7
    private const val FOUR_WEEKS_DAYS = 28.0
    private const val PERCENT = 100.0

    fun evaluate(
        points: List<SessionPoint>,
        now: Long,
        settings: AnalysisSettings,
    ): Verdict {
        val from = now - settings.windowWeeks * WEEK_DAYS * DAY_MS.toLong()
        val window = points.filter { it.at in from..now }.sortedBy { it.at }
        if (window.size < MIN_POINTS) return Verdict(VerdictKind.NO_DATA, points = window.size)
        val slopePerDay = theilSen(window.map { (it.at - window.first().at) / DAY_MS to it.value })
        val level = median(window.map { it.value })
        val pct = if (level > 0) slopePerDay * FOUR_WEEKS_DAYS / level * PERCENT else 0.0
        val kind =
            when {
                pct > settings.upPct -> VerdictKind.PROGRESS
                pct < settings.downPct -> VerdictKind.REGRESS
                else -> VerdictKind.STABLE
            }
        val span = ((window.last().at - window.first().at) / DAY_MS).toInt()
        return Verdict(kind, pct, window.first().value, window.last().value, window.size, span)
    }

    /** Median of the pairwise slopes; robust to one bad session. */
    fun theilSen(xy: List<Pair<Double, Double>>): Double {
        val slopes = mutableListOf<Double>()
        for (i in xy.indices) {
            for (j in i + 1 until xy.size) {
                val dx = xy[j].first - xy[i].first
                if (dx > 0) slopes += (xy[j].second - xy[i].second) / dx
            }
        }
        return if (slopes.isEmpty()) 0.0 else median(slopes)
    }

    fun median(values: List<Double>): Double {
        val s = values.sorted()
        val mid = s.size / 2
        return if (s.size % 2 == 1) s[mid] else (s[mid - 1] + s[mid]) / 2
    }
}
