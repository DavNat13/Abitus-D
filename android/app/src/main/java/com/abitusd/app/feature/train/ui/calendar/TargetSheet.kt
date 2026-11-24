package com.abitusd.app.feature.train.ui.calendar

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.Stepper
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Minimum and maximum weekly sets for one muscle group; the minimum never passes the maximum. */
@Composable
internal fun TargetSheet(
    muscle: String,
    current: IntRange?,
    onDismiss: () -> Unit,
    onSave: (IntRange?) -> Unit,
) {
    var min by remember { mutableIntStateOf(current?.first ?: DEFAULT_MIN) }
    var max by remember { mutableIntStateOf(current?.last ?: DEFAULT_MAX) }
    val minus = stringResource(R.string.workout_decrease)
    val plus = stringResource(R.string.workout_increase)
    AppBottomSheet(muscle, onDismiss) {
        Text(
            stringResource(R.string.targets_hint),
            style = AppTheme.typography.caption,
            color = AppTheme.colors.textSecondary,
        )
        Stepper(
            stringResource(R.string.targets_min),
            min.toString(),
            minus,
            plus,
            { min = (min - 1).coerceAtLeast(0) },
            { min = (min + 1).coerceAtMost(LIMIT).also { if (it > max) max = it } },
        )
        Stepper(
            stringResource(R.string.targets_max),
            max.toString(),
            minus,
            plus,
            { max = (max - 1).coerceAtLeast(0).also { if (it < min) min = it } },
            { max = (max + 1).coerceAtMost(LIMIT) },
        )
        AppButton(stringResource(R.string.targets_save), {
            onSave(min..max)
            onDismiss()
        }, icon = AppIcons.Check)
        if (current != null) {
            AppButton(stringResource(R.string.targets_remove), {
                onSave(null)
                onDismiss()
            }, style = AppButtonStyle.Text, icon = AppIcons.Close)
        }
    }
}

private const val DEFAULT_MIN = 10
private const val DEFAULT_MAX = 20
private const val LIMIT = 60
