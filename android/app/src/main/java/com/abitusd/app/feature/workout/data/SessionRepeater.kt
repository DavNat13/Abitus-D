package com.abitusd.app.feature.workout.data

import androidx.room3.withWriteTransaction
import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.session.ActiveSessionEntity
import com.abitusd.app.core.sync.SyncScheduler
import com.abitusd.app.feature.workout.domain.SessionFactory
import javax.inject.Inject

/** Repeat a past session (10f: copy the previous session). */
class SessionRepeater
    @Inject
    constructor(
        private val database: AbitusDatabase,
        private val syncScheduler: SyncScheduler,
        private val previous: PreviousSession,
    ) {
        private val dao get() = database.sessionDao()

        /** Copies a past session (exercises and the values of its done sets) as the new active one. */
        suspend fun repeat(sessionId: String): String? {
            val source = dao.session(sessionId) ?: return null
            previous.close(save = true)
            val done = dao.setsOfSessionOnce(sessionId).filter { it.completedAt != null }
            val now = System.currentTimeMillis()
            val factory = SessionFactory()
            val session = factory.fromRoutine(source.title, source.routineId, null, emptyList(), now).session
            val exercises =
                dao.sessionExercisesOnce(sessionId).map {
                    factory.exercise(session.id, it.exerciseId, it.position, now, it.groupId) to
                        it.id
                }
            val sets =
                exercises.flatMap { (e, oldId) ->
                    done.filter { it.sessionExerciseId == oldId }.mapIndexed { i, old ->
                        factory
                            .plannedSet(
                                e,
                                i,
                                now,
                            ).copy(
                                reps = old.reps,
                                loadKg = old.loadKg,
                                durationS = old.durationS,
                                rpe = old.rpe,
                                restS = old.restS,
                                setType = old.setType,
                            )
                    }
                }
            database.withWriteTransaction {
                dao.upsertSessions(listOf(session))
                dao.upsertSessionExercises(exercises.map { it.first })
                dao.upsertSets(sets)
                dao.setActive(ActiveSessionEntity(sessionId = session.id))
            }
            syncScheduler.requestSync()
            return session.id
        }
    }
