package com.abitusd.app.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.EmptyState
import com.abitusd.app.core.designsystem.component.HeroCard
import com.abitusd.app.core.designsystem.component.InfoButton
import com.abitusd.app.core.designsystem.component.InfoPill
import com.abitusd.app.core.designsystem.component.ListItemCard
import com.abitusd.app.core.designsystem.component.ScreenHeader
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.core.designsystem.theme.LightAppColors
import com.abitusd.app.feature.workout.ui.WorkoutInfo
import java.time.format.DateTimeFormatter

/** Home: today's workout first, then the next one and this week's progress (05-UI-UX.md). */
@Composable
fun HomeScreen(
    onOpenProfile: () -> Unit,
    onGoTrain: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val s by viewModel.state.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0]
    val dayFormat = DateTimeFormatter.ofPattern("EEEE d MMMM", locale)
    Column(
        modifier
            .fillMaxSize()
            .background(
                AppTheme.colors.background,
            ).statusBarsPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        ScreenHeader(stringResource(R.string.nav_home)) {
            InfoButton(R.string.info_screen_home, WorkoutInfo.home)
            IconButton(onClick = onOpenProfile) {
                Icon(
                    painterResource(AppIcons.Profile),
                    stringResource(R.string.home_open_profile),
                    tint = AppTheme.colors.textPrimary,
                )
            }
        }
        Column(
            Modifier.padding(horizontal = AppSpacing.screenMargin),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            if (s.loading) return@Column
            val all = s.today.filterNot { it.activeRest }
            val training = all.filter { it.status != STATUS_DONE }
            if (training.isEmpty() && all.isNotEmpty()) {
                EmptyState(AppIcons.Check, stringResource(R.string.home_done_today, all.first().routineName))
            } else if (training.isNotEmpty()) {
                HeroCard(
                    stringResource(R.string.home_today),
                    training.first().routineName,
                    stringResource(R.string.home_go_train),
                    onGoTrain,
                ) {
                    if (training.size > 1) {
                        Text(
                            stringResource(R.string.home_more_today, training.size - 1),
                            style = AppTheme.typography.body,
                            color = LightAppColors.onPrimary,
                        )
                    }
                }
            } else if (s.today.any { it.activeRest }) {
                EmptyState(AppIcons.ActiveRest, stringResource(R.string.home_active_rest))
            } else {
                EmptyState(
                    AppIcons.Rest,
                    stringResource(R.string.home_rest_day),
                    actionLabel = stringResource(R.string.home_plan),
                    onAction = onGoTrain,
                )
            }
            s.next?.let { next ->
                val day = next.date.format(dayFormat).replaceFirstChar { it.titlecase(locale) }
                ListItemCard(
                    next.routineName,
                    onGoTrain,
                    icon = AppIcons.Calendar,
                    meta = stringResource(R.string.home_next, day),
                )
            }
            SectionCard(stringResource(R.string.home_week), icon = AppIcons.Today) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
                ) {
                    InfoPill(stringResource(R.string.home_week_planned, s.weekPlanned), emphasized = true)
                    InfoPill(stringResource(R.string.home_week_done, s.weekDone))
                }
            }
        }
    }
}

private const val STATUS_DONE = "done"
