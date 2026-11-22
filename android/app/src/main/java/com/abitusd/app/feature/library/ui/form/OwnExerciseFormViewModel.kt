package com.abitusd.app.feature.library.ui.form

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.database.catalog.LibraryDao
import com.abitusd.app.core.database.catalog.NamedRef
import com.abitusd.app.core.i18n.AppLocale
import com.abitusd.app.core.sync.SyncScheduler
import com.abitusd.app.feature.library.data.OwnExerciseInput
import com.abitusd.app.feature.library.data.OwnExerciseRepository
import com.abitusd.app.feature.library.domain.DuplicateDetector
import com.abitusd.app.feature.library.domain.NamedExercise
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

private const val DEFAULT_REST_SECONDS = 90

/** Create or edit an own exercise. Saves to Room first; sync runs later (golden rule 12). */
@HiltViewModel(assistedFactory = OwnExerciseFormViewModel.Factory::class)
class OwnExerciseFormViewModel
    @AssistedInject
    constructor(
        @Assisted private val editId: String?,
        private val dao: LibraryDao,
        private val repository: OwnExerciseRepository,
        private val syncScheduler: SyncScheduler,
    ) : ViewModel() {
        private val locale = AppLocale.current()
        private val id = editId ?: UUID.randomUUID().toString()
        private val mutableState = MutableStateFlow(OwnExerciseFormState(isEdit = editId != null))
        val state: StateFlow<OwnExerciseFormState> = mutableState.asStateFlow()
        private val savedChannel = Channel<String>(Channel.BUFFERED)
        val saved = savedChannel.receiveAsFlow()
        private var candidates: List<NamedExercise> = emptyList()

        init {
            viewModelScope.launch {
                combine(
                    dao.disciplines(locale),
                    dao.muscleGroups(locale),
                    dao.equipment(locale),
                ) { d, m, q -> Triple(d, m, q) }
                    .collect { (d, m, q) ->
                        mutableState.update { it.copy(disciplines = d, muscles = m, equipmentOptions = q) }
                    }
            }
            viewModelScope.launch {
                candidates =
                    dao.exercises(locale, "", null, null, null, null).first().map { NamedExercise(it.id, it.name) }
                editId?.let { loadExisting(it) }
            }
        }

        fun update(transform: (OwnExerciseFormState) -> OwnExerciseFormState) {
            mutableState.update { current ->
                val next = transform(current).copy(error = null)
                if (next.name ==
                    current.name
                ) {
                    next
                } else {
                    next.copy(duplicates = DuplicateDetector.similar(next.name, candidates, id))
                }
            }
        }

        fun save() {
            val s = state.value
            val error =
                when {
                    s.name.isBlank() -> R.string.form_name_required
                    s.primary.isEmpty() -> R.string.form_muscle_required
                    else -> null
                }
            if (error != null || s.saving) {
                mutableState.update { it.copy(error = error) }
                return
            }
            mutableState.update { it.copy(saving = true) }
            viewModelScope.launch {
                repository.save(
                    OwnExerciseInput(
                        id,
                        s.name,
                        s.notes,
                        locale,
                        s.disciplineId,
                        s.metricType,
                        s.movementPattern,
                        s.difficulty,
                        s.primary,
                        s.secondary,
                        s.equipment,
                        DEFAULT_REST_SECONDS,
                    ),
                )
                syncScheduler.requestSync()
                savedChannel.send(id)
            }
        }

        private suspend fun loadExisting(exerciseId: String) {
            val rows = repository.load(exerciseId) ?: return
            mutableState.update {
                it.copy(
                    name = rows.text.name,
                    notes = rows.text.summary,
                    disciplineId = rows.exercise.disciplineId,
                    metricType = rows.exercise.metricType,
                    movementPattern = rows.exercise.movementPattern,
                    difficulty = rows.exercise.difficulty,
                    primary =
                        rows.muscles
                            .filter { m -> m.role == "primary" }
                            .map { m -> m.muscleGroupId }
                            .toSet(),
                    secondary =
                        rows.muscles
                            .filter { m -> m.role == "secondary" }
                            .map { m -> m.muscleGroupId }
                            .toSet(),
                    equipment = rows.equipment.map { q -> q.equipmentId }.toSet(),
                )
            }
        }

        @AssistedFactory
        interface Factory {
            fun create(editId: String?): OwnExerciseFormViewModel
        }
    }
