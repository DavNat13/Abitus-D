package com.abitusd.app.feature.train.data

import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.planning.ScheduledWorkoutEntity
import com.abitusd.app.core.timer.WorkoutReminders
import com.abitusd.app.feature.train.domain.PlannedDay
import com.abitusd.app.feature.train.domain.WeekFit
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/** Week-level calendar planning (10f Fase 4b): availability and planned time with reminder. */
@Singleton
class CalendarPlanning
    @Inject
    constructor(
        private val database: AbitusDatabase,
        private val calendar: CalendarRepository,
        private val reminders: WorkoutReminders,
    ) {
        /**
         * Weekly availability (10f Fase 4b): the planned workouts of the week starting on [monday]
         * keep their order on the [days] given (0 Monday). Extra workouts share the last free day.
         */
        suspend fun fitWeek(
            monday: LocalDate,
            days: Set<Int>,
        ) {
            if (days.isEmpty()) return
            val rows =
                database
                    .planningDao()
                    .scheduledBetweenOnce(
                        monday.toString(),
                        monday.plusDays(LAST_DAY).toString(),
                    ).filter {
                        it.status ==
                            "planned"
                    }
            val week =
                rows.sortedBy { it.date }.map {
                    PlannedDay(
                        it,
                        (
                            LocalDate.parse(it.date).toEpochDay() -
                                monday.toEpochDay()
                        ).toInt(),
                    )
                }
            val fit = WeekFit.fit(week, days)
            val last = days.max().toLong()
            val moved =
                fit.placed.map { (it.item as ScheduledWorkoutEntity) to it.dayIndex.toLong() } +
                    fit.unplaced.map { (it as ScheduledWorkoutEntity) to last }
            calendar.write(
                moved.filter { (row, day) -> row.date != monday.plusDays(day).toString() }.map { (row, day) ->
                    row.copy(date = monday.plusDays(day).toString(), rescheduledFrom = row.rescheduledFrom ?: row.date)
                },
            )
        }

        /** Planned time (HH:mm) or null, and its reminder. */
        suspend fun setTime(
            id: String,
            time: String?,
            title: String,
        ) {
            val row = database.planningDao().scheduled(id) ?: return
            calendar.write(listOf(row.copy(plannedTime = time)))
            reminders.schedule(id, LocalDate.parse(row.date), time, title)
        }

        private companion object {
            const val LAST_DAY = 6L
        }
    }
