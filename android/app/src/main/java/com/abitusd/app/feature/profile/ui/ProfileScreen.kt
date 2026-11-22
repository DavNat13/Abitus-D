package com.abitusd.app.feature.profile.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.auth.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) : ViewModel() {
        private val mutableSigningOut = MutableStateFlow(false)
        val signingOut: StateFlow<Boolean> = mutableSigningOut.asStateFlow()

        /** The root leaves the app shell when the session state becomes signed out. */
        fun signOut() {
            if (mutableSigningOut.value) return
            mutableSigningOut.value = true
            viewModelScope.launch { repository.logout() }
        }
    }

/** Phase 1 profile: legal documents and sign out. Settings arrive in Phase 9. */
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenCredits: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val signingOut by viewModel.signingOut.collectAsStateWithLifecycle()
    FormScreen(title = stringResource(R.string.profile_title), onBack = onBack) {
        LanguagePicker()
        EnglishNamesPicker()
        AppButton(stringResource(R.string.profile_privacy), onOpenPrivacy, style = AppButtonStyle.Text)
        AppButton(stringResource(R.string.profile_terms), onOpenTerms, style = AppButtonStyle.Text)
        AppButton(stringResource(R.string.profile_credits), onOpenCredits, style = AppButtonStyle.Text)
        Text(
            text = stringResource(R.string.profile_sign_out_note),
            style = AppTheme.typography.caption,
            color = AppTheme.colors.textSecondary,
        )
        AppButton(
            text = stringResource(R.string.profile_sign_out),
            onClick = viewModel::signOut,
            style = AppButtonStyle.Secondary,
            loading = signingOut,
        )
    }
}
