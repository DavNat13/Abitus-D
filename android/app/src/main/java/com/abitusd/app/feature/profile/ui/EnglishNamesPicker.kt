package com.abitusd.app.feature.profile.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.database.catalog.LibraryDao
import com.abitusd.app.core.designsystem.component.AppMultiDropdown
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.core.i18n.AppLocale
import com.abitusd.app.feature.library.domain.EnglishNamesStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EnglishNamesViewModel
    @Inject
    constructor(
        dao: LibraryDao,
        private val store: EnglishNamesStore,
    ) : ViewModel() {
        val disciplines =
            dao
                .disciplines(
                    AppLocale.current(),
                ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), emptyList())
        val selected = store.disciplines

        fun toggle(id: Int) = store.toggle(id)

        private companion object {
            const val STOP_MS = 5_000L
        }
    }

/** Disciplines whose exercise names stay in English (for example calisthenics). */
@Composable
fun EnglishNamesPicker(viewModel: EnglishNamesViewModel = hiltViewModel()) {
    val disciplines by viewModel.disciplines.collectAsStateWithLifecycle()
    val selected by viewModel.selected.collectAsStateWithLifecycle()
    AppMultiDropdown(
        stringResource(R.string.profile_english_names),
        disciplines.map { ChoiceOption(it.id, it.name) },
        selected,
        viewModel::toggle,
    )
    Text(
        stringResource(R.string.profile_english_names_hint),
        style = AppTheme.typography.caption,
        color = AppTheme.colors.textSecondary,
    )
}
