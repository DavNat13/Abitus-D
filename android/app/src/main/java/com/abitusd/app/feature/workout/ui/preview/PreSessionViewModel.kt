package com.abitusd.app.feature.workout.ui.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.feature.library.domain.ExerciseNames
import com.abitusd.app.feature.train.data.RoutineRepository
import com.abitusd.app.feature.workout.data.PreviousSession
import com.abitusd.app.feature.workout.data.SessionPlanner
import com.abitusd.app.feature.workout.data.WorkoutRepository
import com.abitusd.app.feature.workout.domain.Readiness
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PreSessionState(
    val title: String? = null,
    val exercises: List<PreviewLine> = emptyList(),
    val block: PreviewBlock = PreviewBlock(),
    val travel: Boolean = false,
    val minutes: Int = 0,
    val readiness: Readiness = Readiness(),
    val starting: Boolean = false,
    val previousTitle: String? = null,
)

/** What the session holds before it starts; starting writes it to Room as the active session. */
@HiltViewModel(assistedFactory = PreSessionViewModel.Factory::class)
class PreSessionViewModel
    @AssistedInject
    constructor(
        @Assisted("routine") private val routineId: String?,
        @Assisted("scheduled") private val scheduledWorkoutId: String?,
        private val routines: RoutineRepository,
        private val workouts: WorkoutRepository,
        private val names: ExerciseNames,
        private val previous: PreviousSession,
        private val planner: SessionPlanner,
    ) : ViewModel() {
        private val mutable = MutableStateFlow(PreSessionState())
        val state: StateFlow<PreSessionState> = mutable.asStateFlow()
        private val startedChannel = Channel<Unit>(Channel.BUFFERED)
        val started = startedChannel.receiveAsFlow()

        init {
            viewModelScope.launch {
                val routine = routineId?.let { routines.routine(it) }
                mutable.update { it.copy(title = routine?.name) }
                refresh()
            }
        }

        fun setTravel(on: Boolean) {
            mutable.update { it.copy(travel = on) }
            viewModelScope.launch { refresh() }
        }

        private suspend fun refresh() {
            val planned = routineId?.let { routines.exercisesOnce(it) }.orEmpty()
            val plan = planner.plan(planned, state.value.travel)
            val all = names.all()
            mutable.update {
                it.copy(
                    exercises = PreviewPlan.lines(plan, all),
                    block = PreviewPlan.block(plan, all),
                    minutes = PreviewPlan.minutes(plan.exercises),
                )
            }
        }

        fun setSleep(value: Int?) = mutable.update { it.copy(readiness = it.readiness.copy(sleep = value)) }

        fun setEnergy(value: Int?) = mutable.update { it.copy(readiness = it.readiness.copy(energy = value)) }

        /** Starts, or first asks what to do with an unfinished session. */
        fun start(freeTitle: String) {
            if (state.value.starting) return
            mutable.update { it.copy(starting = true) }
            viewModelScope.launch {
                val pending = previous.pending()
                if (pending != null) {
                    mutable.update { it.copy(starting = false, previousTitle = pending.title) }
                    return@launch
                }
                val s = state.value
                workouts.start(s.title ?: freeTitle, routineId, scheduledWorkoutId, s.readiness, s.travel)
                startedChannel.send(Unit)
            }
        }

        fun closePreviousSheet() = mutable.update { it.copy(previousTitle = null) }

        /** null keeps the previous session (it reopens); true saves it, false discards it. */
        fun resolvePrevious(
            save: Boolean?,
            freeTitle: String,
        ) {
            mutable.update { it.copy(previousTitle = null) }
            viewModelScope.launch {
                if (save == null) {
                    startedChannel.send(Unit)
                } else {
                    previous.close(save)
                    start(freeTitle)
                }
            }
        }

        @AssistedFactory
        interface Factory {
            fun create(
                @Assisted("routine") routineId: String?,
                @Assisted("scheduled") scheduledWorkoutId: String?,
            ): PreSessionViewModel
        }
    }
