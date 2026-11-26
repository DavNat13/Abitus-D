package com.abitusd.app.feature.progress.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.ChoiceOption
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
    var settingsOpen by rememberSaveable { mutableStateOf(false) }
    if (settingsOpen) {
        AppBottomSheet(stringResource(R.string.progress_settings_title), { settingsOpen = false }) {
            AnalysisSettingsCard(s.settings, viewModel::settings)
        }
    }
    Column(
        modifier
            .fillMaxSize()
            .background(
                AppTheme.colors.background,
            ).statusBarsPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        ScreenHeader(stringResource(R.string.nav_progress)) {
            Row {
                IconButton(onClick = { settingsOpen = true }) {
                    Icon(painterResource(AppIcons.Filter), stringResource(R.string.progress_settings_title))
                }
                InfoButton(R.string.nav_progress, INFO)
            }
        }
        Column(
            Modifier.padding(horizontal = AppSpacing.screenMargin),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            val labels =
                listOf(
                    R.string.progress_tab_summary,
                    R.string.progress_tab_exercises,
                    R.string.progress_tab_body,
                    R.string.progress_tab_measures,
                )
            if (LocalDensity.current.fontScale > 1f) {
                AppDropdown(
                    stringResource(R.string.nav_progress),
                    labels.mapIndexed { index, label -> ChoiceOption(index, stringResource(label)) },
                    tab,
                    { it?.let { tab = it } },
                )
            } else {
                SegmentedTabs(labels.map { stringResource(it) }, tab, { tab = it })
            }
            when {
                s.loading -> {
                    Unit
                }

                tab == TAB_BODY -> {
                    com.abitusd.app.feature.progress.ui.body
                        .BodyTab()
                }

                tab == TAB_MEASURES -> {
                    MeasurementsTab(s)
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
        InfoEntry(AppIcons.Muscles, R.string.progress_tab_body, R.string.progress_info_body),
        InfoEntry(AppIcons.Weight, R.string.progress_tab_measures, R.string.progress_info_measures),
        InfoEntry(AppIcons.Filter, R.string.progress_tab_settings, R.string.progress_info_settings),
    )

private const val TAB_BODY = 2
private const val TAB_MEASURES = 3
