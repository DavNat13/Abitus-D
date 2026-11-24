package com.abitusd.app.feature.workout.ui.preview

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Unfinished session found before starting: continue it, save it or discard it. */
@Composable
fun PreviousSessionSheet(
    title: String,
    onResolve: (Boolean?) -> Unit,
    onDismiss: () -> Unit,
) {
    AppBottomSheet(stringResource(R.string.workout_resume_title), onDismiss) {
        Text(
            stringResource(R.string.previous_text, title),
            style = AppTheme.typography.body,
            color = AppTheme.colors.textSecondary,
        )
        AppButton(stringResource(R.string.previous_continue), { onResolve(null) }, icon = AppIcons.Start)
        AppButton(stringResource(R.string.previous_save), {
            onResolve(true)
        }, style = AppButtonStyle.Tonal, icon = AppIcons.Check)
        AppButton(stringResource(R.string.previous_discard), {
            onResolve(false)
        }, style = AppButtonStyle.Destructive, icon = AppIcons.Delete)
    }
}
