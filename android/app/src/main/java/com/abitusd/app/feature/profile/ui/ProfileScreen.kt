package com.abitusd.app.feature.profile.ui

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.ConfirmDialog
import com.abitusd.app.core.designsystem.component.FormScreen
import com.abitusd.app.core.designsystem.component.InfoButton
import com.abitusd.app.core.designsystem.component.NavRow
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.auth.data.AuthRepository
import com.abitusd.app.feature.train.ui.EditorInfo
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

/** Profile in cards: preferences, legal documents and account. Full settings arrive in Phase 9. */
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenCredits: () -> Unit,
    onOpenAthlete: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val signingOut by viewModel.signingOut.collectAsStateWithLifecycle()
    var confirmSignOut by rememberSaveable { mutableStateOf(false) }
    FormScreen(title = stringResource(R.string.profile_title), onBack = onBack, info = {
        InfoButton(R.string.info_screen_profile, EditorInfo.profile)
    }) {
        SectionCard(stringResource(R.string.athlete_title), icon = AppIcons.Profile) {
            NavRow(stringResource(R.string.athlete_title), onOpenAthlete)
        }
        SectionCard(stringResource(R.string.profile_preferences), icon = AppIcons.Filter) {
            LanguagePicker()
            ThemePicker()
            EnglishNamesPicker()
        }
        SectionCard(stringResource(R.string.profile_legal), icon = AppIcons.Info) {
            NavRow(stringResource(R.string.profile_privacy), onOpenPrivacy)
            HorizontalDivider(color = AppTheme.colors.outline)
            NavRow(stringResource(R.string.profile_terms), onOpenTerms)
            HorizontalDivider(color = AppTheme.colors.outline)
            NavRow(stringResource(R.string.profile_credits), onOpenCredits)
        }
        SectionCard(stringResource(R.string.profile_account), icon = AppIcons.Profile) {
            Text(
                stringResource(R.string.profile_sign_out_note),
                style = AppTheme.typography.body,
                color = AppTheme.colors.textSecondary,
            )
            AppButton(stringResource(R.string.profile_sign_out), {
                confirmSignOut = true
            }, style = AppButtonStyle.Destructive, loading = signingOut, icon = AppIcons.Logout)
        }
    }
    if (confirmSignOut) {
        ConfirmDialog(
            title = stringResource(R.string.profile_sign_out),
            message = stringResource(R.string.profile_sign_out_note),
            confirmLabel = stringResource(R.string.profile_sign_out),
            dismissLabel = stringResource(R.string.detail_cancel),
            onConfirm = viewModel::signOut,
            onDismiss = { confirmSignOut = false },
        )
    }
}
