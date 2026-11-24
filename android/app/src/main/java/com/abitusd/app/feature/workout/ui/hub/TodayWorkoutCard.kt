package com.abitusd.app.feature.workout.ui.hub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.ScheduledWorkoutEntity
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.HeroCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.feature.train.data.CalendarRepository
import com.abitusd.app.feature.train.data.RoutineRepository
import com.abitusd.app.feature.workout.data.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

data class TrainHubState(
    val activeTitle: String? = null,
    val today: ScheduledWorkoutEntity? = null,
    val todayName: String = "",
)

@HiltViewModel
class TrainHubViewModel
    @Inject
    constructor(
        workouts: WorkoutRepository,
        calendar: CalendarRepository,
        routines: RoutineRepository,
    ) : ViewModel() {
        private val today = LocalDate.now()
        val state =
            combine(
                activeSession(workouts),
                calendar.between(today, today),
                routines.routines(),
            ) { active, rows, list ->
                val planned = rows.firstOrNull { it.status == "planned" && it.routineId != null }
                TrainHubState(
                    activeTitle = active?.title,
                    today = planned,
                    todayName = list.firstOrNull { it.id == planned?.routineId }?.name.orEmpty(),
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), TrainHubState())

        private companion object {
            const val STOP_MS = 5_000L
        }
    }

/** Top of Train (10f): continue the active session, start today's, or train freely; history. */
@Composable
fun TodayWorkoutCard(
    onStart: (routineId: String?, scheduledId: String?) -> Unit,
    onContinue: () -> Unit,
    onHistory: () -> Unit,
    viewModel: TrainHubViewModel = hiltViewModel(),
) {
    val s by viewModel.state.collectAsStateWithLifecycle()
    val today = s.today
    Column(
        Modifier.padding(horizontal = AppSpacing.screenMargin),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
    ) {
        MissedSessionCard()
        BlockProgressCard()
        when {
            s.activeTitle != null -> {
                HeroCard(
                    stringResource(R.string.workout_in_progress),
                    s.activeTitle.orEmpty(),
                    stringResource(R.string.workout_continue),
                    onContinue,
                )
            }

            today != null -> {
                HeroCard(stringResource(R.string.workout_today), s.todayName, stringResource(R.string.workout_start), {
                    onStart(today.routineId, today.id)
                })
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            AppButton(stringResource(R.string.workout_free), {
                onStart(null, null)
            }, Modifier.weight(1f), AppButtonStyle.Tonal, icon = AppIcons.Start)
            AppButton(
                stringResource(R.string.workout_history),
                onHistory,
                Modifier.weight(1f),
                AppButtonStyle.Tonal,
                icon = AppIcons.History,
            )
        }
    }
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
private fun activeSession(workouts: WorkoutRepository) =
    workouts.active().flatMapLatest { active -> active?.let { workouts.session(it.sessionId) } ?: flowOf(null) }
