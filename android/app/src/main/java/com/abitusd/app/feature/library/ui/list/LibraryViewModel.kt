package com.abitusd.app.feature.library.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.core.database.catalog.ExerciseListRow
import com.abitusd.app.core.database.catalog.LibraryDao
import com.abitusd.app.core.database.catalog.NamedRef
import com.abitusd.app.core.i18n.AppLocale
import com.abitusd.app.core.i18n.SearchText
import com.abitusd.app.core.sync.SyncScheduler
import com.abitusd.app.feature.library.data.FavoritesRepository
import com.abitusd.app.feature.library.domain.EnglishNamesStore
import com.abitusd.app.feature.library.domain.EquipmentProfile
import com.abitusd.app.feature.library.domain.EquipmentProfileStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class LibraryFilters(
    val disciplineId: Int? = null,
    val muscleId: Int? = null,
    val equipmentId: Int? = null,
    val difficulty: Int? = null,
    val favoritesOnly: Boolean = false,
) {
    val activeCount: Int
        get() = listOfNotNull(disciplineId, muscleId, equipmentId, difficulty).size + if (favoritesOnly) 1 else 0
}

data class LibraryState(
    val query: String = "",
    val filters: LibraryFilters = LibraryFilters(),
    val items: List<ExerciseListRow> = emptyList(),
    val disciplines: List<NamedRef> = emptyList(),
    val muscles: List<NamedRef> = emptyList(),
    val equipment: List<NamedRef> = emptyList(),
    val catalogEmpty: Boolean = false,
    val icons: Map<Int, String> = emptyMap(),
    val loading: Boolean = true,
)

private const val STOP_TIMEOUT_MS = 5000L

private data class ItemsQuery(
    val query: String,
    val filters: LibraryFilters,
    val profile: EquipmentProfile,
    val english: Set<Int>,
)

/** Library list: reads only Room, so it works offline after the first download. */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LibraryViewModel
    @Inject
    constructor(
        private val dao: LibraryDao,
        private val syncScheduler: SyncScheduler,
        private val profiles: EquipmentProfileStore,
        private val englishNames: EnglishNamesStore,
        private val favorites: FavoritesRepository,
    ) : ViewModel() {
        val profile = profiles.profile

        fun onProfile(value: EquipmentProfile) = profiles.set(value)

        private val locale = AppLocale.current()
        private val query = MutableStateFlow("")
        private val filters = MutableStateFlow(LibraryFilters())

        private val items =
            combine(
                query,
                filters,
                profiles.profile,
                englishNames.disciplines,
            ) { q, f, p, en -> ItemsQuery(q, f, p, en) }.flatMapLatest { iq ->
                val f = iq.filters
                val p = iq.profile
                val mode =
                    if (p.allowed == null) {
                        0
                    } else if (p.excludes) {
                        1
                    } else {
                        2
                    }
                combine(
                    dao.exercises(
                        locale,
                        SearchText.normalize(iq.query),
                        f.disciplineId,
                        f.muscleId,
                        f.equipmentId,
                        f.difficulty,
                        mode,
                        p.allowed.orEmpty().toList(),
                        iq.english.toList(),
                    ),
                    favorites.ids,
                ) { list, fav -> FavoritesRepository.arrange(list, fav, f.favoritesOnly) { it.id } }
            }

        private val references =
            combine(
                dao.disciplines(locale),
                dao.muscleGroups(locale),
                dao.equipment(locale),
                dao.catalogCount(),
                dao.disciplineIcons(),
            ) {
                d,
                m,
                e,
                count,
                icons,
                ->
                LibraryState(
                    disciplines = d,
                    muscles = m,
                    equipment = e,
                    catalogEmpty = count == 0,
                    icons =
                        icons.associate {
                            it.id to
                                it.iconKey
                        },
                )
            }

        val state: StateFlow<LibraryState> =
            combine(query, filters, items, references) { q, f, list, refs ->
                refs.copy(query = q, filters = f, items = list, loading = false)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), LibraryState())

        fun onQuery(value: String) {
            query.value = value
        }

        fun onFilters(transform: (LibraryFilters) -> LibraryFilters) = filters.update(transform)

        fun retryDownload() = syncScheduler.requestSync()
    }
