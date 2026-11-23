package com.abitusd.app.feature.workout.ui.active

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.abitusd.app.R
import com.abitusd.app.core.database.session.SetEntity
import com.abitusd.app.core.database.session.SetType
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/**
 * One set as a clean strip: number, the value in large type, RPE and the previous time below,
 * and a 56 dp confirm button. Tapping the strip opens the editor sheet (no tiny fields).
 */
@Composable
fun SetRow(
    set: SetEntity,
    number: Int,
    previous: SetEntity?,
    onOpen: () -> Unit,
    onConfirm: () -> Unit,
) {
    val colors = AppTheme.colors
    val done = set.completedAt != null
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = AppSizes.listItemMin)
            .clip(AppShapes.card)
            .background(if (done) colors.successContainer else colors.surfaceSoft)
            .clickable(role = Role.Button, onClick = onOpen)
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.xs),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(
                    AppSizes.headerBadge,
                ).background(if (done) colors.success else colors.surface, AppShapes.pill),
            contentAlignment = Alignment.Center,
        ) {
            val label =
                if (set.setType ==
                    SetType.NORMAL
                ) {
                    number.toString()
                } else {
                    stringResource(typeLabel(set.setType)).take(1)
                }
            Text(label, style = AppTheme.typography.label, color = if (done) colors.onPrimary else colors.textPrimary)
        }
        Column(Modifier.weight(1f)) {
            Text(mainValue(set), style = AppTheme.typography.titleMedium, color = colors.textPrimary)
            val details =
                listOfNotNull(
                    set.setType.takeIf { it != SetType.NORMAL }?.let { stringResource(typeLabel(it)) },
                    set.rpe?.let { stringResource(R.string.workout_rpe_value, formatKg(it)) },
                    previous?.let { stringResource(R.string.workout_previous_value, setValue(it)) },
                )
            if (details.isNotEmpty()) {
                Text(
                    details.joinToString("  ·  "),
                    style = AppTheme.typography.caption,
                    color = colors.textSecondary,
                )
            }
        }
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
                border = BorderStroke(AppSizes.borderButton, colors.primary),
            ) { Icon(painterResource(AppIcons.Check), description, tint = colors.primary) }
        }
    }
}

@Composable
private fun mainValue(set: SetEntity): String {
    val load = set.loadKg
    val reps = set.reps ?: 0
    return if (load != null && load > 0.0) {
        stringResource(R.string.workout_set_value, formatKg(load), reps)
    } else {
        stringResource(R.string.workout_set_reps, reps)
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
