package com.abitusd.app.feature.workout.data

import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.session.WorkoutSessionEntity
import javax.inject.Inject

/**
 * The active session that was never finished. Before starting a new one the user decides:
 * continue it, save it as it is, or discard it (no session is left hanging on the server).
 */
class PreviousSession
    @Inject
    constructor(
        private val database: AbitusDatabase,
        private val workouts: WorkoutRepository,
        private val rest: SessionRest,
    ) {
        private val dao get() = database.sessionDao()

        suspend fun pending(): WorkoutSessionEntity? {
            val active = dao.active() ?: return null
            return dao.session(active.sessionId)?.takeIf { it.endedAt == null && it.deletedAt == null }
        }

        suspend fun close(save: Boolean) {
            val session = pending() ?: return
            rest.skip()
            if (save) workouts.finish(session.id, session.sessionRpe, session.notes) else workouts.discard(session)
        }
    }
