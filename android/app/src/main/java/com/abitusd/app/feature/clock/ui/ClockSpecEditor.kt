package com.abitusd.app.feature.clock.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.Stepper
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.clock.domain.ClockSpec

/** Durations of the first phases and the rounds; longer sequences are shown, not edited. */
@Composable
internal fun SpecEditor(
    spec: ClockSpec,
    countsDown: Boolean,
    onChange: (ClockSpec) -> Unit,
) {
    spec.phases.take(EDITABLE).forEachIndexed { i, phase ->
        val step = if (phase.durationS >= LONG_S) LONG_STEP else SHORT_STEP

        fun set(value: Int) =
            onChange(
                spec.copy(
                    phases =
                        spec.phases.mapIndexed { j, p ->
                            if (j ==
                                i
                            ) {
                                p.copy(durationS = value)
                            } else {
                                p
                            }
                        },
                ),
            )
        Stepper(
            stringResource(ClockLabels.kind(phase.kind)),
            ClockLabels.time(phase.durationS * MS),
            stringResource(R.string.clock_less),
            stringResource(R.string.clock_more),
            { set(maxOf(MIN_S, phase.durationS - step)) },
            { set(minOf(MAX_S, phase.durationS + step)) },
        )
    }
    if (spec.phases.size > EDITABLE) {
        Text(
            stringResource(R.string.clock_sequence, spec.phases.size),
            style = AppTheme.typography.body,
            color = AppTheme.colors.textSecondary,
        )
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
private const val MS = 1000L
private const val MIN_S = 5
private const val MAX_S = 86_400
private const val LONG_S = 120
private const val SHORT_STEP = 5
private const val LONG_STEP = 30
private const val MAX_ROUNDS = 100
