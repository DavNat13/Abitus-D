package com.abitusd.app.feature.workout.ui.active

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.BuildConfig
import com.abitusd.app.core.database.session.SetEntity
import com.abitusd.app.feature.library.domain.ExerciseNames
import com.abitusd.app.feature.workout.data.SessionEditor
import com.abitusd.app.feature.workout.data.SessionRest
import com.abitusd.app.feature.workout.data.WorkoutRepository
import com.abitusd.app.feature.workout.domain.SetChange
import com.abitusd.app.feature.workout.domain.UndoStack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** The active session: one fast Room write per action, undo and redo, rest by exact alarm. */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ActiveSessionViewModel
    @Inject
    constructor(
        private val workouts: WorkoutRepository,
        private val editor: SessionEditor,
        private val names: ExerciseNames,
        val rest: SessionRest,
    ) : ViewModel() {
        private val undo = UndoStack()
        private val history = MutableStateFlow(0)
        private var nameCache: Map<String, String> = emptyMap()
        private val lastTimes = mutableMapOf<String, List<SetEntity>>()

        val state: StateFlow<ActiveSessionState> =
            workouts
                .active()
                .filterNotNull()
                .flatMapLatest { active ->
                    val id = active.sessionId
                    combine(
                        workouts.session(id),
                        workouts.exercises(id),
                        workouts.sets(id),
                        history,
                    ) { session, exercises, sets, _ ->
                        Snapshot(active, session, exercises, sets)
                    }
                }.mapLatest { snap ->
                    if (nameCache.isEmpty()) nameCache = names.all()
                    snap.exercises.filter { it.exerciseId !in lastTimes }.forEach {
                        lastTimes[it.exerciseId] = workouts.lastTime(it.exerciseId, it.sessionId)
                    }
                    val blocks = buildBlocks(snap.exercises, snap.sets, nameCache, lastTimes)
                    ActiveSessionState(
                        loading = false,
                        session = snap.session,
                        blocks = blocks,
                        index = snap.active.index(blocks.size),
                        restEndsAt = snap.active.restEndsAt,
                        restTotalS = snap.active.restTotalS,
                        canUndo = undo.canUndo,
                        canRedo = undo.canRedo,
                    )
                }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), ActiveSessionState())

        fun select(index: Int) = launch { workouts.setPosition(index) }

        /** Confirms the set as it is and starts its rest. */
        fun confirm(set: SetEntity) =
            launch {
                val now = System.currentTimeMillis()
                val started = System.nanoTime()
                change(set, set.copy(completedAt = now, performedAt = now))
                if (BuildConfig.DEBUG) Log.d(PERF_TAG, "confirm_ms=" + (System.nanoTime() - started) / NANOS_PER_MS)
                rest.start(
                    set.restS ?: 0,
                    state.value.blocks
                        .firstOrNull { it.exercise.id == set.sessionExerciseId }
                        ?.name
                        .orEmpty(),
                )
            }

        /** Field edits while typing are saved but not kept in the undo history. */
        fun edit(
            set: SetEntity,
            transform: (SetEntity) -> SetEntity,
        ) = launch { editor.saveSet(transform(set)) }

        fun setType(
            set: SetEntity,
            type: String,
        ) = launch { change(set, set.copy(setType = type)) }

        fun remove(set: SetEntity) = launch { change(set, set.copy(deletedAt = System.currentTimeMillis())) }

        fun addSet(block: ExerciseBlock) = launch { editor.addSet(block.exercise.id, block.sets.lastOrNull()) }

        fun addExercise(exerciseId: String) =
            launch { state.value.session?.let { editor.addExercise(it.id, exerciseId) } }

        fun move(
            exerciseRowId: String,
            delta: Int,
        ) = launch { state.value.session?.let { editor.moveExercise(it.id, exerciseRowId, delta) } }

        /** Undo or redo the last set change; both work offline (10f). */
        fun history(redo: Boolean) =
            launch {
                (if (redo) undo.redo() else undo.undo())?.let { editor.saveSet(it) }
                history.value++
            }

        private suspend fun change(
            before: SetEntity,
            after: SetEntity,
        ) {
            editor.saveSet(after)
            undo.record(SetChange(before, after))
            history.value++
        }

        private fun launch(block: suspend () -> Unit) {
            viewModelScope.launch { block() }
        }

        private data class Snapshot(
            val active: com.abitusd.app.core.database.session.ActiveSessionEntity,
            val session: com.abitusd.app.core.database.session.WorkoutSessionEntity?,
            val exercises: List<com.abitusd.app.core.database.session.SessionExerciseEntity>,
            val sets: List<SetEntity>,
        )

        private companion object {
            const val STOP_MS = 5_000L
            const val PERF_TAG = "AbitusPerf"
            const val NANOS_PER_MS = 1_000_000L
        }
    }
