package com.abitusd.app.feature.progress.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.EmptyState
import com.abitusd.app.core.designsystem.component.InfoButton
import com.abitusd.app.core.designsystem.component.InfoEntry
import com.abitusd.app.core.designsystem.component.ScreenHeader
import com.abitusd.app.core.designsystem.component.SegmentedTabs
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Progress (09d): weekly summary, per exercise evolution with its verdict, and analysis settings. */
@Composable
fun ProgressScreen(
    modifier: Modifier = Modifier,
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val s by viewModel.state.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableIntStateOf(0) }
    Column(
        modifier
            .fillMaxSize()
            .background(
                AppTheme.colors.background,
            ).statusBarsPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        ScreenHeader(stringResource(R.string.nav_progress)) { InfoButton(R.string.nav_progress, INFO) }
        Column(
            Modifier.padding(horizontal = AppSpacing.screenMargin),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            val labels =
                listOf(R.string.progress_tab_summary, R.string.progress_tab_exercises, R.string.progress_tab_settings)
            SegmentedTabs(labels.map { stringResource(it) }, tab, { tab = it })
            when {
                s.loading -> {
                    Unit
                }

                tab == 2 -> {
                    AnalysisSettingsCard(s.settings, viewModel::settings)
                }

                s.trained.isEmpty() -> {
                    EmptyState(AppIcons.Progress, stringResource(R.string.progress_empty_body))
                }

                tab == 0 -> {
                    ProgressSummary(s, viewModel::isStagnant) { id ->
                        viewModel.select(id)
                        tab = 1
                    }
                }

                else -> {
                    ProgressExercise(s, viewModel::select)
                }
            }
        }
    }
}

private val INFO =
    listOf(
        InfoEntry(AppIcons.Today, R.string.progress_tab_summary, R.string.progress_info_summary),
        InfoEntry(AppIcons.Progress, R.string.progress_tab_exercises, R.string.progress_info_exercises),
        InfoEntry(AppIcons.Filter, R.string.progress_tab_settings, R.string.progress_info_settings),
    )
