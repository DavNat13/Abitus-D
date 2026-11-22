package com.abitusd.app.core.designsystem.icon

import androidx.annotation.DrawableRes
import com.abitusd.app.R

/** Discipline icon keys of the seed (`icon`) mapped to drawables; unknown keys use the generic exercise icon. */
object DisciplineIcons {
    private val byKey =
        mapOf(
            "sports_gymnastics" to R.drawable.ic_sports_gymnastics,
            "fitness_center" to R.drawable.ic_fitness_center,
            "exercise" to R.drawable.ic_exercise,
            "weight" to R.drawable.ic_weight,
            "sprint" to R.drawable.ic_sprint,
            "directions_run" to R.drawable.ic_directions_run,
            "pool" to R.drawable.ic_pool,
            "directions_bike" to R.drawable.ic_directions_bike,
            "rowing" to R.drawable.ic_rowing,
            "accessibility_new" to R.drawable.ic_accessibility_new,
            "self_improvement" to R.drawable.ic_self_improvement,
            "directions_walk" to R.drawable.ic_directions_walk,
        )

    @DrawableRes
    fun of(iconKey: String?): Int = byKey[iconKey] ?: R.drawable.ic_exercise
}
