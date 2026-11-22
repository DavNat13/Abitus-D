package com.abitusd.app.core.navigation

import androidx.navigation3.runtime.NavKey
import com.abitusd.app.feature.legal.LegalDocument
import kotlinx.serialization.Serializable

/** Typed navigation keys of the access flow. Saved across configuration changes. */
@Serializable data object WelcomeRoute : NavKey

@Serializable data object RegisterRoute : NavKey

@Serializable data class VerifyRoute(
    val email: String,
) : NavKey

@Serializable data class LoginRoute(
    val passwordWasReset: Boolean = false,
) : NavKey

@Serializable data object ForgotPasswordRoute : NavKey

@Serializable data class ResetPasswordRoute(
    val email: String,
) : NavKey

@Serializable data class LegalRoute(
    val document: LegalDocument,
) : NavKey

/** Typed navigation keys of the signed-in app. */
@Serializable data object MainRoute : NavKey

@Serializable data object ProfileRoute : NavKey
