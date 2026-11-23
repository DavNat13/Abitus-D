package com.abitusd.app.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.core.database.planning.ScheduledWorkoutEntity
import com.abitusd.app.feature.train.data.CalendarRepository
import com.abitusd.app.feature.train.data.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

/** One planned workout shown on Home. */
data class HomeWorkout(
    val date: LocalDate,
    val routineName: String,
    val status: String,
)

data class HomeState(
    val loading: Boolean = true,
    val today: List<HomeWorkout> = emptyList(),
    val next: HomeWorkout? = null,
    val weekPlanned: Int = 0,
    val weekDone: Int = 0,
)

/** Reads the local plan only (Room): today, the next workout and this week's counts. */
@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        calendar: CalendarRepository,
        routines: RoutineRepository,
    ) : ViewModel() {
        private val today = LocalDate.now()
        private val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        val state: StateFlow<HomeState> =
            combine(calendar.between(weekStart, today.plusDays(LOOKAHEAD_DAYS)), routines.routines()) { rows, list ->
                val names = list.associate { it.id to it.name }

                fun ScheduledWorkoutEntity.toHome() =
                    HomeWorkout(LocalDate.parse(date), names[routineId].orEmpty(), status)
                val active = rows.filter { it.status != STATUS_SKIPPED }
                val week = active.filter { LocalDate.parse(it.date) < weekStart.plusDays(DAYS_PER_WEEK) }
                HomeState(
                    loading = false,
                    today = active.filter { it.date == today.toString() }.map { it.toHome() },
                    next =
                        active
                            .filter {
                                LocalDate.parse(it.date) > today && it.status == STATUS_PLANNED
                            }.minByOrNull { it.date }
                            ?.toHome(),
                    weekPlanned = week.size,
                    weekDone = week.count { it.status == STATUS_DONE },
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), HomeState())

        private companion object {
            const val LOOKAHEAD_DAYS = 14L
            const val DAYS_PER_WEEK = 7L
            const val STOP_MS = 5_000L
            const val STATUS_SKIPPED = "skipped"
            const val STATUS_DONE = "done"
            const val STATUS_PLANNED = "planned"
        }
    }
