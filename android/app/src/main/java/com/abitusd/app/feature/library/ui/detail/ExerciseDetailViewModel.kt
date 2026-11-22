package com.abitusd.app.feature.library.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.core.database.catalog.ExerciseEntity
import com.abitusd.app.core.database.catalog.ExerciseTextEntity
import com.abitusd.app.core.database.catalog.LibraryDao
import com.abitusd.app.core.database.catalog.NamedRef
import com.abitusd.app.core.i18n.AppLocale
import com.abitusd.app.core.sync.SyncScheduler
import com.abitusd.app.feature.library.data.OwnExerciseRepository
import com.abitusd.app.feature.library.domain.EnglishNamesStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val STOP_TIMEOUT_MS = 5000L

/** Exercise detail. Texts follow the fallback chain: app locale, English, Spanish. */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = ExerciseDetailViewModel.Factory::class)
class ExerciseDetailViewModel
    @AssistedInject
    constructor(
        @Assisted private val id: String,
        dao: LibraryDao,
        private val ownExercises: OwnExerciseRepository,
        private val syncScheduler: SyncScheduler,
        private val englishNames: EnglishNamesStore,
    ) : ViewModel() {
        private val locale = AppLocale.current()
        private val closeChannel = Channel<Unit>(Channel.BUFFERED)
        val closed = closeChannel.receiveAsFlow()

        private val own =
            combine(dao.exercise(id), dao.texts(id), dao.muscles(id), dao.equipmentOf(id), dao.relations(id)) {
                e,
                t,
                m,
                q,
                r,
                ->
                Own(
                    e,
                    t,
                    m.map { it.muscleGroupId to it.role },
                    q.map { it.equipmentId },
                    r.map { it.relatedId to it.type },
                )
            }
        private val names =
            combine(
                dao.muscleGroups(locale),
                dao.equipment(locale),
                dao.disciplines(locale),
                englishNames.disciplines.flatMapLatest { en ->
                    dao.exercises(locale, "", null, null, null, null, englishDisciplines = en.toList())
                },
            ) {
                m,
                q,
                d,
                all,
                ->
                Names(
                    m.associate { it.id to it.name },
                    q.associate { it.id to it.name },
                    d,
                    all.associate {
                        it.id to
                            it.name
                    },
                )
            }

        val state: StateFlow<DetailState> =
            combine(own, names) { o, n ->
                val texts =
                    o.texts.firstOrNull { it.locale == locale } ?: o.texts.firstOrNull { it.locale == "en" }
                        ?: o.texts.firstOrNull()
                val englishName = o.texts.firstOrNull { it.locale == "en" }?.name
                val title =
                    if (o.exercise?.disciplineId in englishNames.disciplines.value &&
                        englishName != null
                    ) {
                        englishName
                    } else {
                        texts?.name
                    }

                fun related(type: String) =
                    o.relations
                        .filter {
                            it.second == type
                        }.mapNotNull { (rid, _) -> n.exercises[rid]?.let { RelatedExercise(rid, it) } }
                DetailState(
                    loading = false,
                    exercise = o.exercise?.takeIf { it.deletedAt == null },
                    texts = texts?.copy(name = title.orEmpty()),
                    discipline =
                        n.disciplines
                            .firstOrNull { it.id == o.exercise?.disciplineId }
                            ?.name
                            .orEmpty(),
                    primaryMuscles = o.muscles.filter { it.second == "primary" }.mapNotNull { n.muscles[it.first] },
                    secondaryMuscles = o.muscles.filter { it.second == "secondary" }.mapNotNull { n.muscles[it.first] },
                    equipment = o.equipment.mapNotNull { n.equipment[it] },
                    progressions = related("progression"),
                    regressions = related("regression"),
                    alternatives = related("alternative"),
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), DetailState())

        fun delete() {
            viewModelScope.launch {
                ownExercises.delete(id)
                syncScheduler.requestSync()
                closeChannel.send(Unit)
            }
        }

        @AssistedFactory
        interface Factory {
            fun create(id: String): ExerciseDetailViewModel
        }
    }
