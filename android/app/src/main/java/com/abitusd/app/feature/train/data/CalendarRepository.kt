package com.abitusd.app.feature.train.data

import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.planning.ScheduledWorkoutEntity
import com.abitusd.app.core.sync.SyncScheduler
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Scheduled workouts. Rescheduling keeps the same row, so its history stays attached. */
@Singleton
class CalendarRepository
    @Inject
    constructor(
        private val database: AbitusDatabase,
        private val syncScheduler: SyncScheduler,
    ) {
        private val dao get() = database.planningDao()

        fun between(
            from: LocalDate,
            to: LocalDate,
        ) = dao.scheduledBetween(from.toString(), to.toString())

        suspend fun schedule(
            routineId: String,
            date: LocalDate,
            programDayId: String? = null,
        ) {
            val row =
                ScheduledWorkoutEntity(
                    id = UUID.randomUUID().toString(),
                    date = date.toString(),
                    routineId = routineId,
                    programDayId = programDayId,
                    updatedAt = 0,
                )
            write(listOf(row))
        }

        suspend fun reschedule(
            id: String,
            newDate: LocalDate,
        ) {
            val row = dao.scheduled(id) ?: return
            val original = row.rescheduledFrom ?: row.date
            write(listOf(row.copy(date = newDate.toString(), rescheduledFrom = original, status = "planned")))
        }

        suspend fun setStatus(
            id: String,
            status: String,
        ) {
            val row = dao.scheduled(id) ?: return
            write(listOf(row.copy(status = status)))
        }

        /** Links the finished session and marks the planned workout as done. */
        suspend fun markDone(
            id: String,
            sessionId: String,
        ) {
            val row = dao.scheduled(id) ?: return
            write(listOf(row.copy(status = "done", sessionId = sessionId)))
        }

        suspend fun delete(id: String) {
            val row = dao.scheduled(id) ?: return
            write(listOf(row.copy(deletedAt = System.currentTimeMillis())))
        }

        suspend fun write(rows: List<ScheduledWorkoutEntity>) {
            if (rows.isEmpty()) return
            val now = System.currentTimeMillis()
            dao.upsertScheduled(
                rows.map { it.copy(updatedAt = now, dirty = true, deletedAt = it.deletedAt?.let { now }) },
            )
            syncScheduler.requestSync()
        }
    }
