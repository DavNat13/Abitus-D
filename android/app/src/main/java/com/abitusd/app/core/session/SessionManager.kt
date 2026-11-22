package com.abitusd.app.core.session

import com.abitusd.app.core.network.dto.TokensDto
import com.abitusd.app.core.security.SessionStore
import com.abitusd.app.core.security.StoredSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

sealed interface SessionState {
    data object Loading : SessionState

    data object SignedOut : SessionState

    data class SignedIn(
        val userId: String,
    ) : SessionState
}

/** Single source of the session state. The token value decides the start destination. */
@Singleton
class SessionManager
    @Inject
    constructor(
        private val store: SessionStore,
    ) {
        private val mutableState = MutableStateFlow<SessionState>(SessionState.Loading)
        val state: StateFlow<SessionState> = mutableState.asStateFlow()

        /** Serializes token renewals so parallel 401 responses refresh only once. */
        val refreshLock = Mutex()

        private var cached: StoredSession? = null

        suspend fun restore() {
            cached = store.read()
            mutableState.value = cached?.let { SessionState.SignedIn(it.userId) } ?: SessionState.SignedOut
        }

        suspend fun current(): StoredSession? = cached ?: store.read().also { cached = it }

        suspend fun save(tokens: TokensDto) {
            val session =
                StoredSession(
                    accessToken = tokens.accessToken,
                    refreshToken = tokens.refreshToken,
                    userId = tokens.userId,
                    deviceId = tokens.deviceId,
                )
            store.write(session)
            cached = session
            mutableState.value = SessionState.SignedIn(session.userId)
        }

        suspend fun clear() {
            store.clear()
            cached = null
            mutableState.value = SessionState.SignedOut
        }

        suspend fun installationId(): String = store.installationId()
    }
