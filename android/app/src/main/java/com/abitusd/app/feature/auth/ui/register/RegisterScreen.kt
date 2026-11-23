package com.abitusd.app.feature.auth.ui.register

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.AppTextField
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.component.StatusBanner
import com.abitusd.app.core.designsystem.component.StatusKind
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.auth.domain.PasswordStrength
import com.abitusd.app.feature.auth.ui.LabeledCheckbox

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onRegistered: (email: String) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.openVerification.collect(onRegistered) }
    FormScreen(
        title = stringResource(R.string.register_title),
        subtitle = stringResource(R.string.register_subtitle),
        onBack = onBack,
    ) {
        SectionCard(stringResource(R.string.auth_credentials), icon = AppIcons.Profile) {
            AppTextField(
                value = state.email,
                onValueChange = viewModel::onEmail,
                label = stringResource(R.string.field_email),
                error = state.emailError?.let { stringResource(it) },
                keyboardType = KeyboardType.Email,
            )
            AppTextField(
                value = state.password,
                onValueChange = viewModel::onPassword,
                label = stringResource(R.string.field_password),
                error = state.passwordError?.let { stringResource(it) },
                supportingText = stringResource(R.string.register_password_hint),
                isPassword = true,
                imeAction = ImeAction.Done,
            )
            if (state.password.isNotEmpty()) PasswordStrengthText(state.strength)
        }
        SectionCard(stringResource(R.string.auth_consent), icon = AppIcons.Safety) {
            LabeledCheckbox(stringResource(R.string.register_age), state.ageConfirmed, viewModel::onAge)
            LabeledCheckbox(stringResource(R.string.register_legal), state.legalAccepted, viewModel::onLegal)
            AppButton(stringResource(R.string.register_read_terms), onOpenTerms, style = AppButtonStyle.Text)
            AppButton(stringResource(R.string.register_read_privacy), onOpenPrivacy, style = AppButtonStyle.Text)
        }
        state.error?.let { StatusBanner(stringResource(it), StatusKind.Error) }
        AppButton(
            text = stringResource(R.string.register_submit),
            onClick = viewModel::submit,
            enabled = state.canSubmit,
            icon = AppIcons.Check,
            loading = state.submitting,
        )
    }
}

@Composable
private fun PasswordStrengthText(strength: PasswordStrength) {
    val colors = AppTheme.colors
    val (text, color) =
        when (strength) {
            PasswordStrength.WEAK -> R.string.password_strength_weak to colors.error
            PasswordStrength.MEDIUM -> R.string.password_strength_medium to colors.warning
            PasswordStrength.STRONG -> R.string.password_strength_strong to colors.successText
        }
    Text(text = stringResource(text), style = AppTheme.typography.caption, color = color)
}
