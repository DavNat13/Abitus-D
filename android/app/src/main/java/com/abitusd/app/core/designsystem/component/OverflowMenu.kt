package com.abitusd.app.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppTheme

/** One entry of a contextual menu. */
data class MenuAction(
    val label: String,
    @param:DrawableRes val icon: Int,
    val destructive: Boolean = false,
    val onClick: () -> Unit,
)

/** Three-dot button that opens the secondary actions of an item (05d). */
@Composable
fun OverflowMenu(
    contentDescription: String,
    actions: List<MenuAction>,
) {
    val colors = AppTheme.colors
    var open by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { open = true }) {
            Icon(painterResource(AppIcons.More), contentDescription = contentDescription, tint = colors.textSecondary)
        }
        DropdownMenu(expanded = open, onDismissRequest = {
            open = false
        }, containerColor = colors.surface, shape = AppShapes.field) {
            actions.forEach { action ->
                val tint = if (action.destructive) colors.error else colors.textPrimary
                DropdownMenuItem(
                    text = { Text(action.label, style = AppTheme.typography.body, color = tint) },
                    leadingIcon = { Icon(painterResource(action.icon), contentDescription = null, tint = tint) },
                    onClick = {
                        open = false
                        action.onClick()
                    },
                )
            }
        }
    }
}
