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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppShapes
import com.abitusd.app.core.designsystem.theme.AppSizes
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.core.designsystem.theme.LightAppColors

/** Two actions and no long tutorial (05-UI-UX.md). Brand gradient on the hero card. */
@Composable
fun WelcomeScreen(
    onCreateAccount: () -> Unit,
    onSignIn: () -> Unit,
) {
    val brand = LightAppColors
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
                    .verticalScroll(rememberScrollState())
                    .padding(AppSpacing.screenMargin),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(brand.navy, brand.primary)), AppShapes.hero)
                        .padding(AppSpacing.xl),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = AppTheme.typography.titleMedium,
                    color = brand.onPrimary,
                )
                Text(
                    text = stringResource(R.string.welcome_title),
                    style = AppTheme.typography.headline,
                    color = brand.onPrimary,
                    modifier = Modifier.semantics { heading() },
                )
                Text(
                    text = stringResource(R.string.welcome_subtitle),
                    style = AppTheme.typography.body,
                    color = brand.onPrimary,
                )
            }
            SectionCard(stringResource(R.string.welcome_record_title), icon = AppIcons.Train) {
                Text(stringResource(R.string.welcome_record_description), style = AppTheme.typography.body)
            }
            SectionCard(stringResource(R.string.welcome_plan_title), icon = AppIcons.Calendar) {
                Text(stringResource(R.string.welcome_plan_description), style = AppTheme.typography.body)
            }
            SectionCard(stringResource(R.string.welcome_progress_title), icon = AppIcons.Progress) {
                Text(stringResource(R.string.welcome_progress_description), style = AppTheme.typography.body)
            }
            Spacer(Modifier.padding(AppSpacing.xs))
            AppButton(stringResource(R.string.welcome_create_account), onCreateAccount, icon = AppIcons.Add)
            AppButton(
                stringResource(R.string.welcome_sign_in),
                onSignIn,
                style = AppButtonStyle.Secondary,
                icon = AppIcons.Profile,
            )
        }
    }
}
