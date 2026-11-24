package com.abitusd.app.feature.workout.ui.active

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.database.session.SetEntity
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.InfoPill
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** One exercise of the session: clean header, then one strip per set; tap a strip to edit it. */
@Composable
fun ExercisePage(
    block: ExerciseBlock,
    position: Int,
    total: Int,
    viewModel: ActiveSessionViewModel,
    onPlates: () -> Unit,
) {
    var editing by remember { mutableStateOf<String?>(null) }
    val colors = AppTheme.colors
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState(),
            ).padding(horizontal = AppSpacing.screenMargin)
            .padding(bottom = AppSpacing.fabClearance),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        Text(
            stringResource(R.string.workout_exercise_of, position + 1, total),
            style = AppTheme.typography.label,
            color = colors.primary,
        )
        Text(block.name, style = AppTheme.typography.headline, color = colors.textPrimary)
        InfoPill(lastTimeText(block.lastTime), emphasized = block.lastTime.isNotEmpty())
        Row(Modifier.padding(top = AppSpacing.xs), verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.workout_sets_title),
                style = AppTheme.typography.titleMedium,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            Text(
                stringResource(R.string.workout_sets_done, block.done, block.sets.size),
                style = AppTheme.typography.label,
                color = colors.textSecondary,
            )
        }
        block.sets.forEachIndexed { index, set ->
            SetRow(set, index + 1, block.lastTime.getOrNull(index), onOpen = {
                editing = set.id
            }, onConfirm = { viewModel.confirm(set) })
        }
        AppButton(stringResource(R.string.workout_add_set), {
            viewModel.addSet(block)
        }, style = AppButtonStyle.Tonal, icon = AppIcons.Add)
        ExerciseTools(block, viewModel.editor, onPlates)
    }
    val index = block.sets.indexOfFirst { it.id == editing }
    if (index >= 0) {
        val set = block.sets[index]
        SetEditorSheet(
            set = set,
            number = index + 1,
            onChange = { transform -> viewModel.edit(set, transform) },
            onType = { viewModel.setType(set, it) },
            onRemove = { viewModel.remove(set) },
            onDismiss = { editing = null },
        )
    }
}

@Composable
private fun lastTimeText(sets: List<SetEntity>): String {
    if (sets.isEmpty()) return stringResource(R.string.workout_last_none)
    return stringResource(R.string.workout_last_time, sets.map { setValue(it) }.joinToString(", "))
}

/** "80 kg x 8", "12 reps" or "45 s" depending on what the set holds. */
@Composable
fun setValue(set: SetEntity): String {
    val load = set.loadKg
    val reps = set.reps
    val seconds = set.durationS
    return when {
        load != null && reps != null -> stringResource(R.string.workout_set_value, formatKg(load), reps)
        reps != null -> stringResource(R.string.workout_set_reps, reps)
        seconds != null -> stringResource(R.string.workout_set_time, seconds)
        else -> ""
    }
}

fun formatKg(value: Double): String =
    if (value % 1.0 ==
        0.0
    ) {
        value.toLong().toString()
    } else {
        "%.2f".format(value).trimEnd('0').trimEnd('.', ',')
    }
