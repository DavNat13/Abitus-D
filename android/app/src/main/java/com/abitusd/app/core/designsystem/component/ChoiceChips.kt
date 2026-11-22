package com.abitusd.app.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** One option of a chip selector. */
data class ChoiceOption<T>(
    val value: T,
    val label: String,
)

@Composable
private fun chipColors() =
    FilterChipDefaults.filterChipColors(
        containerColor = AppTheme.colors.surfaceSoft,
        labelColor = AppTheme.colors.textPrimary,
        selectedContainerColor = AppTheme.colors.primaryContainer,
        selectedLabelColor = AppTheme.colors.onPrimaryContainer,
        iconColor = AppTheme.colors.textSecondary,
        selectedLeadingIconColor = AppTheme.colors.onPrimaryContainer,
        selectedTrailingIconColor = AppTheme.colors.onPrimaryContainer,
    )

/** Chip that opens a menu to pick one value or none ("any"). */
@Composable
fun <T> MenuChip(
    label: String,
    anyLabel: String,
    selected: T?,
    options: List<ChoiceOption<T>>,
    modifier: Modifier = Modifier,
    onSelect: (T?) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    val current = options.firstOrNull { it.value == selected }?.label
    Box(modifier) {
        FilterChip(
            selected = selected != null,
            onClick = { open = true },
            label = { Text(current ?: label, style = AppTheme.typography.label) },
            trailingIcon = { Icon(painterResource(AppIcons.ExpandMore), contentDescription = null) },
            colors = chipColors(),
        )
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            DropdownMenuItem(text = { Text(anyLabel) }, onClick = {
                onSelect(null)
                open = false
            })
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option.label) }, onClick = {
                    onSelect(option.value)
                    open = false
                })
            }
        }
    }
}

/** Wrapping group of chips; single or multiple selection decided by the caller. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> ChipGroup(
    options: List<ChoiceOption<T>>,
    isSelected: (T) -> Boolean,
    onToggle: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.xs),
    ) {
        options.forEach { option ->
            FilterChip(
                selected = isSelected(option.value),
                onClick = { onToggle(option.value) },
                label = { Text(option.label, style = AppTheme.typography.label) },
                colors = chipColors(),
            )
        }
    }
}
