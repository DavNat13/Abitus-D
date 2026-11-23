package com.abitusd.app.feature.auth.ui.verify

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.CodeInput
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.component.StatusBanner
import com.abitusd.app.core.designsystem.component.StatusKind
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme

@Composable
fun VerifyScreen(
    email: String,
    onBack: () -> Unit,
    viewModel: VerifyViewModel =
        hiltViewModel<VerifyViewModel, VerifyViewModel.Factory>(
            key = email,
            creationCallback = { factory -> factory.create(email) },
        ),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    FormScreen(
        title = stringResource(R.string.verify_title),
        subtitle = stringResource(R.string.verify_subtitle, state.email),
        onBack = onBack,
    ) {
        SectionCard(stringResource(R.string.auth_verification), icon = AppIcons.Email) {
            CodeInput(
                code = state.code,
                onCodeChange = viewModel::onCode,
                accessibilityLabel = stringResource(R.string.verify_code_label),
                isError = state.error != null,
                onDone = viewModel::submit,
            )
        }
        state.error?.let { StatusBanner(stringResource(it), StatusKind.Error) }
        state.info?.let { StatusBanner(stringResource(it), StatusKind.Success) }
        AppButton(
            text = stringResource(R.string.verify_submit),
            onClick = viewModel::submit,
            enabled = state.canSubmit,
            icon = AppIcons.Check,
            loading = state.submitting,
        )
        if (state.resendSeconds > 0) {
            Text(
                text = stringResource(R.string.verify_resend_wait, state.resendSeconds),
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
            )
        } else {
            AppButton(stringResource(R.string.verify_resend), viewModel::resend, style = AppButtonStyle.Secondary)
        }
    }
}
