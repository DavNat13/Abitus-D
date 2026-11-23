package com.abitusd.app.feature.workout.ui.hub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.ScheduledWorkoutEntity
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.feature.train.data.CalendarRepository
import com.abitusd.app.feature.train.data.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/** Most recent planned workout of the last week that was not done. */
data class MissedSession(
    val row: ScheduledWorkoutEntity,
    val name: String,
)

@HiltViewModel
class MissedSessionViewModel
    @Inject
    constructor(
        private val calendar: CalendarRepository,
        routines: RoutineRepository,
    ) : ViewModel() {
        private val today = LocalDate.now()
        val missed =
            combine(
                calendar.between(today.minusDays(LOOKBACK_DAYS), today.minusDays(1)),
                routines.routines(),
            ) { rows, list ->
                rows
                    .filter { it.status == "planned" && it.routineId != null }
                    .maxByOrNull { it.date }
                    ?.let { row -> MissedSession(row, list.firstOrNull { it.id == row.routineId }?.name.orEmpty()) }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), null)

        fun doToday(row: ScheduledWorkoutEntity) = viewModelScope.launch { calendar.reschedule(row.id, today) }

        fun skip(row: ScheduledWorkoutEntity) = viewModelScope.launch { calendar.setStatus(row.id, "skipped") }

        /** Moves the whole remaining plan so the missed workout lands today. */
        fun pushPlan(row: ScheduledWorkoutEntity) =
            viewModelScope.launch {
                val from = LocalDate.parse(row.date)
                calendar.pushPlan(from, ChronoUnit.DAYS.between(from, today))
            }

        private companion object {
            const val LOOKBACK_DAYS = 7L
            const val STOP_MS = 5_000L
        }
    }

/** Missed workout (10f): do it today, skip it or push the plan. Hidden when nothing is missed. */
@Composable
fun MissedSessionCard(viewModel: MissedSessionViewModel = hiltViewModel()) {
    val missed by viewModel.missed.collectAsStateWithLifecycle()
    val item = missed ?: return
    val locale = LocalConfiguration.current.locales[0]
    val day = LocalDate.parse(item.row.date).format(DateTimeFormatter.ofPattern("EEEE d", locale))
    SectionCard(stringResource(R.string.missed_title, item.name, day), icon = AppIcons.Reschedule) {
        AppButton(stringResource(R.string.missed_today), { viewModel.doToday(item.row) }, icon = AppIcons.Today)
        AppButton(stringResource(R.string.missed_push), {
            viewModel.pushPlan(item.row)
        }, style = AppButtonStyle.Tonal, icon = AppIcons.Reschedule)
        AppButton(stringResource(R.string.missed_skip), {
            viewModel.skip(item.row)
        }, style = AppButtonStyle.Text, icon = AppIcons.Skip)
    }
}
