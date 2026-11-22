package com.abitusd.app.feature.auth.data

import android.os.Build
import com.abitusd.app.BuildConfig
import com.abitusd.app.core.database.SyncStateDao
import com.abitusd.app.core.network.AccountApi
import com.abitusd.app.core.network.ApiResult
import com.abitusd.app.core.network.PublicAuthApi
import com.abitusd.app.core.network.apiCall
import com.abitusd.app.core.network.dto.DeviceDto
import com.abitusd.app.core.network.dto.EmailRequest
import com.abitusd.app.core.network.dto.LoginRequest
import com.abitusd.app.core.network.dto.RegisterRequest
import com.abitusd.app.core.network.dto.ResetPasswordRequest
import com.abitusd.app.core.network.dto.TokensDto
import com.abitusd.app.core.network.dto.VerifyEmailRequest
import com.abitusd.app.core.session.SessionManager
import com.abitusd.app.core.sync.SyncScheduler
import com.abitusd.app.feature.library.data.OwnExerciseRepository
import javax.inject.Inject
import javax.inject.Singleton

/** Versions of the legal documents bundled with this build (legal/). */
object LegalVersions {
    const val TERMS = "2026-11"
    const val PRIVACY = "2026-11"
}

/** Account access. Successful sign-ins store the session; everything else is passed through. */
@Singleton
class AuthRepository
    @Inject
    constructor(
        private val publicApi: PublicAuthApi,
        private val accountApi: AccountApi,
        private val sessions: SessionManager,
        private val syncState: SyncStateDao,
        private val ownExercises: OwnExerciseRepository,
        private val syncScheduler: SyncScheduler,
    ) {
        suspend fun register(
            email: String,
            password: String,
            locale: String,
        ): ApiResult<Unit> =
            apiCall {
                publicApi.register(
                    RegisterRequest(email.trim(), password, locale, true, LegalVersions.TERMS, LegalVersions.PRIVACY),
                )
            }.toUnit()

        suspend fun verifyEmail(
            email: String,
            code: String,
        ): ApiResult<Unit> = signIn { publicApi.verifyEmail(VerifyEmailRequest(email.trim(), code, device())) }

        suspend fun resendCode(email: String): ApiResult<Unit> =
            apiCall { publicApi.resendCode(EmailRequest(email.trim())) }.toUnit()

        suspend fun login(
            email: String,
            password: String,
        ): ApiResult<Unit> = signIn { publicApi.login(LoginRequest(email.trim(), password, device())) }

        suspend fun forgotPassword(email: String): ApiResult<Unit> =
            apiCall { publicApi.forgotPassword(EmailRequest(email.trim())) }.toUnit()

        suspend fun resetPassword(
            email: String,
            code: String,
            newPassword: String,
        ): ApiResult<Unit> =
            apiCall { publicApi.resetPassword(ResetPasswordRequest(email.trim(), code, newPassword)) }.toUnit()

        /** Ends the session on the server when possible and always clears local data. */
        suspend fun logout() {
            apiCall { accountApi.logout() }
            syncScheduler.cancel()
            ownExercises.clear()
            syncState.clear()
            sessions.clear()
        }

        private suspend fun signIn(call: suspend () -> TokensDto): ApiResult<Unit> =
            when (val result = apiCall { call() }) {
                is ApiResult.Success -> {
                    sessions.save(result.value)
                    ApiResult.Success(Unit)
                }

                is ApiResult.Problem -> {
                    result
                }

                ApiResult.Offline -> {
                    ApiResult.Offline
                }

                ApiResult.Unexpected -> {
                    ApiResult.Unexpected
                }
            }

        private suspend fun device() =
            DeviceDto(
                installationId = sessions.installationId(),
                name = "${Build.MANUFACTURER} ${Build.MODEL}".trim().take(MAX_DEVICE_NAME),
                appVersion = BuildConfig.VERSION_NAME,
            )

        private fun <T> ApiResult<T>.toUnit(): ApiResult<Unit> =
            when (this) {
                is ApiResult.Success -> ApiResult.Success(Unit)
                is ApiResult.Problem -> this
                ApiResult.Offline -> ApiResult.Offline
                ApiResult.Unexpected -> ApiResult.Unexpected
            }

        private companion object {
            const val MAX_DEVICE_NAME = 80
        }
    }
