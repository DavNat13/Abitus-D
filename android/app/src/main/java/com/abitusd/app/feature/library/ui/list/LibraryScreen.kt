package com.abitusd.app.feature.library.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Library tab: search, filters and the list of catalog and own exercises (10b, Fase 2). */
@Composable
fun LibraryScreen(
    onOpenExercise: (String) -> Unit,
    onCreateExercise: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    Box(modifier.fillMaxSize().background(colors.background)) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            Text(
                text = stringResource(R.string.nav_library),
                style = AppTheme.typography.titleLarge,
                color = colors.textPrimary,
                modifier =
                    Modifier
                        .padding(horizontal = AppSpacing.screenMargin, vertical = AppSpacing.sm)
                        .semantics { heading() },
            )
            LibrarySearchField(state.query, viewModel::onQuery)
            LibraryFilterRow(state, viewModel::onFilters)
            when {
                state.catalogEmpty && state.items.isEmpty() -> {
                    CatalogMissing(viewModel::retryDownload)
                }

                state.items.isEmpty() -> {
                    EmptyMessage(stringResource(R.string.library_no_results))
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = AppSpacing.screenMargin),
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.library_count, state.items.size),
                                style = AppTheme.typography.caption,
                                color = colors.textSecondary,
                            )
                        }
                        items(state.items, key = { it.id }) { row ->
                            ExerciseRowCard(row, state.disciplines, onClick = { onOpenExercise(row.id) })
                        }
                        item { Box(Modifier.padding(bottom = AppSpacing.xxxl * 2)) }
                    }
                }
            }
        }
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

@Composable
private fun CatalogMissing(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(AppSpacing.screenMargin),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        EmptyMessage(stringResource(R.string.library_needs_connection))
        AppButton(stringResource(R.string.library_retry), onRetry, style = AppButtonStyle.Secondary)
    }
}

@Composable
private fun EmptyMessage(text: String) {
    Text(
        text = text,
        style = AppTheme.typography.body,
        color = AppTheme.colors.textSecondary,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(AppSpacing.xl),
    )
}
