package com.abitusd.app.feature.auth.ui.register

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.network.ApiResult
import com.abitusd.app.feature.auth.data.AuthRepository
import com.abitusd.app.feature.auth.domain.CredentialRules
import com.abitusd.app.feature.auth.domain.PasswordStrength
import com.abitusd.app.feature.auth.ui.appLanguage
import com.abitusd.app.feature.auth.ui.errorText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterState(
    val email: String = "",
    val password: String = "",
    val ageConfirmed: Boolean = false,
    val legalAccepted: Boolean = false,
    @param:StringRes val emailError: Int? = null,
    @param:StringRes val passwordError: Int? = null,
    @param:StringRes val error: Int? = null,
    val submitting: Boolean = false,
) {
    val strength: PasswordStrength get() = CredentialRules.strength(password)
    val canSubmit: Boolean get() = ageConfirmed && legalAccepted && !submitting
}

@HiltViewModel
class RegisterViewModel
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(RegisterState())
        val state: StateFlow<RegisterState> = mutableState.asStateFlow()

        private val verifyChannel = Channel<String>(Channel.BUFFERED)

        /** Emits the email once registration is accepted, to open the verification screen. */
        val openVerification = verifyChannel.receiveAsFlow()

        fun onEmail(value: String) = mutableState.update { it.copy(email = value, emailError = null, error = null) }

        fun onPassword(value: String) =
            mutableState.update { it.copy(password = value, passwordError = null, error = null) }

        fun onAge(value: Boolean) = mutableState.update { it.copy(ageConfirmed = value) }

        fun onLegal(value: Boolean) = mutableState.update { it.copy(legalAccepted = value) }

        fun submit() {
            val current = state.value
            val emailError = if (CredentialRules.isValidEmail(current.email)) null else R.string.validation_email
            val passwordError =
                if (CredentialRules.meetsMinimum(current.password)) null else R.string.validation_password_length
            if (emailError != null || passwordError != null || !current.canSubmit) {
                mutableState.update { it.copy(emailError = emailError, passwordError = passwordError) }
                return
            }
            mutableState.update { it.copy(submitting = true, error = null) }
            viewModelScope.launch {
                val result = repository.register(current.email, current.password, appLanguage())
                mutableState.update { it.copy(submitting = false, error = result.errorText()) }
                if (result is ApiResult.Success) verifyChannel.send(current.email.trim())
            }
        }
    }
