package com.abitusd.app.feature.workout.ui.summary

import com.abitusd.app.feature.train.data.RoutineRepository
import com.abitusd.app.feature.workout.data.WorkoutRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** "Save session as template" (pending from Phase 3): a new routine with what was done. */
class SessionTemplate
    @Inject
    constructor(
        private val workouts: WorkoutRepository,
        private val routines: RoutineRepository,
    ) {
        suspend fun fromSession(
            sessionId: String,
            title: String,
        ) {
            val exercises = workouts.exercises(sessionId).first()
            val sets = workouts.sets(sessionId).first().filter { it.completedAt != null }
            val routine = RoutineRepository.newRoutine(title)
            val planned =
                exercises.mapNotNull { e ->
                    val done = sets.filter { it.sessionExerciseId == e.id }
                    if (done.isEmpty()) return@mapNotNull null
                    RoutineRepository.newExercise(routine.id, e.exerciseId, done.last().restS).copy(
                        groupId = e.groupId,
                        targetSets = done.size,
                        targetRepsMin = done.mapNotNull { it.reps }.minOrNull(),
                        targetRepsMax = done.mapNotNull { it.reps }.maxOrNull(),
                        targetLoadKg = done.mapNotNull { it.loadKg }.maxOrNull(),
                    )
                }
            routines.save(routine, planned)
        }
    }
