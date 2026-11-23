package com.abitusd.app.feature.train.data

import androidx.room3.withWriteTransaction
import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.planning.ProgramDayEntity
import com.abitusd.app.core.database.planning.ProgramEntity
import com.abitusd.app.core.database.planning.ScheduledWorkoutEntity
import com.abitusd.app.core.sync.SyncScheduler
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Programs by weeks and days. Activating one fills the calendar from its start date. */
@Singleton
class ProgramRepository
    @Inject
    constructor(
        private val database: AbitusDatabase,
        private val calendar: CalendarRepository,
        private val syncScheduler: SyncScheduler,
    ) {
        private val dao get() = database.planningDao()

        fun programs() = dao.programs()

        fun days(programId: String) = dao.programDays(programId)

        suspend fun program(id: String) = dao.program(id)?.takeIf { it.deletedAt == null }

        suspend fun daysOnce(programId: String) = dao.programDaysOnce(programId)

        /** Saves the program and its days; days outside the weeks count become tombstones. */
        suspend fun save(
            program: ProgramEntity,
            days: List<ProgramDayEntity>,
        ) {
            val now = System.currentTimeMillis()
            database.withWriteTransaction {
                val keep = days.filter { it.weekIndex < program.weeksCount }
                val keepIds = keep.map { it.id }.toSet()
                val removed =
                    dao.programDaysOnce(program.id).filter { it.id !in keepIds }.map {
                        it.copy(deletedAt = now, updatedAt = now, dirty = true)
                    }
                dao.upsertPrograms(listOf(program.copy(updatedAt = now, dirty = true)))
                dao.upsertProgramDays(
                    removed + keep.map { it.copy(programId = program.id, updatedAt = now, dirty = true) },
                )
            }
            syncScheduler.requestSync()
        }

        /** Marks the program active and schedules each training day from the start date. */
        suspend fun activate(
            id: String,
            start: LocalDate,
        ) {
            val program = program(id) ?: return
            save(program.copy(isActive = true, startDate = start.toString()), daysOnce(id))
            val rows =
                daysOnce(id).filter { (!it.isRest && it.routineId != null) || it.activeRest }.map { day ->
                    ScheduledWorkoutEntity(
                        id = UUID.randomUUID().toString(),
                        date = start.plusDays(day.weekIndex * DAYS_PER_WEEK + day.dayIndex.toLong()).toString(),
                        routineId = day.routineId,
                        programDayId = day.id,
                        updatedAt = 0,
                    )
                }
            calendar.write(rows)
        }

        suspend fun delete(id: String) {
            val program = dao.program(id) ?: return
            save(program.copy(deletedAt = System.currentTimeMillis()), emptyList())
        }

        companion object {
            const val DAYS_PER_WEEK = 7L

            fun newProgram(
                name: String,
                weeks: Int,
            ) = ProgramEntity(id = UUID.randomUUID().toString(), name = name, weeksCount = weeks, updatedAt = 0)

            fun newDay(
                programId: String,
                week: Int,
                day: Int,
                routineId: String?,
            ) = ProgramDayEntity(
                id = UUID.randomUUID().toString(),
                programId = programId,
                weekIndex = week,
                dayIndex = day,
                routineId = routineId,
                isRest = routineId == null,
                updatedAt = 0,
            )
        }
    }
