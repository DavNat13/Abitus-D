package com.abitusd.app.feature.auth.ui.verify

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.designsystem.component.CODE_LENGTH
import com.abitusd.app.core.network.ApiResult
import com.abitusd.app.feature.auth.data.AuthRepository
import com.abitusd.app.feature.auth.ui.errorText
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VerifyState(
    val email: String,
    val code: String = "",
    val submitting: Boolean = false,
    val resendSeconds: Int = RESEND_WAIT_SECONDS,
    @param:StringRes val error: Int? = null,
    @param:StringRes val info: Int? = null,
) {
    val canSubmit: Boolean get() = code.length == CODE_LENGTH && !submitting
}

const val RESEND_WAIT_SECONDS = 60
private const val ONE_SECOND_MS = 1000L

/** Email verification. On success the session starts and the root switches to the app. */
@HiltViewModel(assistedFactory = VerifyViewModel.Factory::class)
class VerifyViewModel
    @AssistedInject
    constructor(
        @Assisted email: String,
        private val repository: AuthRepository,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(VerifyState(email = email))
        val state: StateFlow<VerifyState> = mutableState.asStateFlow()
        private var countdown: Job? = null

        init {
            startCountdown()
        }

        fun onCode(value: String) {
            mutableState.update { it.copy(code = value, error = null) }
            if (value.length == CODE_LENGTH) submit()
        }

        fun submit() {
            if (!state.value.canSubmit) return
            mutableState.update { it.copy(submitting = true, error = null, info = null) }
            viewModelScope.launch {
                val result = repository.verifyEmail(state.value.email, state.value.code)
                if (result !is ApiResult.Success) {
                    mutableState.update { it.copy(submitting = false, code = "", error = result.errorText()) }
                }
            }
        }

        fun resend() {
            if (state.value.resendSeconds > 0) return
            viewModelScope.launch {
                val result = repository.resendCode(state.value.email)
                mutableState.update {
                    it.copy(
                        error = result.errorText(),
                        info = if (result is ApiResult.Success) R.string.verify_resent else null,
                    )
                }
                if (result is ApiResult.Success) startCountdown()
            }
        }

        private fun startCountdown() {
            countdown?.cancel()
            countdown =
                viewModelScope.launch {
                    for (left in RESEND_WAIT_SECONDS downTo 0) {
                        mutableState.update { it.copy(resendSeconds = left) }
                        if (left > 0) delay(ONE_SECOND_MS)
                    }
                }
        }

        @AssistedFactory
        interface Factory {
            fun create(email: String): VerifyViewModel
        }
    }
