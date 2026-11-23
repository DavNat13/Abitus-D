package com.abitusd.app.feature.workout.ui.active

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.database.session.SetEntity
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.InfoPill
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** One exercise of the session: target, last time and its sets. */
@Composable
fun ExercisePage(
    block: ExerciseBlock,
    position: Int,
    total: Int,
    viewModel: ActiveSessionViewModel,
    onPlates: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = AppSpacing.screenMargin),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        Text(
            stringResource(R.string.workout_exercise_of, position + 1, total),
            style = AppTheme.typography.label,
            color = AppTheme.colors.textSecondary,
        )
        Text(block.name, style = AppTheme.typography.titleLarge, color = AppTheme.colors.textPrimary)
        InfoPill(lastTimeText(block.lastTime), emphasized = block.lastTime.isNotEmpty())
        SectionCard(stringResource(R.string.workout_sets_done, block.done, block.sets.size), icon = AppIcons.Steps) {
            block.sets.forEachIndexed { index, set ->
                SetRow(
                    set = set,
                    number = index + 1,
                    onEdit = { transform -> viewModel.edit(set, transform) },
                    onConfirm = { viewModel.confirm(set) },
                    onType = { viewModel.setType(set, it) },
                    onRemove = { viewModel.remove(set) },
                )
            }
            AppButton(stringResource(R.string.workout_add_set), {
                viewModel.addSet(block)
            }, style = AppButtonStyle.Tonal, icon = AppIcons.Add)
        }
        AppButton(
            stringResource(R.string.plate_calculator),
            onPlates,
            style = AppButtonStyle.Text,
            icon = AppIcons.Calculator,
        )
    }
}

@Composable
private fun lastTimeText(sets: List<SetEntity>): String {
    if (sets.isEmpty()) return stringResource(R.string.workout_last_none)
    val parts = sets.map { setValue(it) }
    return stringResource(R.string.workout_last_time, parts.joinToString(", "))
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
