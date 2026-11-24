package com.abitusd.app.feature.workout.data

import androidx.room3.withWriteTransaction
import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.session.ActiveSessionEntity
import com.abitusd.app.core.database.session.WorkoutSessionEntity
import com.abitusd.app.core.sync.SyncScheduler
import com.abitusd.app.feature.train.data.CalendarRepository
import com.abitusd.app.feature.train.data.RoutineRepository
import com.abitusd.app.feature.workout.domain.Readiness
import com.abitusd.app.feature.workout.domain.SessionFactory
import javax.inject.Inject
import javax.inject.Singleton

/** Session lifecycle and reads, local first (09b). Editing during a session is in SessionEditor. */
@Singleton
class WorkoutRepository
    @Inject
    constructor(
        private val database: AbitusDatabase,
        private val routines: RoutineRepository,
        private val calendar: CalendarRepository,
        private val syncScheduler: SyncScheduler,
        private val planner: SessionPlanner,
    ) {
        private val dao get() = database.sessionDao()

        fun active() = dao.activeFlow()

        fun session(id: String) = dao.sessionFlow(id)

        fun exercises(sessionId: String) = dao.sessionExercises(sessionId)

        fun sets(sessionId: String) = dao.setsOfSession(sessionId)

        fun history() = dao.finishedSessions()

        suspend fun lastTime(
            exerciseId: String,
            sessionId: String,
        ) = dao.lastTime(exerciseId, sessionId)

        /** Starts from a routine (or empty when null) and makes it the active session. */
        suspend fun start(
            title: String,
            routineId: String?,
            scheduledWorkoutId: String?,
            readiness: Readiness,
            travel: Boolean = false,
        ): String {
            val planned = routineId?.let { planner.plan(routines.exercisesOnce(it), travel).exercises }.orEmpty()
            val now = System.currentTimeMillis()
            val built = SessionFactory().fromRoutine(title, routineId, scheduledWorkoutId, planned, now, readiness)
            database.withWriteTransaction {
                dao.upsertSessions(listOf(built.session))
                dao.upsertSessionExercises(built.exercises)
                dao.upsertSets(built.sets)
                dao.setActive(ActiveSessionEntity(sessionId = built.session.id))
            }
            syncScheduler.requestSync()
            return built.session.id
        }

        /** Remembers the visible exercise so the session reopens where it was. */
        suspend fun setPosition(index: Int) {
            dao.active()?.let { dao.setActive(it.copy(exerciseIndex = index)) }
        }

        /** Persists the running rest (wall-clock end) so it survives process death. */
        suspend fun setRest(
            endsAt: Long?,
            totalS: Int,
        ) {
            dao.active()?.let { dao.setActive(it.copy(restEndsAt = endsAt, restTotalS = totalS)) }
        }

        /** Ends the session: pending sets are dropped and the scheduled workout becomes done. */
        suspend fun finish(
            sessionId: String,
            sessionRpe: Double?,
            notes: String,
        ) {
            val session = dao.session(sessionId) ?: return
            val now = System.currentTimeMillis()
            val pending = dao.setsOfSessionOnce(sessionId).filter { it.completedAt == null }
            database.withWriteTransaction {
                dao.upsertSets(pending.map { it.copy(deletedAt = now, updatedAt = now, dirty = true) })
                val ended =
                    session.copy(
                        endedAt = now,
                        sessionRpe = sessionRpe,
                        notes = notes,
                        updatedAt = now,
                        dirty = true,
                    )
                dao.upsertSessions(listOf(ended))
                dao.clearActive()
            }
            session.scheduledWorkoutId?.let { calendar.markDone(it, sessionId) }
            syncScheduler.requestSync()
        }

        /** Throws the session away (tombstone) and leaves no active session. */
        suspend fun discard(session: WorkoutSessionEntity) {
            val now = System.currentTimeMillis()
            database.withWriteTransaction {
                dao.upsertSessions(listOf(session.copy(deletedAt = now, updatedAt = now, dirty = true)))
                dao.clearActive()
            }
            syncScheduler.requestSync()
        }
    }
