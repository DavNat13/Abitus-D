package com.abitusd.app.core.designsystem.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Confirmation for destructive or critical actions only (05c). */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    destructive: Boolean = true,
) {
    val colors = AppTheme.colors
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = AppShapes.hero,
        containerColor = colors.surface,
        title = { Text(title, style = AppTheme.typography.titleMedium, color = colors.textPrimary) },
        text = { Text(message, style = AppTheme.typography.body, color = colors.textSecondary) },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onConfirm()
            }) {
                Text(
                    confirmLabel,
                    style = AppTheme.typography.label,
                    color = if (destructive) colors.error else colors.primary,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) { Text(dismissLabel, style = AppTheme.typography.label, color = colors.primary) }
        },
    )
}
