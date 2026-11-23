package com.abitusd.app.feature.auth.ui.login

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

@Composable
fun LoginScreen(
    passwordWasReset: Boolean,
    onBack: () -> Unit,
    onForgotPassword: () -> Unit,
    onCreateAccount: () -> Unit,
    onNeedsVerification: (email: String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.openVerification.collect(onNeedsVerification) }
    FormScreen(title = stringResource(R.string.login_title), onBack = onBack) {
        if (passwordWasReset) StatusBanner(stringResource(R.string.login_password_reset_done), StatusKind.Success)
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
                isPassword = true,
                imeAction = ImeAction.Done,
                onImeAction = viewModel::submit,
            )
        }
        state.error?.let { StatusBanner(stringResource(it), StatusKind.Error) }
        AppButton(
            stringResource(R.string.login_submit),
            viewModel::submit,
            loading = state.submitting,
            icon = AppIcons.Check,
        )
        AppButton(stringResource(R.string.login_forgot), onForgotPassword, style = AppButtonStyle.Text)
        AppButton(stringResource(R.string.login_no_account), onCreateAccount, style = AppButtonStyle.Secondary)
    }
}
