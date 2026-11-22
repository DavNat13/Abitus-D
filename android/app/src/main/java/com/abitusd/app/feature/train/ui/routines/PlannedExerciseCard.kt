package com.abitusd.app.feature.train.ui.routines

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.RoutineExerciseEntity
import com.abitusd.app.core.designsystem.component.AppCard
import com.abitusd.app.core.designsystem.component.MenuAction
import com.abitusd.app.core.designsystem.component.NumberField
import com.abitusd.app.core.designsystem.component.OverflowMenu
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** One planned exercise: header with order or superset letter and actions, then its targets. */
@Composable
fun PlannedExerciseCard(
    item: RoutineExerciseEntity,
    name: String,
    position: Int,
    isFirst: Boolean,
    isLast: Boolean,
    onEdit: ((RoutineExerciseEntity) -> RoutineExerciseEntity) -> Unit,
    onMove: (Int) -> Unit,
    onRemove: () -> Unit,
) {
    val colors = AppTheme.colors
    val up = stringResource(R.string.routine_move_up)
    val down = stringResource(R.string.routine_move_down)
    val remove = stringResource(R.string.routine_remove)
    val actions =
        buildList {
            if (!isFirst) add(MenuAction(up, AppIcons.MoveUp) { onMove(-1) })
            if (!isLast) add(MenuAction(down, AppIcons.MoveDown) { onMove(1) })
            add(MenuAction(remove, AppIcons.Delete, destructive = true, onClick = onRemove))
        }
    AppCard {
        Column(Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                val superset = item.groupId.isNotBlank()
                Box(
                    Modifier
                        .size(AppSizes.headerBadge)
                        .background(if (superset) colors.primary else colors.primaryContainer, AppShapes.pill),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (superset) item.groupId else position.toString(),
                        style = AppTheme.typography.label,
                        color = if (superset) colors.onPrimary else colors.onPrimaryContainer,
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(name, style = AppTheme.typography.titleMedium, color = colors.textPrimary)
                    if (superset) {
                        Text(
                            stringResource(R.string.routine_superset, item.groupId),
                            style = AppTheme.typography.caption,
                            color = colors.primary,
                        )
                    }
                }
                OverflowMenu(stringResource(R.string.common_more_actions), actions)
            }
            TargetsGrid(item, onEdit)
        }
    }
}

@Composable
private fun TargetsGrid(
    item: RoutineExerciseEntity,
    onEdit: ((RoutineExerciseEntity) -> RoutineExerciseEntity) -> Unit,
) {
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
        val kg = stringResource(R.string.routine_load, stringResource(R.string.unit_kg))
        NumberField(item.targetLoadKg, kg, { v ->
            onEdit {
                it.copy(targetLoadKg = v.toDoubleOrNull()?.coerceAtMost(MAX_LOAD))
            }
        }, Modifier.weight(1f), decimal = true)
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
        GroupField(item.groupId, { v: String -> onEdit { it.copy(groupId = v) } }, Modifier.weight(1f))
    }
}

private const val MAX_LOAD = 2000.0
private const val MAX_RIR = 10
private const val MIN_RPE = 1.0
private const val MAX_RPE = 10.0
private const val MAX_REST = 3600
