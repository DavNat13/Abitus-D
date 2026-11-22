package com.abitusd.app.feature.train.ui.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.RoutineEntity
import com.abitusd.app.core.database.planning.RoutineExerciseEntity
import com.abitusd.app.core.i18n.AppLocale
import com.abitusd.app.feature.library.domain.ExerciseNames
import com.abitusd.app.feature.train.data.CalendarRepository
import com.abitusd.app.feature.train.data.RoutineRepository
import com.abitusd.app.feature.train.data.VolumeCalculator
import com.abitusd.app.feature.train.domain.PlannedSets
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
import java.time.LocalDate

/** Edits a routine locally; saving writes Room first and requests a sync. */
@HiltViewModel(assistedFactory = RoutineEditorViewModel.Factory::class)
class RoutineEditorViewModel
    @AssistedInject
    constructor(
        @Assisted private val routineId: String?,
        private val repository: RoutineRepository,
        private val calendar: CalendarRepository,
        private val volumes: VolumeCalculator,
        private val exerciseNames: ExerciseNames,
    ) : ViewModel() {
        private val locale = AppLocale.current()
        private val mutable = MutableStateFlow(RoutineEditorState())
        val state: StateFlow<RoutineEditorState> = mutable.asStateFlow()
        private val doneChannel = Channel<Unit>(Channel.BUFFERED)
        val done = doneChannel.receiveAsFlow()

        init {
            viewModelScope.launch {
                val names = exerciseNames.all()
                val routine = routineId?.let { repository.routine(it) }
                val items = routineId?.let { repository.exercisesOnce(it) }.orEmpty()
                mutable.update {
                    it.copy(
                        isNew = routine == null,
                        routine = routine ?: it.routine,
                        items = items,
                        names = names,
                    )
                }
                refreshVolume()
            }
        }

        fun editRoutine(transform: (RoutineEntity) -> RoutineEntity) =
            mutable.update {
                it.copy(routine = transform(it.routine), error = null)
            }

        fun editItem(
            id: String,
            transform: (RoutineExerciseEntity) -> RoutineExerciseEntity,
        ) {
            mutable.update { s -> s.copy(items = s.items.map { if (it.id == id) transform(it) else it }) }
            viewModelScope.launch { refreshVolume() }
        }

        fun setPicking(value: Boolean) = mutable.update { it.copy(picking = value) }

        fun addExercise(exerciseId: String) {
            val item = RoutineRepository.newExercise(state.value.routine.id, exerciseId, null)
            mutable.update { it.copy(items = it.items + item, picking = false) }
            viewModelScope.launch { refreshVolume() }
        }

        fun move(
            id: String,
            delta: Int,
        ) = mutable.update { s ->
            val i = s.items.indexOfFirst { it.id == id }
            val j = i + delta
            if (i < 0 ||
                j !in s.items.indices
            ) {
                s
            } else {
                s.copy(items = s.items.toMutableList().apply { add(j, removeAt(i)) })
            }
        }

        fun remove(id: String) {
            mutable.update { s -> s.copy(items = s.items.filterNot { it.id == id }) }
            viewModelScope.launch { refreshVolume() }
        }

        fun save() {
            val s = state.value
            if (s.routine.name.isBlank()) {
                mutable.update { it.copy(error = R.string.routine_name_required) }
                return
            }
            viewModelScope.launch {
                repository.save(s.routine.copy(name = s.routine.name.trim()), s.items)
                doneChannel.send(Unit)
            }
        }

        /** Runs an action on the saved routine and closes the editor. */
        fun run(
            action: RoutineAction,
            copySuffix: String = "",
        ) {
            val id = state.value.routine.id
            viewModelScope.launch {
                when (action) {
                    RoutineAction.DUPLICATE -> repository.duplicate(id, copySuffix)
                    RoutineAction.ARCHIVE -> repository.setArchived(id, !state.value.routine.archived)
                    RoutineAction.DELETE -> repository.delete(id)
                    RoutineAction.SCHEDULE_TODAY -> calendar.schedule(id, LocalDate.now())
                }
                doneChannel.send(Unit)
            }
        }

        private suspend fun refreshVolume() {
            val sets = state.value.items.map { PlannedSets(it.exerciseId, it.targetSets ?: 0) }
            val volume = volumes.perMuscle(sets, locale)
            mutable.update { it.copy(volume = volume) }
        }

        @AssistedFactory
        interface Factory {
            fun create(routineId: String?): RoutineEditorViewModel
        }
    }
