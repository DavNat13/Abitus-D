package com.abitusd.app.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

enum class StatusKind { Error, Success, Warning }

/** State card of 05c: soft container, icon and text, no shadow. Announced by screen readers. */
@Composable
fun StatusBanner(
    message: String,
    kind: StatusKind,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val (container, content, icon) =
        when (kind) {
            StatusKind.Error -> Triple(colors.errorContainer, colors.error, AppIcons.Error)
            StatusKind.Success -> Triple(colors.successContainer, colors.successText, AppIcons.Success)
            StatusKind.Warning -> Triple(colors.warningContainer, colors.warning, AppIcons.Offline)
        }
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(container, AppShapes.field)
                .padding(AppSpacing.sm)
                .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painter = painterResource(icon), contentDescription = null, tint = content)
        Text(text = message, style = AppTheme.typography.label, color = colors.textPrimary)
    }
}
