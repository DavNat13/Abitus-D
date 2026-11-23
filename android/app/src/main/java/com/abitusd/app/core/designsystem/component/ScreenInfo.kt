package com.abitusd.app.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** One explained element of a screen: its icon, its name and what it does. */
data class InfoEntry(
    @param:DrawableRes val icon: Int,
    @param:StringRes val title: Int,
    @param:StringRes val text: Int,
)

/**
 * Header info button (author's request, 2026-11-23): headers show only the screen name and this
 * button, which opens a sheet explaining every element of the screen.
 */
@Composable
fun InfoButton(
    @StringRes screen: Int,
    entries: List<InfoEntry>,
) {
    var open by rememberSaveable { mutableStateOf(false) }
    IconButton(onClick = { open = true }) {
        Icon(painterResource(AppIcons.ScreenInfo), stringResource(R.string.info_button), tint = AppTheme.colors.primary)
    }
    if (open) {
        AppBottomSheet(stringResource(R.string.info_title, stringResource(screen)), { open = false }) {
            entries.forEachIndexed { index, entry ->
                if (index > 0) HorizontalDivider(color = AppTheme.colors.outline)
                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                    IconBadge(entry.icon, size = AppSizes.headerBadge)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.xxxs)) {
                        Text(
                            stringResource(entry.title),
                            style = AppTheme.typography.titleMedium,
                            color = AppTheme.colors.textPrimary,
                        )
                        Text(
                            stringResource(entry.text),
                            style = AppTheme.typography.body,
                            color = AppTheme.colors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}
