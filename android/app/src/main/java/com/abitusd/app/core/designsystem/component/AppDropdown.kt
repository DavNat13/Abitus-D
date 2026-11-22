package com.abitusd.app.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Single choice in a compact field-like dropdown (05d). [noneLabel] adds an "any" entry that selects null. */
@Composable
fun <T> AppDropdown(
    label: String,
    options: List<ChoiceOption<T>>,
    selected: T?,
    onSelect: (T?) -> Unit,
    modifier: Modifier = Modifier,
    noneLabel: String? = null,
) {
    val current = options.firstOrNull { it.value == selected }?.label ?: noneLabel.orEmpty()
    DropdownField(label, current, modifier) { close ->
        noneLabel?.let { none -> MenuEntry(none, selected == null) { onSelect(null).also { close() } } }
        options.forEach { option ->
            MenuEntry(option.label, option.value == selected) { onSelect(option.value).also { close() } }
        }
    }
}

/** Multiple choice: the menu stays open and the field lists what is selected. */
@Composable
fun <T> AppMultiDropdown(
    label: String,
    options: List<ChoiceOption<T>>,
    selected: Set<T>,
    onToggle: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val current = options.filter { it.value in selected }.joinToString(", ") { it.label }
    DropdownField(label, current, modifier) {
        options.forEach { option ->
            val checked = option.value in selected
            DropdownMenuItem(
                text = { Text(option.label, style = AppTheme.typography.body) },
                onClick = { onToggle(option.value) },
                leadingIcon = {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = null,
                        colors =
                            CheckboxDefaults.colors(
                                checkedColor = AppTheme.colors.primary,
                                uncheckedColor = AppTheme.colors.inputBorder,
                            ),
                    )
                },
            )
        }
    }
}

@Composable
private fun DropdownField(
    label: String,
    value: String,
    modifier: Modifier,
    items: @Composable (close: () -> Unit) -> Unit,
) {
    val colors = AppTheme.colors
    var expanded by remember { mutableStateOf(false) }
    Box(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    painterResource(AppIcons.ExpandMore),
                    contentDescription = null,
                    tint = colors.textSecondary,
                )
            },
            shape = AppShapes.field,
            textStyle = AppTheme.typography.body,
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colors.surfaceSoft,
                    unfocusedContainerColor = colors.surfaceSoft,
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.inputBorder,
                    focusedLabelColor = colors.primary,
                    unfocusedLabelColor = colors.textSecondary,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                ),
            modifier = Modifier.fillMaxWidth(),
        )
        Box(
            Modifier
                .matchParentSize()
                .clip(AppShapes.field)
                .clickable(role = Role.DropdownList, onClickLabel = label) { expanded = true },
        )
        DropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }, containerColor = colors.surface, shape = AppShapes.field) {
            items { expanded = false }
        }
    }
}

@Composable
private fun MenuEntry(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(
                text,
                style = if (selected) AppTheme.typography.label else AppTheme.typography.body,
                color = if (selected) AppTheme.colors.primary else AppTheme.colors.textPrimary,
            )
        },
        onClick = onClick,
    )
}
