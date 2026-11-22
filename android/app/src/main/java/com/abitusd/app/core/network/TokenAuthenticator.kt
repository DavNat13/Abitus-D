package com.abitusd.app.core.network

import com.abitusd.app.core.network.dto.RefreshRequest
import com.abitusd.app.core.session.SessionManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

private const val AUTHORIZATION = "Authorization"
private const val BEARER = "Bearer "

/** Adds the current access token to authenticated requests. */
class AuthInterceptor
    @Inject
    constructor(
        private val sessions: SessionManager,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val token = runBlocking { sessions.current()?.accessToken }
            val request = chain.request()
            if (token == null) return chain.proceed(request)
            return chain.proceed(request.newBuilder().header(AUTHORIZATION, BEARER + token).build())
        }
    }

/**
 * On 401, renews the session once under a lock (09-FRONTEND-ANDROID.md). If another request
 * already renewed it, retries with the new token. If renewal fails, ends the local session.
 */
class TokenAuthenticator
    @Inject
    constructor(
        private val sessions: SessionManager,
        private val publicApi: Provider<PublicAuthApi>,
    ) : Authenticator {
        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? {
            if (response.priorResponse != null) return null
            val sent = response.request.header(AUTHORIZATION)?.removePrefix(BEARER)
            return runBlocking {
                sessions.refreshLock.withLock {
                    val current = sessions.current() ?: return@withLock null
                    if (current.accessToken != sent) return@withLock withToken(response.request, current.accessToken)
                    when (val result = apiCall { publicApi.get().refresh(RefreshRequest(current.refreshToken)) }) {
                        is ApiResult.Success -> {
                            sessions.save(result.value)
                            withToken(response.request, result.value.accessToken)
                        }

                        is ApiResult.Problem -> {
                            sessions.clear()
                            null
                        }

                        else -> {
                            null
                        }
                    }
                }
            }
        }

        private fun withToken(
            request: Request,
            token: String,
        ): Request = request.newBuilder().header(AUTHORIZATION, BEARER + token).build()
    }
