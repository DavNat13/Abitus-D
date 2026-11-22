package com.abitusd.app.feature.auth.ui.login

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.network.ApiResult
import com.abitusd.app.feature.auth.data.AuthRepository
import com.abitusd.app.feature.auth.domain.CredentialRules
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

data class LoginState(
    val email: String = "",
    val password: String = "",
    @param:StringRes val emailError: Int? = null,
    @param:StringRes val passwordError: Int? = null,
    @param:StringRes val error: Int? = null,
    val submitting: Boolean = false,
)

/** Sign in. On success the session starts and the root switches to the app. */
@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(LoginState())
        val state: StateFlow<LoginState> = mutableState.asStateFlow()

        private val verifyChannel = Channel<String>(Channel.BUFFERED)

        /** Emits the email when the account still needs email verification. */
        val openVerification = verifyChannel.receiveAsFlow()

        fun onEmail(value: String) = mutableState.update { it.copy(email = value, emailError = null, error = null) }

        fun onPassword(value: String) =
            mutableState.update { it.copy(password = value, passwordError = null, error = null) }

        fun submit() {
            val current = state.value
            val emailError = if (CredentialRules.isValidEmail(current.email)) null else R.string.validation_email
            val passwordError = if (current.password.isEmpty()) R.string.validation_required else null
            if (emailError != null || passwordError != null || current.submitting) {
                mutableState.update { it.copy(emailError = emailError, passwordError = passwordError) }
                return
            }
            mutableState.update { it.copy(submitting = true, error = null) }
            viewModelScope.launch {
                val result = repository.login(current.email, current.password)
                mutableState.update { it.copy(submitting = false, error = result.errorText()) }
                if (result is ApiResult.Problem && result.code == "AUTH_EMAIL_NOT_VERIFIED") {
                    repository.resendCode(current.email)
                    verifyChannel.send(current.email.trim())
                }
            }
        }
    }
