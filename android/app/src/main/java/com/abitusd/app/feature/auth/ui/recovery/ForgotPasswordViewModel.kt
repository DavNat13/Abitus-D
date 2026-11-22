package com.abitusd.app.feature.auth.ui.recovery

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

data class ForgotPasswordState(
    val email: String = "",
    @param:StringRes val emailError: Int? = null,
    @param:StringRes val error: Int? = null,
    val submitting: Boolean = false,
)

/** Requests a recovery code. The answer is the same whether or not the account exists. */
@HiltViewModel
class ForgotPasswordViewModel
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(ForgotPasswordState())
        val state: StateFlow<ForgotPasswordState> = mutableState.asStateFlow()

        private val sentChannel = Channel<String>(Channel.BUFFERED)
        val codeSent = sentChannel.receiveAsFlow()

        fun onEmail(value: String) = mutableState.update { it.copy(email = value, emailError = null, error = null) }

        fun submit() {
            val email = state.value.email
            if (!CredentialRules.isValidEmail(email)) {
                mutableState.update { it.copy(emailError = R.string.validation_email) }
                return
            }
            if (state.value.submitting) return
            mutableState.update { it.copy(submitting = true, error = null) }
            viewModelScope.launch {
                val result = repository.forgotPassword(email)
                mutableState.update { it.copy(submitting = false, error = result.errorText()) }
                if (result is ApiResult.Success) sentChannel.send(email.trim())
            }
        }
    }
