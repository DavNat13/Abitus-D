package com.abitusd.app.feature.progress.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.component.Stepper
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.feature.progress.domain.AnalysisSettings
import com.abitusd.app.feature.progress.domain.OneRmFormula
import java.util.Locale

/** Analysis settings (moves to Settings in Phase 9); every view recalculates when they change. */
@Composable
fun AnalysisSettingsCard(
    s: AnalysisSettings,
    onChange: ((AnalysisSettings) -> AnalysisSettings) -> Unit,
) {
    val less = stringResource(R.string.clock_less)
    val more = stringResource(R.string.clock_more)
    SectionCard(stringResource(R.string.progress_settings_title), icon = AppIcons.Filter) {
        Hint(R.string.progress_settings_note)
        val formulas =
            listOf(
                ChoiceOption(OneRmFormula.EPLEY, stringResource(R.string.formula_epley)),
                ChoiceOption(OneRmFormula.BRZYCKI, stringResource(R.string.formula_brzycki)),
            )
        AppDropdown(stringResource(R.string.progress_formula), formulas, s.formula, { f ->
            f?.let {
                onChange { c ->
                    c.copy(formula = it)
                }
            }
        })
        Stepper(stringResource(R.string.progress_window), s.windowWeeks.toString(), less, more, {
            onChange { it.copy(windowWeeks = maxOf(MIN_WEEKS, it.windowWeeks - 1)) }
        }, { onChange { it.copy(windowWeeks = minOf(MAX_WEEKS, it.windowWeeks + 1)) } })
        Stepper(stringResource(R.string.progress_threshold), pct(s.upPct), less, more, {
            onChange { it.copy(upPct = maxOf(STEP, it.upPct - STEP), downPct = -maxOf(STEP, it.upPct - STEP)) }
        }, {
            onChange {
                it.copy(
                    upPct = minOf(MAX_PCT, it.upPct + STEP),
                    downPct = -minOf(MAX_PCT, it.upPct + STEP),
                )
            }
        })
        Stepper(stringResource(R.string.progress_sets_min), s.minWeeklySets.toString(), less, more, {
            onChange { it.copy(minWeeklySets = maxOf(1, it.minWeeklySets - 1)) }
        }, { onChange { it.copy(minWeeklySets = minOf(it.maxWeeklySets - 1, it.minWeeklySets + 1)) } })
        Stepper(stringResource(R.string.progress_sets_max), s.maxWeeklySets.toString(), less, more, {
            onChange { it.copy(maxWeeklySets = maxOf(it.minWeeklySets + 1, it.maxWeeklySets - 1)) }
        }, { onChange { it.copy(maxWeeklySets = minOf(MAX_SETS, it.maxWeeklySets + 1)) } })
    }
}

private fun pct(v: Double) = String.format(Locale.getDefault(), "%.1f %%", v)

private const val MIN_WEEKS = 3
private const val MAX_WEEKS = 26
private const val STEP = 0.5
private const val MAX_PCT = 10.0
private const val MAX_SETS = 40
