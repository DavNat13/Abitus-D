package com.abitusd.app.feature.workout.ui.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.core.database.planning.RoutineExerciseEntity
import com.abitusd.app.feature.library.domain.ExerciseNames
import com.abitusd.app.feature.train.data.RoutineRepository
import com.abitusd.app.feature.workout.data.PreviousSession
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
    val exercises: List<Pair<String, Int>> = emptyList(),
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
    ) : ViewModel() {
        private val mutable = MutableStateFlow(PreSessionState())
        val state: StateFlow<PreSessionState> = mutable.asStateFlow()
        private val startedChannel = Channel<Unit>(Channel.BUFFERED)
        val started = startedChannel.receiveAsFlow()

        init {
            viewModelScope.launch {
                val routine = routineId?.let { routines.routine(it) }
                val planned = routineId?.let { routines.exercisesOnce(it) }.orEmpty()
                val all = names.all()
                mutable.update {
                    it.copy(
                        title = routine?.name,
                        exercises = planned.map { p -> all[p.exerciseId].orEmpty() to (p.targetSets ?: DEFAULT_SETS) },
                        minutes = estimateMinutes(planned),
                    )
                }
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
                workouts.start(state.value.title ?: freeTitle, routineId, scheduledWorkoutId, state.value.readiness)
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

        /** About 45 s per set plus its planned rest. */
        private fun estimateMinutes(planned: List<RoutineExerciseEntity>): Int =
            (
                planned.sumOf { (it.targetSets ?: DEFAULT_SETS) * (SET_SECONDS + (it.restSeconds ?: DEFAULT_REST)) } /
                    SECONDS_PER_MIN
            )

        @AssistedFactory
        interface Factory {
            fun create(
                @Assisted("routine") routineId: String?,
                @Assisted("scheduled") scheduledWorkoutId: String?,
            ): PreSessionViewModel
        }

        private companion object {
            const val DEFAULT_SETS = 3
            const val SET_SECONDS = 45
            const val DEFAULT_REST = 90
            const val SECONDS_PER_MIN = 60
        }
    }
