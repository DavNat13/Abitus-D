package com.abitusd.app.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Large number with minus and plus buttons of 56 dp (05c, workout numeric fields). */
@Composable
fun Stepper(
    label: String,
    value: String,
    minusDescription: String,
    plusDescription: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    Column(
        modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xxs),
    ) {
        Text(label, style = AppTheme.typography.label, color = colors.textSecondary)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            StepButton(AppIcons.Remove, minusDescription, onMinus)
            Text(
                value,
                style = AppTheme.typography.headline,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            StepButton(AppIcons.Add, plusDescription, onPlus)
        }
    }
}

@Composable
private fun StepButton(
    icon: Int,
    description: String,
    onClick: () -> Unit,
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = Modifier.size(AppSizes.sessionButton),
        colors =
            IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = AppTheme.colors.primaryContainer,
                contentColor = AppTheme.colors.onPrimaryContainer,
            ),
    ) { Icon(painterResource(icon), description) }
}
