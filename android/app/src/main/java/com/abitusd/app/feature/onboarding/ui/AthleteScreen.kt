package com.abitusd.app.feature.onboarding.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.component.InfoButton
import com.abitusd.app.core.designsystem.component.InfoEntry
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme

@Composable
fun AthleteScreen(
    onDone: () -> Unit,
    viewModel: AthleteViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.finished) { if (state.finished) onDone() }
    FormScreen(stringResource(R.string.athlete_title), info = {
        InfoButton(
            R.string.athlete_help,
            listOf(InfoEntry(AppIcons.Profile, R.string.athlete_title, R.string.athlete_help_body)) +
                com.abitusd.app.feature.train.ui.EditorInfo.athlete,
        )
    }) {
        Text(stringResource(R.string.athlete_intro), style = AppTheme.typography.body)
        val profile = state.profile
        if (profile != null) {
            Text(stringResource(R.string.athlete_step, state.step + 1), style = AppTheme.typography.label)
            SectionCard(stringResource(STEPS[state.step]), icon = AppIcons.Profile) {
                when (state.step) {
                    0 -> AthleteBody(profile, viewModel::change)
                    1 -> AthleteExperience(profile, viewModel::change)
                    2 -> AthleteGoals(profile, viewModel::change)
                    HEALTH_STEP -> AthleteHealth(profile, viewModel::change)
                    else -> AthleteBaseline(profile, viewModel::change)
                }
            }
            state.error?.let { Text(stringResource(it), color = AppTheme.colors.error) }
            AppButton(
                stringResource(
                    if (state.step ==
                        AthleteViewModel.LAST_STEP
                    ) {
                        R.string.athlete_finish
                    } else {
                        R.string.athlete_next
                    },
                ),
                { viewModel.next() },
                loading = state.busy,
            )
            AppButton(
                stringResource(R.string.athlete_skip_step),
                { viewModel.next(skip = true) },
                style = AppButtonStyle.Secondary,
            )
            AppButton(stringResource(R.string.athlete_skip_all), {
                viewModel.next(skip = true, finish = true)
            }, style = AppButtonStyle.Secondary)
        } else if (state.error != null) {
            Text(stringResource(R.string.athlete_save_error))
            AppButton(stringResource(R.string.athlete_next), viewModel::load)
        }
    }
}

private const val HEALTH_STEP = 3

private val STEPS =
    listOf(
        R.string.athlete_body,
        R.string.athlete_experience,
        R.string.athlete_goals,
        R.string.athlete_health,
        R.string.athlete_baseline,
    )
