package com.abitusd.app.feature.clock.ui

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppTextField
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Laps newest first with their split; the best and slowest split are labeled, not only colored. */
@Composable
fun LapList(
    marksMs: List<Long>,
    labels: Map<Int, String>,
    onLabel: (Int, String) -> Unit,
) {
    var naming by rememberSaveable { mutableStateOf<Int?>(null) }
    val splits = marksMs.mapIndexed { i, ms -> ms - (marksMs.getOrNull(i - 1) ?: 0L) }
    val best = splits.minOrNull()
    val worst = splits.maxOrNull()
    splits.indices.reversed().forEach { i ->
        val split = splits[i]
        val tag =
            when {
                splits.size < 2 -> ""
                split == best -> " - " + stringResource(R.string.clock_lap_best)
                split == worst -> " - " + stringResource(R.string.clock_lap_worst)
                else -> ""
            }
        val name = labels[i]?.let { " ($it)" }.orEmpty()
        Text(
            stringResource(R.string.clock_lap_row, i + 1, ClockLabels.time(split, tenths = true)) + name + tag,
            modifier = Modifier.clickable { naming = i },
            style = AppTheme.typography.body,
            color = if (tag.isEmpty()) AppTheme.colors.textPrimary else AppTheme.colors.primary,
        )
    }
    naming?.let { i ->
        var text by rememberSaveable(i) { mutableStateOf(labels[i].orEmpty()) }
        AppBottomSheet(stringResource(R.string.clock_lap_label, i + 1), { naming = null }) {
            AppTextField(text, { text = it.take(MAX_LABEL) }, stringResource(R.string.clock_lap_label, i + 1))
            AppButton(stringResource(R.string.clock_lap_save), {
                onLabel(i, text.trim())
                naming = null
            }, enabled = text.isNotBlank())
        }
    }
}

private const val MAX_LABEL = 30
