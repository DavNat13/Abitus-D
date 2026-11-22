package com.abitusd.app.core.network.dto

import kotlinx.serialization.Serializable

/** Wire contracts of /v1/auth (07-BACKEND.md). Field names match the backend schemas. */
@Serializable
data class DeviceDto(
    val installationId: String,
    val name: String,
    val appVersion: String,
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val locale: String,
    val ageConfirmed: Boolean,
    val termsVersion: String,
    val privacyVersion: String,
)

@Serializable
data class VerifyEmailRequest(
    val email: String,
    val code: String,
    val device: DeviceDto,
)

@Serializable
data class EmailRequest(
    val email: String,
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val device: DeviceDto,
)

@Serializable
data class RefreshRequest(
    val refreshToken: String,
)

@Serializable
data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val newPassword: String,
)

@Serializable
data class TokensDto(
    val accessToken: String,
    val accessTokenExpiresIn: Int,
    val refreshToken: String,
    val userId: String,
    val deviceId: String,
)

@Serializable
data class AcceptedDto(
    val status: String,
)

@Serializable
data class ProblemDto(
    val status: Int,
    val code: String,
)

@Serializable
data class ProfileDto(
    val id: String,
    val email: String,
    val displayName: String? = null,
    val locale: String,
)
