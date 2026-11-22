package com.abitusd.app.feature.train.ui.routines

import com.abitusd.app.core.database.planning.RoutineEntity
import com.abitusd.app.core.database.planning.RoutineExerciseEntity
import com.abitusd.app.feature.train.data.RoutineRepository

enum class RoutineAction { DUPLICATE, ARCHIVE, DELETE, SCHEDULE_TODAY }

data class RoutineEditorState(
    val isNew: Boolean = true,
    val routine: RoutineEntity = RoutineRepository.newRoutine(),
    val items: List<RoutineExerciseEntity> = emptyList(),
    val names: Map<String, String> = emptyMap(),
    val volume: List<Pair<String, Double>> = emptyList(),
    val picking: Boolean = false,
    val error: Int? = null,
)
