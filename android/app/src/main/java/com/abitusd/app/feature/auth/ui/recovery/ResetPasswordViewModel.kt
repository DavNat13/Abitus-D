package com.abitusd.app.feature.auth.ui.recovery

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.CODE_LENGTH
import com.abitusd.app.core.network.ApiResult
import com.abitusd.app.feature.auth.data.AuthRepository
import com.abitusd.app.feature.auth.domain.CredentialRules
import com.abitusd.app.feature.auth.ui.errorText
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResetPasswordState(
    val email: String,
    val code: String = "",
    val password: String = "",
    @param:StringRes val passwordError: Int? = null,
    @param:StringRes val error: Int? = null,
    val submitting: Boolean = false,
) {
    val canSubmit: Boolean get() = code.length == CODE_LENGTH && password.isNotEmpty() && !submitting
}

/** Sets a new password with the recovery code. All sessions end on the server. */
@HiltViewModel(assistedFactory = ResetPasswordViewModel.Factory::class)
class ResetPasswordViewModel
    @AssistedInject
    constructor(
        @Assisted email: String,
        private val repository: AuthRepository,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(ResetPasswordState(email = email))
        val state: StateFlow<ResetPasswordState> = mutableState.asStateFlow()

        private val doneChannel = Channel<Unit>(Channel.BUFFERED)
        val done = doneChannel.receiveAsFlow()

        fun onCode(value: String) = mutableState.update { it.copy(code = value, error = null) }

        fun onPassword(value: String) =
            mutableState.update { it.copy(password = value, passwordError = null, error = null) }

        fun submit() {
            val current = state.value
            if (!CredentialRules.meetsMinimum(current.password)) {
                mutableState.update { it.copy(passwordError = R.string.validation_password_length) }
                return
            }
            if (!current.canSubmit) return
            mutableState.update { it.copy(submitting = true, error = null) }
            viewModelScope.launch {
                val result = repository.resetPassword(current.email, current.code, current.password)
                mutableState.update { it.copy(submitting = false, error = result.errorText()) }
                if (result is ApiResult.Success) doneChannel.send(Unit)
            }
        }

        @AssistedFactory
        interface Factory {
            fun create(email: String): ResetPasswordViewModel
        }
    }
