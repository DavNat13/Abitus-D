package com.abitusd.app.feature.library.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.feature.library.data.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel
    @Inject
    constructor(
        private val favorites: FavoritesRepository,
    ) : ViewModel() {
        val ids = favorites.ids.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), emptySet())

        fun toggle(exerciseId: String) {
            viewModelScope.launch { favorites.toggle(exerciseId) }
        }

        private companion object {
            const val STOP_MS = 5_000L
        }
    }

@Composable
fun FavoriteButton(
    exerciseId: String,
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel(),
) {
    val ids by viewModel.ids.collectAsStateWithLifecycle()
    val on = exerciseId in ids
    AppButton(
        stringResource(if (on) R.string.favorite_remove else R.string.favorite_add),
        { viewModel.toggle(exerciseId) },
        modifier,
        AppButtonStyle.Tonal,
        icon = if (on) AppIcons.FavoriteOn else AppIcons.Favorite,
    )
}
