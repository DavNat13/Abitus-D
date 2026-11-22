package com.abitusd.app.feature.train.ui.routines

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.RoutineExerciseEntity
import com.abitusd.app.core.designsystem.component.NumberField
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Targets of one planned exercise: sets, rep range, load (kg), RPE or RIR, rest, superset. */
@Composable
fun PlannedExerciseCard(
    item: RoutineExerciseEntity,
    name: String,
    isFirst: Boolean,
    isLast: Boolean,
    onEdit: ((RoutineExerciseEntity) -> RoutineExerciseEntity) -> Unit,
    onMove: (Int) -> Unit,
    onRemove: () -> Unit,
) {
    val colors = AppTheme.colors
    Card(
        shape = AppShapes.card,
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(AppSizes.borderThin, colors.outline),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            Text(name, style = AppTheme.typography.titleMedium, color = colors.textPrimary)
            if (item.groupId.isNotBlank()) {
                Text(
                    stringResource(R.string.routine_superset, item.groupId),
                    style = AppTheme.typography.caption,
                    color = colors.primary,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                NumberField(item.targetSets, stringResource(R.string.routine_sets), { v ->
                    onEdit {
                        it.copy(targetSets = v.toIntOrNull())
                    }
                }, Modifier.weight(1f))
                NumberField(item.targetRepsMin, stringResource(R.string.routine_reps_min), { v ->
                    onEdit {
                        it.copy(targetRepsMin = v.toIntOrNull())
                    }
                }, Modifier.weight(1f))
                NumberField(item.targetRepsMax, stringResource(R.string.routine_reps_max), { v ->
                    onEdit {
                        it.copy(targetRepsMax = v.toIntOrNull())
                    }
                }, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                NumberField(
                    item.targetLoadKg,
                    stringResource(R.string.routine_load, stringResource(R.string.unit_kg)),
                    { v -> onEdit { it.copy(targetLoadKg = v.toDoubleOrNull()?.coerceAtMost(MAX_LOAD)) } },
                    Modifier.weight(1f),
                    decimal = true,
                )
                NumberField(item.targetRpe, stringResource(R.string.routine_rpe), { v ->
                    onEdit {
                        it.copy(targetRpe = v.toDoubleOrNull()?.coerceIn(MIN_RPE, MAX_RPE))
                    }
                }, Modifier.weight(1f), decimal = true)
                NumberField(item.targetRir, stringResource(R.string.routine_rir), { v ->
                    onEdit {
                        it.copy(targetRir = v.toIntOrNull()?.coerceAtMost(MAX_RIR))
                    }
                }, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                NumberField(item.restSeconds, stringResource(R.string.routine_rest), { v ->
                    onEdit {
                        it.copy(restSeconds = v.toIntOrNull()?.coerceAtMost(MAX_REST))
                    }
                }, Modifier.weight(1f))
                GroupField(item.groupId, { v -> onEdit { it.copy(groupId = v) } }, Modifier.weight(1f))
            }
            Row {
                if (!isFirst) TextButton(onClick = { onMove(-1) }) { Text(stringResource(R.string.routine_move_up)) }
                if (!isLast) TextButton(onClick = { onMove(1) }) { Text(stringResource(R.string.routine_move_down)) }
                TextButton(onClick = onRemove) { Text(stringResource(R.string.routine_remove), color = colors.error) }
            }
        }
    }
}

@Composable
private fun GroupField(
    value: String,
    onValue: (String) -> Unit,
    modifier: Modifier,
) {
    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = { onValue(it.filter(Char::isLetter).take(1).uppercase()) },
        label = { Text(stringResource(R.string.routine_group), style = AppTheme.typography.caption) },
        singleLine = true,
        shape = AppShapes.field,
        modifier = modifier,
    )
}

private const val MAX_LOAD = 2000.0
private const val MAX_RIR = 10
private const val MIN_RPE = 1.0
private const val MAX_RPE = 10.0
private const val MAX_REST = 3600
