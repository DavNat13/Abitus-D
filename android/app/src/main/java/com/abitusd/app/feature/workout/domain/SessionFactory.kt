package com.abitusd.app.feature.workout.domain

import com.abitusd.app.core.database.planning.RoutineExerciseEntity
import com.abitusd.app.core.database.session.SessionExerciseEntity
import com.abitusd.app.core.database.session.SetEntity
import com.abitusd.app.core.database.session.WorkoutSessionEntity
import java.util.UUID

/** A new session with its exercises and planned (not yet completed) sets. */
data class NewSession(
    val session: WorkoutSessionEntity,
    val exercises: List<SessionExerciseEntity>,
    val sets: List<SetEntity>,
)

/** Builds sessions from a routine or from nothing. Pure: ids and clock are injected for tests. */
class SessionFactory(
    private val newId: () -> String = { UUID.randomUUID().toString() },
) {
    fun fromRoutine(
        title: String,
        routineId: String?,
        scheduledWorkoutId: String?,
        planned: List<RoutineExerciseEntity>,
        now: Long,
        readiness: Readiness = Readiness(),
    ): NewSession {
        val session =
            WorkoutSessionEntity(
                id = newId(),
                routineId = routineId,
                scheduledWorkoutId = scheduledWorkoutId,
                title = title,
                startedAt = now,
                sleepQuality = readiness.sleep,
                energy = readiness.energy,
                updatedAt = now,
                dirty = true,
            )
        val exercises =
            planned.sortedBy { it.position }.mapIndexed {
                index,
                p,
                ->
                exercise(session.id, p.exerciseId, index, now, p.groupId)
            }
        val sets =
            exercises.zip(planned.sortedBy { it.position }).flatMap { (e, p) ->
                (0 until (p.targetSets ?: DEFAULT_SETS)).map { i -> plannedSet(e, i, now, p) }
            }
        return NewSession(session, exercises, sets)
    }

    fun exercise(
        sessionId: String,
        exerciseId: String,
        position: Int,
        now: Long,
        groupId: String = "",
    ) = SessionExerciseEntity(
        id = newId(),
        sessionId = sessionId,
        exerciseId = exerciseId,
        position = position,
        groupId = groupId,
        updatedAt = now,
        dirty = true,
    )

    /** A pending set copying the targets; the reps default to the top of the range. */
    fun plannedSet(
        exercise: SessionExerciseEntity,
        index: Int,
        now: Long,
        target: RoutineExerciseEntity? = null,
    ) = SetEntity(
        id = newId(),
        sessionExerciseId = exercise.id,
        exerciseId = exercise.exerciseId,
        performedAt = now,
        setIndex = index,
        reps = target?.targetRepsMax ?: target?.targetRepsMin,
        loadKg = target?.targetLoadKg,
        durationS = target?.targetDurationS,
        distanceM = target?.targetDistanceM,
        rpe = target?.targetRpe,
        rir = target?.targetRir,
        restS = target?.restSeconds,
        updatedAt = now,
        dirty = true,
    )

    private companion object {
        const val DEFAULT_SETS = 3
    }
}

/** Pre-session check (10f): sleep and energy from 1 to 5, both optional. */
data class Readiness(
    val sleep: Int? = null,
    val energy: Int? = null,
)
