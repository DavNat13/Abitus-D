package com.abitusd.app.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Empty state of a section until its phase arrives: icon in a soft circle and one line. */
@Composable
fun SectionPlaceholder(
    tab: MainTab,
    onOpenProfile: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    Column(modifier = modifier.fillMaxSize().background(colors.background).statusBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = AppSpacing.screenMargin, vertical = AppSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(tab.label),
                style = AppTheme.typography.titleLarge,
                color = colors.textPrimary,
                modifier = Modifier.semantics { heading() },
            )
            if (onOpenProfile != null) {
                IconButton(onClick = onOpenProfile) {
                    Icon(
                        painter = painterResource(AppIcons.Profile),
                        contentDescription = stringResource(R.string.home_open_profile),
                        tint = colors.textPrimary,
                    )
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(AppSpacing.screenMargin),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(AppSizes.iconContainer).background(colors.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(painterResource(tab.icon), contentDescription = null, tint = colors.primary)
            }
            Text(
                text = stringResource(tab.emptyText),
                style = AppTheme.typography.body,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = AppSpacing.md),
            )
        }
    }
}
