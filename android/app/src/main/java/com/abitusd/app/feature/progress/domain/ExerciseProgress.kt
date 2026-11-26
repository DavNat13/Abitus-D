package com.abitusd.app.feature.progress.domain

/** A completed work set, already joined with its session (warm-ups excluded by the caller). */
data class WorkSet(
    val sessionId: String,
    val at: Long,
    val reps: Int?,
    val loadKg: Double?,
    val addedKg: Double?,
    val durationS: Int?,
)

/** How an exercise is measured, from the catalog metric type (007). */
enum class Metric { E1RM, REPS, DURATION }

data class Records(
    val maxLoadKg: Double?,
    val bestE1rm: Double?,
    val maxReps: Int?,
    val maxDurationS: Int?,
)

/**
 * Per exercise math of Progress (09d). Weighted bodyweight exercises use body weight plus the
 * added load when the body weight is known; plain bodyweight work is measured by reps.
 */
object ExerciseProgress {
    private const val TOP = 10
    private const val WEEK_MS = 7 * 86_400_000L

    fun metricOf(metricType: String): Metric =
        when (metricType) {
            "weight_reps", "weighted_bodyweight_reps" -> Metric.E1RM
            "duration" -> Metric.DURATION
            else -> Metric.REPS
        }

    fun load(
        set: WorkSet,
        metricType: String,
        bodyweightKg: Double?,
    ): Double? =
        if (metricType == "weighted_bodyweight_reps") {
            bodyweightKg?.let { it + (set.addedKg ?: 0.0) } ?: set.addedKg
        } else {
            set.loadKg
        }

    fun score(
        set: WorkSet,
        metricType: String,
        bodyweightKg: Double?,
        formula: OneRmFormula,
    ): Double? =
        when (metricOf(metricType)) {
            Metric.E1RM -> {
                load(set, metricType, bodyweightKg)?.let { l ->
                    set.reps?.let { OneRepMax.estimate(l, it, formula) }
                }
            }

            Metric.DURATION -> {
                set.durationS?.toDouble()
            }

            Metric.REPS -> {
                set.reps?.toDouble()
            }
        }

    /** Best score of each session, in time order: the series of the chart and the verdict. */
    fun sessionPoints(
        sets: List<WorkSet>,
        metricType: String,
        bodyweightKg: Double?,
        formula: OneRmFormula,
    ): List<SessionPoint> =
        sets
            .groupBy { it.sessionId }
            .mapNotNull { (_, list) ->
                val best =
                    list
                        .mapNotNull { s ->
                            score(s, metricType, bodyweightKg, formula)?.let { s.at to it }
                        }.maxByOrNull { it.second }
                best?.let { SessionPoint(list.minOf { it.at }, it.second) }
            }.sortedBy { it.at }

    fun records(
        sets: List<WorkSet>,
        metricType: String,
        bodyweightKg: Double?,
        formula: OneRmFormula,
    ) = Records(
        maxLoadKg = sets.mapNotNull { load(it, metricType, bodyweightKg) }.maxOrNull(),
        bestE1rm =
            sets
                .mapNotNull { s ->
                    load(s, metricType, bodyweightKg)?.let { l ->
                        s.reps?.let { OneRepMax.estimate(l, it, formula) }
                    }
                }.maxOrNull(),
        maxReps = sets.mapNotNull { it.reps }.maxOrNull(),
        maxDurationS = sets.mapNotNull { it.durationS }.maxOrNull(),
    )

    /** Ten best sets ever by the exercise score. */
    fun topSets(
        sets: List<WorkSet>,
        metricType: String,
        bodyweightKg: Double?,
        formula: OneRmFormula,
    ): List<Pair<WorkSet, Double>> =
        sets
            .mapNotNull { s ->
                score(s, metricType, bodyweightKg, formula)?.let { s to it }
            }.sortedByDescending { it.second }
            .take(TOP)

    /** Whole weeks since the last session that beat every earlier one; null without history. */
    fun weeksSinceRecord(
        points: List<SessionPoint>,
        now: Long,
    ): Int? {
        var best = Double.NEGATIVE_INFINITY
        var lastRecord: Long? = null
        for (p in points.sortedBy { it.at }) {
            if (p.value > best) {
                best = p.value
                lastRecord = p.at
            }
        }
        return lastRecord?.let { ((now - it) / WEEK_MS).toInt() }
    }
}
