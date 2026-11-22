package com.abitusd.app.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Screen title with optional subtitle and up to one trailing action (05d, Jerarquia). */
@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: @Composable () -> Unit = {},
) {
    Row(
        modifier.fillMaxWidth().padding(
            start = AppSpacing.screenMargin,
            end = AppSpacing.xs,
            top = AppSpacing.md,
            bottom = AppSpacing.xs,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colors.textPrimary,
                modifier =
                    Modifier
                        .semantics {
                            heading()
                        },
            )
            subtitle?.let { Text(it, style = AppTheme.typography.caption, color = AppTheme.colors.textSecondary) }
        }
        action()
    }
}

/** Section label inside a screen. */
@Composable
fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text,
        style = AppTheme.typography.label,
        color = AppTheme.colors.textSecondary,
        modifier = modifier.padding(top = AppSpacing.xs).semantics { heading() },
    )
}
