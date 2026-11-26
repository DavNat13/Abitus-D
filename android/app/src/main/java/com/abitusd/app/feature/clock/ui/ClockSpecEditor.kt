package com.abitusd.app.feature.clock.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.Stepper
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.clock.domain.ClockPhase
import com.abitusd.app.feature.clock.domain.ClockSpec
import com.abitusd.app.feature.clock.domain.PhaseKind

/**
 * Phase durations and rounds. A custom sequence (11e) edits every phase and can add or remove
 * work, rest and warm-up phases; other modes edit their first two phases.
 */
@Composable
internal fun SpecEditor(
    spec: ClockSpec,
    countsDown: Boolean,
    custom: Boolean,
    onChange: (ClockSpec) -> Unit,
) {
    val editable = if (custom) spec.phases.size else minOf(EDITABLE, spec.phases.size)

    fun replace(phases: List<ClockPhase>) = onChange(spec.copy(phases = phases))
    spec.phases.take(editable).forEachIndexed { i, phase ->
        PhaseRow(i, phase, custom && spec.phases.size > 1, { p ->
            replace(
                spec.phases.mapIndexed { j, old ->
                    if (j ==
                        i
                    ) {
                        p
                    } else {
                        old
                    }
                },
            )
        }) {
            replace(spec.phases.filterIndexed { j, _ -> j != i })
        }
    }
    if (!custom && spec.phases.size > EDITABLE) {
        Text(
            stringResource(R.string.clock_sequence, spec.phases.size),
            style = AppTheme.typography.body,
            color = AppTheme.colors.textSecondary,
        )
    }
    if (custom && spec.phases.size < MAX_PHASES) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            for ((kind, seconds) in listOf(
                PhaseKind.WORK to WORK_S,
                PhaseKind.REST to REST_S,
                PhaseKind.PREPARE to REST_S,
            )) {
                AppButton(stringResource(ClockLabels.kind(kind)), {
                    replace(spec.phases + ClockPhase(kind, seconds))
                }, Modifier.weight(1f), AppButtonStyle.Tonal, icon = AppIcons.Add)
            }
        }
        AppButton(stringResource(R.string.clock_reps_phase), {
            replace(spec.phases + ClockPhase(PhaseKind.WORK, 1, reps = DEFAULT_REPS))
        }, style = AppButtonStyle.Tonal, icon = AppIcons.Add)
    }
    if (countsDown) {
        Stepper(
            stringResource(R.string.clock_rounds),
            spec.rounds.toString(),
            stringResource(R.string.clock_less),
            stringResource(R.string.clock_more),
            { onChange(spec.copy(rounds = maxOf(1, spec.rounds - 1))) },
            { onChange(spec.copy(rounds = minOf(MAX_ROUNDS, spec.rounds + 1))) },
        )
    }
}

private const val EDITABLE = 2
private const val MAX_REPS = 1000
private const val DEFAULT_REPS = 10
private const val MAX_PHASES = 50
private const val WORK_S = 30
private const val REST_S = 15
private const val MS = 1000L
private const val MIN_S = 5
private const val MAX_S = 86_400
private const val LONG_S = 120
private const val SHORT_STEP = 5
private const val LONG_STEP = 30
private const val MAX_ROUNDS = 100

@Composable
private fun PhaseRow(
    i: Int,
    phase: ClockPhase,
    removable: Boolean,
    onChange: (ClockPhase) -> Unit,
    onRemove: () -> Unit,
) {
    val reps = phase.reps
    if (reps != null) {
        Stepper(
            stringResource(R.string.clock_phase_n, i + 1, stringResource(R.string.clock_reps_phase)),
            reps.toString(),
            stringResource(R.string.clock_less),
            stringResource(R.string.clock_more),
            { onChange(phase.copy(reps = maxOf(1, reps - 1))) },
            { onChange(phase.copy(reps = minOf(MAX_REPS, reps + 1))) },
        )
    } else {
        val step = if (phase.durationS >= LONG_S) LONG_STEP else SHORT_STEP
        Stepper(
            stringResource(R.string.clock_phase_n, i + 1, stringResource(ClockLabels.kind(phase.kind))),
            ClockLabels.time(phase.durationS * MS),
            stringResource(R.string.clock_less),
            stringResource(R.string.clock_more),
            { onChange(phase.copy(durationS = maxOf(MIN_S, phase.durationS - step))) },
            { onChange(phase.copy(durationS = minOf(MAX_S, phase.durationS + step))) },
        )
    }
    if (removable) {
        AppButton(
            stringResource(R.string.clock_phase_remove, i + 1),
            onRemove,
            style = AppButtonStyle.Secondary,
            icon = AppIcons.Remove,
        )
    }
}
