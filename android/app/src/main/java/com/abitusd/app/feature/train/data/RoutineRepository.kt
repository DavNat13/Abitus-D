package com.abitusd.app.feature.train.data

import androidx.room3.withWriteTransaction
import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.planning.RoutineEntity
import com.abitusd.app.core.database.planning.RoutineExerciseEntity
import com.abitusd.app.core.sync.SyncScheduler
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Routines and their planned exercises. Local first: every write marks rows dirty. */
@Singleton
class RoutineRepository
    @Inject
    constructor(
        private val database: AbitusDatabase,
        private val syncScheduler: SyncScheduler,
    ) {
        private val dao get() = database.planningDao()

        fun routines() = dao.routines()

        fun exercises(routineId: String) = dao.routineExercises(routineId)

        suspend fun routine(id: String) = dao.routine(id)?.takeIf { it.deletedAt == null }

        suspend fun exercisesOnce(routineId: String) = dao.routineExercisesOnce(routineId)

        /** Saves the routine and its exercises in order; removed exercises become tombstones. */
        suspend fun save(
            routine: RoutineEntity,
            exercises: List<RoutineExerciseEntity>,
        ) {
            val now = System.currentTimeMillis()
            database.withWriteTransaction {
                val keep = exercises.map { it.id }.toSet()
                val removed =
                    dao
                        .routineExercisesOnce(routine.id)
                        .filter { it.id !in keep }
                        .map { it.copy(deletedAt = now, updatedAt = now, dirty = true) }
                dao.upsertRoutines(listOf(routine.copy(updatedAt = now, dirty = true, deletedAt = null)))
                dao.upsertRoutineExercises(
                    removed +
                        exercises.mapIndexed { index, e ->
                            e.copy(
                                routineId = routine.id,
                                position = index,
                                updatedAt = now,
                                dirty = true,
                                deletedAt = null,
                            )
                        },
                )
            }
            syncScheduler.requestSync()
        }

        /** Copy with new ids for the routine and every exercise, keeping the order. */
        suspend fun duplicate(
            id: String,
            copySuffix: String,
        ): String? {
            val source = routine(id) ?: return null
            val newId = UUID.randomUUID().toString()
            val copies =
                exercisesOnce(id).map { it.copy(id = UUID.randomUUID().toString(), routineId = newId, serverSeq = 0) }
            save(source.copy(id = newId, name = "${source.name} $copySuffix".take(MAX_NAME), serverSeq = 0), copies)
            return newId
        }

        suspend fun setArchived(
            id: String,
            archived: Boolean,
        ) {
            val routine = routine(id) ?: return
            save(routine.copy(archived = archived), exercisesOnce(id))
        }

        suspend fun delete(id: String) {
            val now = System.currentTimeMillis()
            val routine = dao.routine(id) ?: return
            database.withWriteTransaction {
                dao.upsertRoutines(listOf(routine.copy(deletedAt = now, updatedAt = now, dirty = true)))
                dao.upsertRoutineExercises(
                    dao.routineExercisesOnce(id).map { it.copy(deletedAt = now, updatedAt = now, dirty = true) },
                )
            }
            syncScheduler.requestSync()
        }

        companion object {
            const val MAX_NAME = 80

            fun newRoutine(name: String = "") =
                RoutineEntity(id = UUID.randomUUID().toString(), name = name, updatedAt = 0)

            fun newExercise(
                routineId: String,
                exerciseId: String,
                restSeconds: Int?,
            ) = RoutineExerciseEntity(
                id = UUID.randomUUID().toString(),
                routineId = routineId,
                exerciseId = exerciseId,
                position = 0,
                targetSets = DEFAULT_SETS,
                targetRepsMin = DEFAULT_REPS_MIN,
                targetRepsMax = DEFAULT_REPS_MAX,
                restSeconds = restSeconds,
                updatedAt = 0,
            )

            private const val DEFAULT_SETS = 3
            private const val DEFAULT_REPS_MIN = 8
            private const val DEFAULT_REPS_MAX = 12
        }
    }
