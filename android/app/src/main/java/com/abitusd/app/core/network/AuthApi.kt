package com.abitusd.app.core.network

import com.abitusd.app.core.network.dto.AcceptedDto
import com.abitusd.app.core.network.dto.EmailRequest
import com.abitusd.app.core.network.dto.LoginRequest
import com.abitusd.app.core.network.dto.ProfileDto
import com.abitusd.app.core.network.dto.RefreshRequest
import com.abitusd.app.core.network.dto.RegisterRequest
import com.abitusd.app.core.network.dto.ResetPasswordRequest
import com.abitusd.app.core.network.dto.TokensDto
import com.abitusd.app.core.network.dto.VerifyEmailRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/** Public endpoints: no access token. */
interface PublicAuthApi {
    @POST("v1/auth/register")
    suspend fun register(
        @Body body: RegisterRequest,
    ): AcceptedDto

    @POST("v1/auth/verify-email")
    suspend fun verifyEmail(
        @Body body: VerifyEmailRequest,
    ): TokensDto

    @POST("v1/auth/resend-code")
    suspend fun resendCode(
        @Body body: EmailRequest,
    ): AcceptedDto

    @POST("v1/auth/login")
    suspend fun login(
        @Body body: LoginRequest,
    ): TokensDto

    @POST("v1/auth/refresh")
    suspend fun refresh(
        @Body body: RefreshRequest,
    ): TokensDto

    @POST("v1/auth/password/forgot")
    suspend fun forgotPassword(
        @Body body: EmailRequest,
    ): AcceptedDto

    @POST("v1/auth/password/reset")
    suspend fun resetPassword(
        @Body body: ResetPasswordRequest,
    ): AcceptedDto
}

/** Endpoints that need a session; the client adds and renews the access token. */
interface AccountApi {
    @GET("v1/me")
    suspend fun me(): ProfileDto

    @POST("v1/auth/logout")
    suspend fun logout(): AcceptedDto
}
