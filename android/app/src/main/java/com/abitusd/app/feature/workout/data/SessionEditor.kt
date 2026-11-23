package com.abitusd.app.feature.workout.data

import androidx.room3.withWriteTransaction
import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.session.SetEntity
import com.abitusd.app.core.sync.SyncScheduler
import com.abitusd.app.feature.workout.domain.SessionFactory
import javax.inject.Inject
import javax.inject.Singleton

/** Edits inside an active session: sets and exercises. Each write is one fast Room upsert. */
@Singleton
class SessionEditor
    @Inject
    constructor(
        private val database: AbitusDatabase,
        private val syncScheduler: SyncScheduler,
    ) {
        private val dao get() = database.sessionDao()
        private val factory = SessionFactory()

        /** Writes one set as it is (confirm, edit, undo); the caller decides completedAt. */
        suspend fun saveSet(set: SetEntity) {
            dao.upsertSets(listOf(set.copy(updatedAt = System.currentTimeMillis(), dirty = true)))
            syncScheduler.requestSync()
        }

        /** Adds a pending set copying the values of the last set of that exercise. */
        suspend fun addSet(
            sessionExerciseId: String,
            last: SetEntity?,
        ) {
            val exercise = dao.sessionExercise(sessionExerciseId) ?: return
            val base = factory.plannedSet(exercise, (last?.setIndex ?: -1) + 1, System.currentTimeMillis())
            val copy =
                last?.let {
                    base.copy(
                        reps = it.reps,
                        loadKg = it.loadKg,
                        durationS = it.durationS,
                        rpe = it.rpe,
                        rir = it.rir,
                        restS = it.restS,
                    )
                }
            saveSet(copy ?: base)
        }

        suspend fun removeSet(set: SetEntity) = saveSet(set.copy(deletedAt = System.currentTimeMillis()))

        suspend fun addExercise(
            sessionId: String,
            exerciseId: String,
        ) {
            val now = System.currentTimeMillis()
            val position = dao.sessionExercisesOnce(sessionId).size
            val exercise = factory.exercise(sessionId, exerciseId, position, now)
            database.withWriteTransaction {
                dao.upsertSessionExercises(listOf(exercise))
                dao.upsertSets((0 until NEW_EXERCISE_SETS).map { factory.plannedSet(exercise, it, now) })
            }
            syncScheduler.requestSync()
        }

        /** Moves an exercise up or down; positions are rewritten in order. */
        suspend fun moveExercise(
            sessionId: String,
            id: String,
            delta: Int,
        ) {
            val list = dao.sessionExercisesOnce(sessionId).toMutableList()
            val from = list.indexOfFirst { it.id == id }
            val to = from + delta
            if (from < 0 || to !in list.indices) return
            list.add(to, list.removeAt(from))
            val now = System.currentTimeMillis()
            dao.upsertSessionExercises(list.mapIndexed { i, e -> e.copy(position = i, updatedAt = now, dirty = true) })
            syncScheduler.requestSync()
        }

        /** Notes and discomfort zone of one exercise of the session. */
        suspend fun annotate(
            sessionExerciseId: String,
            notes: String,
            discomfort: String,
        ) {
            val row = dao.sessionExercise(sessionExerciseId) ?: return
            val now = System.currentTimeMillis()
            dao.upsertSessionExercises(
                listOf(row.copy(notes = notes, discomfort = discomfort, updatedAt = now, dirty = true)),
            )
            syncScheduler.requestSync()
        }

        private companion object {
            const val NEW_EXERCISE_SETS = 3
        }
    }
