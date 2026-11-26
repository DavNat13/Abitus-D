package com.abitusd.app.feature.workout.ui.active

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.InfoPill
import com.abitusd.app.core.designsystem.component.ListItemCard
import com.abitusd.app.core.designsystem.component.MenuAction
import com.abitusd.app.core.designsystem.component.NumberField
import com.abitusd.app.core.designsystem.component.OverflowMenu
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.feature.workout.domain.PlateCalculator

/** Every exercise of the session with its progress: jump, reorder or add (10f). */
@Composable
fun SessionMapSheet(
    blocks: List<ExerciseBlock>,
    onJump: (Int) -> Unit,
    onMove: (String, Int) -> Unit,
    onAdd: () -> Unit,
    onDismiss: () -> Unit,
) {
    AppBottomSheet(stringResource(R.string.workout_map), onDismiss) {
        val up = stringResource(R.string.routine_move_up)
        val down = stringResource(R.string.routine_move_down)
        val more = stringResource(R.string.common_more_actions)
        blocks.forEachIndexed { index, block ->
            ListItemCard(
                title = block.name,
                onClick = { onJump(index) },
                icon = if (block.done == block.sets.size && block.sets.isNotEmpty()) AppIcons.Check else AppIcons.Train,
                meta = stringResource(R.string.workout_sets_done, block.done, block.sets.size),
            ) {
                val actions =
                    buildList {
                        if (index > 0) add(MenuAction(up, AppIcons.MoveUp) { onMove(block.exercise.id, -1) })
                        if (index <
                            blocks.lastIndex
                        ) {
                            add(MenuAction(down, AppIcons.MoveDown) { onMove(block.exercise.id, 1) })
                        }
                    }
                if (actions.isNotEmpty()) OverflowMenu(more, actions)
            }
        }
        AppButton(
            stringResource(R.string.workout_add_exercise),
            onAdd,
            style = AppButtonStyle.Tonal,
            icon = AppIcons.Add,
        )
    }
}

/** Plates per side for a total barbell weight. */
@Composable
fun PlateCalculatorSheet(
    initialKg: Double?,
    onDismiss: () -> Unit,
) {
    var target by remember { mutableStateOf(initialKg?.takeIf { it > 0.0 } ?: DEFAULT_TARGET) }
    var bar by remember { mutableStateOf(DEFAULT_BAR) }
    AppBottomSheet(stringResource(R.string.plate_calculator), onDismiss) {
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
            NumberField(target, stringResource(R.string.plate_target), { v ->
                v.toDoubleOrNull()?.let {
                    target = it
                }
            }, Modifier.weight(1f), decimal = true)
            NumberField(bar, stringResource(R.string.plate_bar), { v ->
                v.toDoubleOrNull()?.let {
                    bar = it
                }
            }, Modifier.weight(1f), decimal = true)
        }
        val result = PlateCalculator.perSide(target, bar)
        val perSide = stringResource(R.string.plate_per_side, result.perSide.joinToString(" + ") { formatKg(it) })
        PlateBar(result.perSide, perSide)
        InfoPill(perSide, emphasized = true)
        if (!result.exact(target)) InfoPill(stringResource(R.string.plate_not_exact, formatKg(result.reachedKg)))
    }
}

private const val DEFAULT_TARGET = 60.0
private const val DEFAULT_BAR = 20.0
