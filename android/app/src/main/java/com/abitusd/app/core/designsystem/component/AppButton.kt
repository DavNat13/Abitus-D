package com.abitusd.app.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.abitusd.app.core.designsystem.motion.AppMotion
import com.abitusd.app.core.designsystem.motion.pressScale
import com.abitusd.app.core.designsystem.theme.AppColors
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Button styles of 05c. */
enum class AppButtonStyle { Primary, Secondary, Tonal, Text, Destructive }

private val ProgressSize = 20.dp
private val ProgressStroke = 2.dp

private data class ButtonLook(
    val container: Color,
    val content: Color,
    val border: Color?,
)

private fun look(
    style: AppButtonStyle,
    c: AppColors,
    pressed: Boolean,
    active: Boolean,
): ButtonLook =
    when (style) {
        AppButtonStyle.Primary -> {
            ButtonLook(if (pressed) c.primaryPressed else c.primary, c.onPrimary, null)
        }

        AppButtonStyle.Secondary -> {
            ButtonLook(
                if (pressed) c.primaryContainer else c.surface,
                c.primary,
                if (active) c.primary else c.outline,
            )
        }

        AppButtonStyle.Tonal -> {
            ButtonLook(if (pressed) c.primaryContainer else c.surfaceSoft, c.navy, null)
        }

        AppButtonStyle.Text -> {
            ButtonLook(if (pressed) c.primaryContainer else Color.Transparent, c.primary, null)
        }

        AppButtonStyle.Destructive -> {
            ButtonLook(
                if (pressed) c.errorContainer else c.surface,
                c.error,
                if (active) c.error else c.outline,
            )
        }
    }

/** Button with the states of 05c: rest, pressed, disabled and loading (no double tap). */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: AppButtonStyle = AppButtonStyle.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    @DrawableRes icon: Int? = null,
) {
    val colors = AppTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val active = enabled && !loading
    val l = look(style, colors, pressed, active)
    val disabledContainer = if (style == AppButtonStyle.Primary && !loading) colors.outline else l.container
    Button(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .height(
                    AppSizes.buttonHeight,
                ).pressScale(interaction, AppMotion.PRESS_SCALE_BUTTON),
        enabled = active,
        shape = AppShapes.button,
        interactionSource = interaction,
        border = l.border?.let { BorderStroke(AppSizes.borderButton, it) },
        elevation = null,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = l.container,
                contentColor = l.content,
                disabledContainerColor = disabledContainer,
                disabledContentColor = if (loading) l.content else colors.textDisabled,
            ),
    ) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(ProgressSize), strokeWidth = ProgressStroke, color = l.content)
        } else {
            icon?.let {
                Icon(painterResource(it), contentDescription = null, modifier = Modifier.size(AppSizes.iconSmall))
                Spacer(Modifier.width(AppSpacing.xs))
            }
            Text(text = text, style = AppTheme.typography.label)
        }
    }
}
