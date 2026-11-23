package com.abitusd.app.feature.workout.ui.active

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.database.session.SetEntity
import com.abitusd.app.core.database.session.SetType
import com.abitusd.app.core.designsystem.component.MenuAction
import com.abitusd.app.core.designsystem.component.NumberField
import com.abitusd.app.core.designsystem.component.OverflowMenu
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** One set: number, load, reps, RPE and a large confirm button (56 dp during a session). */
@Composable
fun SetRow(
    set: SetEntity,
    number: Int,
    onEdit: ((SetEntity) -> SetEntity) -> Unit,
    onConfirm: () -> Unit,
    onType: (String) -> Unit,
    onRemove: () -> Unit,
) {
    val colors = AppTheme.colors
    val done = set.completedAt != null
    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(AppSizes.headerBadge), contentAlignment = Alignment.Center) {
            val label =
                if (set.setType ==
                    SetType.NORMAL
                ) {
                    number.toString()
                } else {
                    stringResource(typeLabel(set.setType)).take(1)
                }
            Text(
                label,
                style = AppTheme.typography.label,
                color = if (done) colors.successText else colors.textSecondary,
            )
        }
        NumberField(set.loadKg, stringResource(R.string.unit_kg), { v ->
            onEdit {
                it.copy(loadKg = v.toDoubleOrNull()?.coerceIn(0.0, MAX_LOAD))
            }
        }, Modifier.weight(1f), decimal = true)
        NumberField(set.reps, stringResource(R.string.workout_reps), { v ->
            onEdit {
                it.copy(reps = v.toIntOrNull()?.coerceAtMost(MAX_REPS))
            }
        }, Modifier.weight(1f))
        NumberField(set.rpe, stringResource(R.string.routine_rpe), { v ->
            onEdit {
                it.copy(rpe = v.toDoubleOrNull()?.coerceIn(MIN_RPE, MAX_RPE))
            }
        }, Modifier.weight(1f), decimal = true)
        val description = stringResource(R.string.workout_confirm_set, number)
        if (done) {
            FilledIconButton(
                onClick = onConfirm,
                modifier = Modifier.size(AppSizes.sessionButton),
                colors =
                    IconButtonDefaults.filledIconButtonColors(
                        containerColor = colors.success,
                        contentColor = colors.onPrimary,
                    ),
            ) { Icon(painterResource(AppIcons.Check), description) }
        } else {
            OutlinedIconButton(
                onClick = onConfirm,
                modifier = Modifier.size(AppSizes.sessionButton),
                border = BorderStroke(AppSizes.borderButton, colors.outlineStrong),
            ) { Icon(painterResource(AppIcons.Check), description, tint = colors.textSecondary) }
        }
        val types =
            SetType.ALL.map { type ->
                MenuAction(stringResource(typeLabel(type)), AppIcons.Steps) { onType(type) }
            }
        OverflowMenu(
            stringResource(R.string.workout_set_type),
            types +
                MenuAction(
                    stringResource(R.string.workout_remove_set),
                    AppIcons.Delete,
                    destructive = true,
                    onClick = onRemove,
                ),
        )
    }
}

fun typeLabel(type: String) =
    when (type) {
        SetType.WARMUP -> R.string.set_type_warmup
        SetType.DROP -> R.string.set_type_drop
        SetType.AMRAP -> R.string.set_type_amrap
        SetType.FAILURE -> R.string.set_type_failure
        SetType.REST_PAUSE -> R.string.set_type_rest_pause
        else -> R.string.set_type_normal
    }

private const val MAX_LOAD = 2000.0
private const val MAX_REPS = 1000
private const val MIN_RPE = 1.0
private const val MAX_RPE = 10.0
