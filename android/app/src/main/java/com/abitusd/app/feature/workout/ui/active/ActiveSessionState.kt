package com.abitusd.app.feature.workout.ui.active

import com.abitusd.app.core.database.session.ActiveSessionEntity
import com.abitusd.app.core.database.session.SessionExerciseEntity
import com.abitusd.app.core.database.session.SetEntity
import com.abitusd.app.core.database.session.WorkoutSessionEntity

/** One exercise of the session with its sets and the sets of the previous time. */
data class ExerciseBlock(
    val exercise: SessionExerciseEntity,
    val name: String,
    val sets: List<SetEntity>,
    val lastTime: List<SetEntity>,
) {
    val done get() = sets.count { it.completedAt != null }
}

data class ActiveSessionState(
    val loading: Boolean = true,
    val session: WorkoutSessionEntity? = null,
    val blocks: List<ExerciseBlock> = emptyList(),
    val index: Int = 0,
    val restEndsAt: Long? = null,
    val restTotalS: Int = 0,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
)

/** Groups the session rows into blocks in exercise order. */
internal fun buildBlocks(
    exercises: List<SessionExerciseEntity>,
    sets: List<SetEntity>,
    names: Map<String, String>,
    lastTimes: Map<String, List<SetEntity>>,
): List<ExerciseBlock> {
    val byExercise = sets.groupBy { it.sessionExerciseId }
    return exercises.map { e ->
        ExerciseBlock(
            e,
            names[e.exerciseId].orEmpty(),
            byExercise[e.id].orEmpty().sortedBy {
                it.setIndex
            },
            lastTimes[e.exerciseId].orEmpty(),
        )
    }
}

internal fun ActiveSessionEntity?.index(size: Int) = (this?.exerciseIndex ?: 0).coerceIn(0, (size - 1).coerceAtLeast(0))
