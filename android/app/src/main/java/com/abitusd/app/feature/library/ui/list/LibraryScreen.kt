package com.abitusd.app.feature.library.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.EmptyState
import com.abitusd.app.core.designsystem.component.InfoButton
import com.abitusd.app.core.designsystem.component.ScreenHeader
import com.abitusd.app.core.designsystem.component.SkeletonListItem
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.motion.staggeredEnter
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.workout.ui.WorkoutInfo

private const val SKELETON_ROWS = 6

@Composable
fun LibraryScreen(
    onOpenExercise: (String) -> Unit,
    onCreateExercise: () -> Unit,
    modifier: Modifier = Modifier,
    showCreate: Boolean = true,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    var showFilters by rememberSaveable { mutableStateOf(false) }
    val colors = AppTheme.colors
    Box(modifier.fillMaxSize().background(colors.background)) {
        Column(Modifier.fillMaxSize().statusBarsPadding(), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            ScreenHeader(
                stringResource(R.string.nav_library),
            ) { InfoButton(R.string.info_screen_library, WorkoutInfo.library) }
            LibrarySearchField(state.query, viewModel::onQuery)
            LibraryFilterBar(state, profile, { showFilters = true }, viewModel::onFilters, viewModel::onProfile)
            LibraryContent(state, onOpenExercise, viewModel::retryDownload, showCreate)
        }
        if (showCreate) {
            ExtendedFloatingActionButton(
                onClick = onCreateExercise,
                icon = { Icon(painterResource(AppIcons.Add), contentDescription = null) },
                text = { Text(stringResource(R.string.library_create), style = AppTheme.typography.label) },
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                modifier = Modifier.align(Alignment.BottomEnd).padding(AppSpacing.screenMargin),
            )
        }
    }
    if (showFilters) {
        LibraryFilterSheet(state, profile, viewModel::onFilters, viewModel::onProfile) { showFilters = false }
    }
}

@Composable
private fun LibraryContent(
    state: LibraryState,
    onOpenExercise: (String) -> Unit,
    onRetry: () -> Unit,
    hasFab: Boolean,
) {
    val bottom = if (hasFab) AppSpacing.fabClearance else AppSpacing.xl
    val padding =
        PaddingValues(
            start = AppSpacing.screenMargin,
            end = AppSpacing.screenMargin,
            top = AppSpacing.xxs,
            bottom = bottom,
        )
    when {
        state.loading -> {
            LazyColumn(
                contentPadding = padding,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                userScrollEnabled = false,
            ) {
                items(SKELETON_ROWS) { SkeletonListItem() }
            }
        }

        state.catalogEmpty && state.items.isEmpty() -> {
            EmptyState(
                AppIcons.Offline,
                stringResource(R.string.library_needs_connection),
                actionLabel = stringResource(R.string.library_retry),
                onAction = onRetry,
            )
        }

        state.items.isEmpty() -> {
            EmptyState(AppIcons.Search, stringResource(R.string.library_no_results))
        }

        else -> {
            LazyColumn(contentPadding = padding, verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                itemsIndexed(state.items, key = { _, row -> row.id }) { index, row ->
                    ExerciseItem(
                        row,
                        state,
                        onClick = { onOpenExercise(row.id) },
                        modifier = Modifier.staggeredEnter(index),
                    )
                }
            }
        }
    }
}
