package com.abitusd.app.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppTheme

/** The single chip of 05d: soft at rest; selected with primaryContainer, primary border and a check. */
@Composable
fun AppChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes leadingIcon: Int? = null,
    @DrawableRes trailingIcon: Int? = null,
) {
    val colors = AppTheme.colors
    val leading = if (selected && leadingIcon == null) AppIcons.Check else leadingIcon
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = AppTheme.typography.label) },
        leadingIcon = leading?.let { { Icon(painterResource(it), contentDescription = null) } },
        trailingIcon = trailingIcon?.let { { Icon(painterResource(it), contentDescription = null) } },
        shape = AppShapes.pill,
        colors =
            FilterChipDefaults.filterChipColors(
                containerColor = colors.surfaceSoft,
                labelColor = colors.textPrimary,
                iconColor = colors.textSecondary,
                selectedContainerColor = colors.primaryContainer,
                selectedLabelColor = colors.onPrimaryContainer,
                selectedLeadingIconColor = colors.onPrimaryContainer,
                selectedTrailingIconColor = colors.onPrimaryContainer,
            ),
        border =
            FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selected,
                borderColor = colors.outline,
                selectedBorderColor = colors.primary,
                borderWidth = AppSizes.borderThin,
                selectedBorderWidth = AppSizes.borderThin,
            ),
        modifier = modifier,
    )
}
