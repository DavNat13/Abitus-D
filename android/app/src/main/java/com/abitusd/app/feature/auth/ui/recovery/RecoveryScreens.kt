package com.abitusd.app.feature.auth.ui.recovery

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
import com.abitusd.app.core.designsystem.component.AppTextField
import com.abitusd.app.core.designsystem.component.CodeInput
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.component.StatusBanner
import com.abitusd.app.core.designsystem.component.StatusKind

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onCodeSent: (email: String) -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.codeSent.collect(onCodeSent) }
    FormScreen(
        title = stringResource(R.string.forgot_title),
        subtitle = stringResource(R.string.forgot_subtitle),
        onBack = onBack,
    ) {
        AppTextField(
            value = state.email,
            onValueChange = viewModel::onEmail,
            label = stringResource(R.string.field_email),
            error = state.emailError?.let { stringResource(it) },
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            onImeAction = viewModel::submit,
        )
        state.error?.let { StatusBanner(stringResource(it), StatusKind.Error) }
        AppButton(stringResource(R.string.forgot_submit), viewModel::submit, loading = state.submitting)
    }
}

@Composable
fun ResetPasswordScreen(
    email: String,
    onBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: ResetPasswordViewModel =
        hiltViewModel<ResetPasswordViewModel, ResetPasswordViewModel.Factory>(
            key = email,
            creationCallback = { factory -> factory.create(email) },
        ),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.done.collect { onDone() } }
    FormScreen(
        title = stringResource(R.string.reset_title),
        subtitle = stringResource(R.string.reset_subtitle, state.email),
        onBack = onBack,
    ) {
        CodeInput(
            code = state.code,
            onCodeChange = viewModel::onCode,
            accessibilityLabel = stringResource(R.string.verify_code_label),
            isError = state.error != null,
        )
        AppTextField(
            value = state.password,
            onValueChange = viewModel::onPassword,
            label = stringResource(R.string.field_new_password),
            error = state.passwordError?.let { stringResource(it) },
            supportingText = stringResource(R.string.register_password_hint),
            isPassword = true,
            imeAction = ImeAction.Done,
            onImeAction = viewModel::submit,
        )
        state.error?.let { StatusBanner(stringResource(it), StatusKind.Error) }
        AppButton(
            text = stringResource(R.string.reset_submit),
            onClick = viewModel::submit,
            enabled = state.canSubmit,
            loading = state.submitting,
        )
    }
}
