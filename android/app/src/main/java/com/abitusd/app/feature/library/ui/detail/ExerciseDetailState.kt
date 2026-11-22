package com.abitusd.app.feature.library.ui.detail

import com.abitusd.app.core.database.catalog.ExerciseEntity
import com.abitusd.app.core.database.catalog.ExerciseTextEntity
import com.abitusd.app.core.database.catalog.NamedRef

/** UI state of the exercise detail. */
data class DetailState(
    val loading: Boolean = true,
    val exercise: ExerciseEntity? = null,
    val texts: ExerciseTextEntity? = null,
    val discipline: String = "",
    val primaryMuscles: List<String> = emptyList(),
    val secondaryMuscles: List<String> = emptyList(),
    val equipment: List<String> = emptyList(),
    val progressions: List<RelatedExercise> = emptyList(),
    val regressions: List<RelatedExercise> = emptyList(),
    val alternatives: List<RelatedExercise> = emptyList(),
)

/** Related exercise with its string id and resolved name. */
data class RelatedExercise(
    val id: String,
    val name: String,
)

/** Rows of one exercise read from Room. */
internal data class Own(
    val exercise: ExerciseEntity?,
    val texts: List<ExerciseTextEntity>,
    val muscles: List<Pair<Int, String>>,
    val equipment: List<Int>,
    val relations: List<Pair<String, String>>,
)

/** Names resolved for the app locale. */
internal data class Names(
    val muscles: Map<Int, String>,
    val equipment: Map<Int, String>,
    val disciplines: List<NamedRef>,
    val exercises: Map<String, String>,
)
