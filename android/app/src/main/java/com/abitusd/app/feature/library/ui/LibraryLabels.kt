package com.abitusd.app.feature.library.ui

import androidx.annotation.StringRes
import com.abitusd.app.R

/** Localized labels of catalog enumerations (08-DATABASE.md). */
object LibraryLabels {
    val METRICS: List<Pair<String, Int>> =
        listOf(
            "weight_reps" to R.string.metric_weight_reps,
            "bodyweight_reps" to R.string.metric_bodyweight_reps,
            "weighted_bodyweight_reps" to R.string.metric_weighted_bodyweight_reps,
            "duration" to R.string.metric_duration,
            "distance_duration" to R.string.metric_distance_duration,
            "swim_laps" to R.string.metric_swim_laps,
            "plyo" to R.string.metric_plyo,
        )

    val PATTERNS: List<Pair<String, Int>> =
        listOf(
            "push" to R.string.pattern_push,
            "pull" to R.string.pattern_pull,
            "squat" to R.string.pattern_squat,
            "hinge" to R.string.pattern_hinge,
            "lunge" to R.string.pattern_lunge,
            "carry" to R.string.pattern_carry,
            "core" to R.string.pattern_core,
            "jump" to R.string.pattern_jump,
            "locomotion" to R.string.pattern_locomotion,
            "swim" to R.string.pattern_swim,
            "mobility" to R.string.pattern_mobility,
        )

    val DIFFICULTIES: List<Int> = (1..5).toList()

    @StringRes
    fun metric(key: String): Int = METRICS.firstOrNull { it.first == key }?.second ?: R.string.metric_weight_reps

    @StringRes
    fun pattern(key: String): Int = PATTERNS.firstOrNull { it.first == key }?.second ?: R.string.pattern_core
}
