package com.abitusd.app.feature.auth.ui

import androidx.annotation.StringRes
import com.abitusd.app.R
import com.abitusd.app.core.i18n.AppLocale
import com.abitusd.app.core.network.ApiResult

/** Maps server problem codes (07-BACKEND.md) and network states to localized texts. */
@StringRes
fun ApiResult<*>.errorText(): Int? =
    when (this) {
        is ApiResult.Success -> {
            null
        }

        ApiResult.Offline -> {
            R.string.error_offline
        }

        ApiResult.Unexpected -> {
            R.string.error_unexpected
        }

        is ApiResult.Problem -> {
            when (code) {
                "AUTH_INVALID_CREDENTIALS" -> R.string.error_invalid_credentials
                "AUTH_EMAIL_NOT_VERIFIED" -> R.string.error_email_not_verified
                "AUTH_INVALID_CODE" -> R.string.error_invalid_code
                "AUTH_CODE_EXPIRED" -> R.string.error_code_expired
                "AUTH_WEAK_PASSWORD" -> R.string.error_weak_password
                "RATE_LIMITED", "AUTH_RESEND_TOO_SOON" -> R.string.error_rate_limited
                "VALIDATION_FAILED" -> R.string.error_validation
                else -> R.string.error_unexpected
            }
        }
    }

/** Language sent at registration: the app language if supported, Spanish otherwise. */
fun appLanguage(): String = AppLocale.current()
