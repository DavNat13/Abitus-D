package com.abitusd.app.feature.clock.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.feature.clock.domain.ClockEngine
import com.abitusd.app.feature.clock.domain.ClockMode
import com.abitusd.app.feature.clock.domain.ClockRun
import com.abitusd.app.feature.clock.domain.ClockStatus

/** Pause or resume, finish, and +15 s and skip (count down) or lap (count up). */
@Composable
internal fun ClockControls(
    run: ClockRun,
    status: ClockStatus,
    countsUp: Boolean,
    vm: ClockViewModel,
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        val m = Modifier.weight(1f)
        if (!status.done) {
            if (run.pausedAt == null) {
                AppButton(
                    stringResource(R.string.clock_pause),
                    { vm.act(ClockEngine::pause) },
                    m,
                    icon = AppIcons.Pause,
                )
            } else {
                AppButton(
                    stringResource(R.string.clock_resume),
                    { vm.act(ClockEngine::resume) },
                    m,
                    icon = AppIcons.Start,
                )
            }
        }
        AppButton(stringResource(R.string.clock_stop), vm::stop, m, AppButtonStyle.Secondary, icon = AppIcons.Reset)
    }
    if (status.done) return
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
        val m = Modifier.weight(1f)
        if (countsUp) {
            AppButton(stringResource(R.string.clock_lap), {
                vm.act(ClockEngine::lap)
            }, m, AppButtonStyle.Tonal, icon = AppIcons.Lap)
        } else {
            AppButton(stringResource(R.string.clock_add_15), {
                vm.act {
                    r,
                    now,
                    ->
                    ClockEngine.addTime(r, now, EXTRA_S)
                }
            }, m, AppButtonStyle.Tonal, icon = AppIcons.Add)
            AppButton(stringResource(R.string.clock_skip), {
                vm.act(ClockEngine::skip)
            }, m, AppButtonStyle.Tonal, icon = AppIcons.Skip)
        }
    }
    if (run.mode == ClockMode.AMRAP.key) {
        AppButton(stringResource(R.string.clock_round_done, run.count), {
            vm.act(ClockEngine::count)
        }, style = AppButtonStyle.Tonal, icon = AppIcons.Add)
    }
    ClockKeysEffect(run, countsUp, vm)
}

private const val EXTRA_S = 15
