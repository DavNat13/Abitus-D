package com.abitusd.app.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme

/** Two actions and no long tutorial (05-UI-UX.md). Brand gradient on the hero card. */
@Composable
fun WelcomeScreen(
    onCreateAccount: () -> Unit,
    onSignIn: () -> Unit,
) {
    val colors = AppTheme.colors
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(colors.background)
                .safeDrawingPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier =
                Modifier
                    .widthIn(max = AppSizes.maxContentWidth)
                    .padding(AppSpacing.screenMargin),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(colors.navy, colors.primary)), AppShapes.hero)
                        .padding(AppSpacing.xl),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = AppTheme.typography.titleMedium,
                    color = colors.onPrimary,
                )
                Text(
                    text = stringResource(R.string.welcome_title),
                    style = AppTheme.typography.headline,
                    color = colors.onPrimary,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = stringResource(R.string.welcome_subtitle),
                    style = AppTheme.typography.body,
                    color = colors.onPrimary,
                )
            }
            Spacer(Modifier.padding(AppSpacing.xs))
            AppButton(stringResource(R.string.welcome_create_account), onCreateAccount)
            AppButton(stringResource(R.string.welcome_sign_in), onSignIn, style = AppButtonStyle.Secondary)
        }
    }
}
