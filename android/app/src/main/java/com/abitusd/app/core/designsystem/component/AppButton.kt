package com.abitusd.app.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppTheme

enum class AppButtonStyle { Primary, Secondary, Text }

private const val PRESSED_SCALE = 0.97f
private val ProgressSize = 20.dp
private val ProgressStroke = 2.dp

/** Button with the states of 05c: rest, pressed, disabled and loading (no double tap). */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: AppButtonStyle = AppButtonStyle.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    val colors = AppTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val sized =
        modifier
            .fillMaxWidth()
            .height(AppSizes.buttonHeight)
            .scale(if (pressed) PRESSED_SCALE else 1f)
    val active = enabled && !loading
    val content: @Composable () -> Unit = {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(ProgressSize),
                strokeWidth = ProgressStroke,
                color = if (style == AppButtonStyle.Primary) colors.onPrimary else colors.primary,
            )
        } else {
            Text(text = text, style = AppTheme.typography.label)
        }
    }
    when (style) {
        AppButtonStyle.Primary -> {
            Button(
                onClick = onClick,
                modifier = sized,
                enabled = active,
                shape = AppShapes.button,
                interactionSource = interaction,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = if (pressed) colors.primaryPressed else colors.primary,
                        contentColor = colors.onPrimary,
                        disabledContainerColor = if (loading) colors.primary else colors.outline,
                        disabledContentColor = if (loading) colors.onPrimary else colors.textDisabled,
                    ),
            ) { content() }
        }

        AppButtonStyle.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                modifier = sized,
                enabled = active,
                shape = AppShapes.button,
                interactionSource = interaction,
                border = BorderStroke(AppSizes.borderButton, if (active) colors.primary else colors.outline),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = if (pressed) colors.primaryContainer else colors.surface,
                        contentColor = colors.primary,
                        disabledContentColor = colors.textDisabled,
                    ),
            ) { content() }
        }

        AppButtonStyle.Text -> {
            TextButton(
                onClick = onClick,
                modifier = sized,
                enabled = active,
                shape = AppShapes.button,
                interactionSource = interaction,
                colors =
                    ButtonDefaults.textButtonColors(
                        contentColor = colors.primary,
                        disabledContentColor = colors.textDisabled,
                    ),
            ) { content() }
        }
    }
}
