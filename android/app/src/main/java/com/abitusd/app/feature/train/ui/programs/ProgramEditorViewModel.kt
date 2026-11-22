package com.abitusd.app.feature.train.ui.programs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.ProgramDayEntity
import com.abitusd.app.core.database.planning.ProgramEntity
import com.abitusd.app.core.database.planning.RoutineEntity
import com.abitusd.app.feature.train.data.ProgramRepository
import com.abitusd.app.feature.train.data.RoutineRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ProgramEditorState(
    val isNew: Boolean = true,
    val program: ProgramEntity = ProgramRepository.newProgram("", DEFAULT_WEEKS),
    val days: List<ProgramDayEntity> = emptyList(),
    val routines: List<RoutineEntity> = emptyList(),
    val error: Int? = null,
)

private const val DEFAULT_WEEKS = 4

/** Choice for one cell of the weekly grid. */
sealed interface DayChoice {
    data object Unassigned : DayChoice

    data object Rest : DayChoice

    data class Train(
        val routineId: String,
    ) : DayChoice
}

@HiltViewModel(assistedFactory = ProgramEditorViewModel.Factory::class)
class ProgramEditorViewModel
    @AssistedInject
    constructor(
        @Assisted private val programId: String?,
        private val repository: ProgramRepository,
        routines: RoutineRepository,
    ) : ViewModel() {
        private val mutable = MutableStateFlow(ProgramEditorState())
        val state: StateFlow<ProgramEditorState> = mutable.asStateFlow()
        private val doneChannel = Channel<Unit>(Channel.BUFFERED)
        val done = doneChannel.receiveAsFlow()

        init {
            viewModelScope.launch {
                val active = routines.routines().first().filterNot { it.archived }
                val program = programId?.let { repository.program(it) }
                val days = programId?.let { repository.daysOnce(it) }.orEmpty()
                mutable.update {
                    it.copy(
                        isNew = program == null,
                        program = program ?: it.program,
                        days = days,
                        routines = active,
                    )
                }
            }
        }

        fun setName(name: String) =
            mutable.update {
                it.copy(program = it.program.copy(name = name.take(MAX_NAME)), error = null)
            }

        fun setWeeks(weeks: Int) =
            mutable.update {
                it.copy(program = it.program.copy(weeksCount = weeks.coerceIn(1, MAX_WEEKS)))
            }

        fun setDay(
            week: Int,
            day: Int,
            choice: DayChoice,
        ) = mutable.update { s ->
            val existing = s.days.firstOrNull { it.weekIndex == week && it.dayIndex == day }
            val others = s.days.filterNot { it === existing }
            val base = existing ?: ProgramRepository.newDay(s.program.id, week, day, null)
            val updated =
                when (choice) {
                    DayChoice.Unassigned -> null
                    DayChoice.Rest -> base.copy(isRest = true, routineId = null)
                    is DayChoice.Train -> base.copy(isRest = false, routineId = choice.routineId)
                }
            s.copy(days = others + listOfNotNull(updated))
        }

        fun save() = persist {}

        fun activate() = persist { repository.activate(it, LocalDate.now()) }

        fun delete() {
            viewModelScope.launch {
                repository.delete(state.value.program.id)
                doneChannel.send(Unit)
            }
        }

        private fun persist(after: suspend (String) -> Unit) {
            val s = state.value
            if (s.program.name.isBlank()) {
                mutable.update { it.copy(error = R.string.routine_name_required) }
                return
            }
            viewModelScope.launch {
                repository.save(s.program.copy(name = s.program.name.trim()), s.days)
                after(s.program.id)
                doneChannel.send(Unit)
            }
        }

        @AssistedFactory
        interface Factory {
            fun create(programId: String?): ProgramEditorViewModel
        }

        private companion object {
            const val MAX_NAME = 80
            const val MAX_WEEKS = 52
        }
    }
