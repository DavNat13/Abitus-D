package com.abitusd.app.feature.progress.ui.body

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppColors
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Body tab (09d): front and back maps by weekly sets per muscle, with exact values and a legend. */
@Composable
fun BodyTab(vm: BodyViewModel = hiltViewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    val periods = BodyPeriod.entries.map { ChoiceOption(it, stringResource(PERIOD_LABELS.getValue(it))) }
    AppDropdown(stringResource(R.string.body_period), periods, s.period, { it?.let(vm::setPeriod) })
    val colorOf = { key: String -> s.byKey[key]?.let { statusColor(status(it.perWeek, s.minSets, s.maxSets), colors) } }
    SectionCard(stringResource(R.string.body_map), icon = AppIcons.Muscles) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            BodyMap(
                true,
                colorOf,
                selected,
                stringResource(R.string.body_front),
                { selected = it },
                Modifier.weight(1f),
            )
            BodyMap(
                false,
                colorOf,
                selected,
                stringResource(R.string.body_back),
                { selected = it },
                Modifier.weight(1f),
            )
        }
        Legend(s.minSets, s.maxSets)
    }
    selected?.let { key ->
        val m = s.byKey[key]
        SectionCard(m?.name ?: stringResource(R.string.body_no_sets), icon = AppIcons.Muscles) {
            Text(
                stringResource(R.string.body_detail, perWeek(m?.perWeek ?: 0.0), perWeek(m?.previousPerWeek ?: 0.0)),
                style = AppTheme.typography.body,
            )
        }
    }
    SectionCard(stringResource(R.string.body_list), icon = AppIcons.Steps) {
        if (s.byKey.isEmpty()) {
            Text(
                stringResource(R.string.body_empty),
                color = colors.textSecondary,
                style = AppTheme.typography.body,
            )
        }
        s.byKey.entries.sortedByDescending { it.value.perWeek }.forEach { (_, m) ->
            val label =
                stringResource(
                    STATUS_LABELS.first { it.first == status(m.perWeek, s.minSets, s.maxSets) }.second,
                    if (m.perWeek <
                        s.minSets
                    ) {
                        s.minSets
                    } else {
                        s.maxSets
                    },
                )
            Text(stringResource(R.string.body_row, m.name, perWeek(m.perWeek), label), style = AppTheme.typography.body)
        }
    }
}

@Composable
private fun perWeek(v: Double) = String.format(LocalConfiguration.current.locales[0], "%.1f", v)

internal const val LOW = 0
private const val IN_RANGE = 1
private const val HIGH = 2

private fun status(
    perWeek: Double,
    min: Int,
    max: Int,
) = when {
    perWeek < min -> LOW
    perWeek > max -> HIGH
    else -> IN_RANGE
}

internal fun statusColor(
    status: Int,
    c: AppColors,
): Color =
    when (status) {
        LOW -> c.warning
        HIGH -> c.primary
        else -> c.success
    }

internal val STATUS_LABELS =
    listOf(
        LOW to R.string.body_low,
        IN_RANGE to R.string.body_in_range,
        HIGH to R.string.body_high,
    )

private val PERIOD_LABELS =
    mapOf(
        BodyPeriod.WEEK to R.string.body_week,
        BodyPeriod.MONTH to R.string.body_month,
        BodyPeriod.YEAR to R.string.body_year,
    )
