package com.abitusd.app.feature.workout.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.core.database.session.WorkoutHistoryDao
import com.abitusd.app.feature.library.domain.ExerciseNames
import com.abitusd.app.feature.workout.data.WorkoutRepository
import com.abitusd.app.feature.workout.domain.SessionStats
import com.abitusd.app.feature.workout.domain.SessionSummary
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

/** Exercise of the summary with its completed sets, as display values. */
data class SummaryExercise(
    val name: String,
    val sets: List<com.abitusd.app.core.database.session.SetEntity>,
)

data class SummaryState(
    val title: String = "",
    val summary: SessionSummary? = null,
    val exercises: List<SummaryExercise> = emptyList(),
    val names: Map<String, String> = emptyMap(),
    val rpe: Int? = null,
    val notes: String = "",
    val templateSaved: Boolean = false,
)

/** Session summary before saving, or a finished session from history (read only). */
@HiltViewModel(assistedFactory = SummaryViewModel.Factory::class)
class SummaryViewModel
    @AssistedInject
    constructor(
        @Assisted private val sessionId: String,
        private val workouts: WorkoutRepository,
        private val history: WorkoutHistoryDao,
        private val exerciseNames: ExerciseNames,
        private val templates: SessionTemplate,
        private val repeater: com.abitusd.app.feature.workout.data.SessionRepeater,
    ) : ViewModel() {
        private val mutable = MutableStateFlow(SummaryState())
        val state: StateFlow<SummaryState> = mutable.asStateFlow()
        private val doneChannel = Channel<Unit>(Channel.BUFFERED)
        val done = doneChannel.receiveAsFlow()
        private val repeatChannel = Channel<Unit>(Channel.BUFFERED)
        val repeated = repeatChannel.receiveAsFlow()

        init {
            viewModelScope.launch {
                val session = workouts.session(sessionId).first() ?: return@launch
                val exercises = workouts.exercises(sessionId).first()
                val done = workouts.sets(sessionId).first().filter { it.completedAt != null }
                val previous = history.completedSetsOf(exercises.map { it.exerciseId }.distinct(), sessionId)
                val best =
                    previous.groupBy { it.exerciseId }.mapValues { (_, sets) ->
                        sets.maxOf {
                            SessionStats.estimatedMax(it)
                                ?: 0.0
                        }
                    }
                val names = exerciseNames.all()
                val end = session.endedAt ?: System.currentTimeMillis()
                mutable.update {
                    it.copy(
                        title = session.title,
                        summary = SessionStats.summary(session.startedAt, end, done, best),
                        exercises =
                            exercises.map { e ->
                                SummaryExercise(
                                    names[e.exerciseId].orEmpty(),
                                    done.filter { s ->
                                        s.sessionExerciseId ==
                                            e.id
                                    },
                                )
                            },
                        names = names,
                        rpe = session.sessionRpe?.toInt(),
                        notes = session.notes,
                    )
                }
            }
        }

        fun setRpe(value: Int?) = mutable.update { it.copy(rpe = value) }

        fun setNotes(value: String) = mutable.update { it.copy(notes = value.take(MAX_NOTES)) }

        fun save() =
            launch {
                workouts.finish(sessionId, state.value.rpe?.toDouble(), state.value.notes.trim())
                doneChannel.send(Unit)
            }

        fun saveAsTemplate() =
            launch {
                templates.fromSession(sessionId, state.value.title)
                mutable.update { it.copy(templateSaved = true) }
            }

        fun repeat() =
            launch {
                repeater.repeat(sessionId)
                repeatChannel.send(Unit)
            }

        fun discard() =
            launch {
                workouts.session(sessionId).first()?.let { workouts.discard(it) }
                doneChannel.send(Unit)
            }

        private fun launch(block: suspend () -> Unit) {
            viewModelScope.launch { block() }
        }

        @AssistedFactory
        interface Factory {
            fun create(sessionId: String): SummaryViewModel
        }

        private companion object {
            const val MAX_NOTES = 2000
        }
    }
