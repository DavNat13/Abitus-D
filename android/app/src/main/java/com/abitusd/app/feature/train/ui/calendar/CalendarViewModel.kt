package com.abitusd.app.feature.train.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.core.database.planning.ScheduledWorkoutEntity
import com.abitusd.app.core.i18n.AppLocale
import com.abitusd.app.feature.train.data.CalendarPlanning
import com.abitusd.app.feature.train.data.CalendarRepository
import com.abitusd.app.feature.train.data.RoutineRepository
import com.abitusd.app.feature.train.data.VolumeCalculator
import com.abitusd.app.feature.train.domain.PlannedSets
import com.abitusd.app.feature.train.domain.PlannedVolume
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

data class CalendarState(
    val month: YearMonth = YearMonth.now(),
    val selected: LocalDate = LocalDate.now(),
    val scheduled: List<ScheduledWorkoutEntity> = emptyList(),
    val routineNames: Map<String, String> = emptyMap(),
    val dayLoad: Int = 0,
    val weekVolume: List<com.abitusd.app.feature.train.data.MuscleVolume> = emptyList(),
)

/** Monthly planning calendar; every change is written to Room and synced later. */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel
    @Inject
    constructor(
        private val calendar: CalendarRepository,
        private val planning: CalendarPlanning,
        private val routines: RoutineRepository,
        private val volumes: VolumeCalculator,
    ) : ViewModel() {
        private val locale = AppLocale.current()
        private val month = MutableStateFlow(YearMonth.now())
        private val selected = MutableStateFlow(LocalDate.now())
        private val scheduled =
            month.flatMapLatest { m ->
                calendar.between(m.atDay(1).minusDays(MARGIN_DAYS), m.atEndOfMonth().plusDays(MARGIN_DAYS))
            }

        val state =
            combine(month, selected, scheduled, routines.routines()) { m, day, rows, list ->
                Input(
                    m,
                    day,
                    rows,
                    list.associate {
                        it.id to
                            it.name
                    },
                )
            }.mapLatest { build(it) }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), CalendarState())

        fun shiftMonth(delta: Long) {
            month.value = month.value.plusMonths(delta)
        }

        fun select(date: LocalDate) {
            selected.value = date
        }

        fun add(routineId: String) = launch { calendar.schedule(routineId, selected.value) }

        fun skip(id: String) = launch { calendar.setStatus(id, STATUS_SKIPPED) }

        fun reschedule(
            id: String,
            date: LocalDate,
        ) = launch { calendar.reschedule(id, date) }

        fun remove(id: String) = launch { calendar.delete(id) }

        fun setTime(
            id: String,
            time: String?,
            title: String,
        ) = launch { planning.setTime(id, time, title) }

        fun fitWeek(days: Set<Int>) =
            launch { planning.fitWeek(selected.value.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), days) }

        private fun launch(block: suspend () -> Unit) {
            viewModelScope.launch { block() }
        }

        private suspend fun build(input: Input): CalendarState {
            val dayRows = input.rows.filter { it.date == input.day.toString() && it.status != STATUS_SKIPPED }
            val weekStart = input.day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val weekDates = (0L until DAYS_PER_WEEK).map { weekStart.plusDays(it).toString() }.toSet()
            val weekRows = input.rows.filter { it.date in weekDates && it.status != STATUS_SKIPPED }
            val weekSets = weekRows.flatMap { setsOf(it.routineId) }
            return CalendarState(
                month = input.month,
                selected = input.day,
                scheduled = input.rows,
                routineNames = input.names,
                dayLoad = PlannedVolume.totalSets(dayRows.flatMap { setsOf(it.routineId) }),
                weekVolume = volumes.byMuscle(weekSets, locale),
            )
        }

        private suspend fun setsOf(routineId: String?): List<PlannedSets> =
            routineId
                ?.let {
                    routines.exercisesOnce(
                        it,
                    )
                }.orEmpty()
                .map { PlannedSets(it.exerciseId, it.targetSets ?: 0) }

        private data class Input(
            val month: YearMonth,
            val day: LocalDate,
            val rows: List<ScheduledWorkoutEntity>,
            val names: Map<String, String>,
        )

        companion object {
            const val STATUS_SKIPPED = "skipped"
            const val STATUS_DONE = "done"
            private const val MARGIN_DAYS = 6L
            private const val DAYS_PER_WEEK = 7L
            private const val STOP_MS = 5_000L
        }
    }
