package com.abitusd.app.feature.library.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.core.database.catalog.ExerciseListRow
import com.abitusd.app.core.database.catalog.LibraryDao
import com.abitusd.app.core.database.catalog.NamedRef
import com.abitusd.app.core.i18n.AppLocale
import com.abitusd.app.core.i18n.SearchText
import com.abitusd.app.core.sync.SyncScheduler
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
)

data class LibraryState(
    val query: String = "",
    val filters: LibraryFilters = LibraryFilters(),
    val items: List<ExerciseListRow> = emptyList(),
    val disciplines: List<NamedRef> = emptyList(),
    val muscles: List<NamedRef> = emptyList(),
    val equipment: List<NamedRef> = emptyList(),
    val catalogEmpty: Boolean = false,
)

private const val STOP_TIMEOUT_MS = 5000L

/** Library list: reads only Room, so it works offline after the first download. */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LibraryViewModel
    @Inject
    constructor(
        private val dao: LibraryDao,
        private val syncScheduler: SyncScheduler,
    ) : ViewModel() {
        private val locale = AppLocale.current()
        private val query = MutableStateFlow("")
        private val filters = MutableStateFlow(LibraryFilters())

        private val items =
            combine(query, filters) { q, f -> q to f }.flatMapLatest { (q, f) ->
                dao.exercises(locale, SearchText.normalize(q), f.disciplineId, f.muscleId, f.equipmentId, f.difficulty)
            }

        private val references =
            combine(dao.disciplines(locale), dao.muscleGroups(locale), dao.equipment(locale), dao.catalogCount()) {
                d,
                m,
                e,
                count,
                ->
                LibraryState(disciplines = d, muscles = m, equipment = e, catalogEmpty = count == 0)
            }

        val state: StateFlow<LibraryState> =
            combine(query, filters, items, references) { q, f, list, refs ->
                refs.copy(query = q, filters = f, items = list)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), LibraryState())

        fun onQuery(value: String) {
            query.value = value
        }

        fun onFilters(transform: (LibraryFilters) -> LibraryFilters) = filters.update(transform)

        fun retryDownload() = syncScheduler.requestSync()
    }
