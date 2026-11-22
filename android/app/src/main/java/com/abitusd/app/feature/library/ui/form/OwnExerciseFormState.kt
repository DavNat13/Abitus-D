package com.abitusd.app.feature.library.ui.form

import androidx.annotation.StringRes
import com.abitusd.app.core.database.catalog.NamedRef
import com.abitusd.app.feature.library.domain.NamedExercise

/** UI state of the own exercise form. */
data class OwnExerciseFormState(
    val isEdit: Boolean = false,
    val name: String = "",
    val notes: String = "",
    val disciplineId: Int = DEFAULT_DISCIPLINE,
    val metricType: String = "weight_reps",
    val movementPattern: String = "push",
    val difficulty: Int = 2,
    val primary: Set<Int> = emptySet(),
    val secondary: Set<Int> = emptySet(),
    val equipment: Set<Int> = emptySet(),
    val disciplines: List<NamedRef> = emptyList(),
    val muscles: List<NamedRef> = emptyList(),
    val equipmentOptions: List<NamedRef> = emptyList(),
    val duplicates: List<NamedExercise> = emptyList(),
    @param:StringRes val error: Int? = null,
    val saving: Boolean = false,
)

internal const val DEFAULT_DISCIPLINE = 3
