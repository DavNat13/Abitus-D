package com.abitusd.app.core.security

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Session tokens kept on the device. Never logged. */
@Serializable
data class StoredSession(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val deviceId: String,
)

private val Context.sessionDataStore by preferencesDataStore(name = "session")

/** Persists the session encrypted with [TokenCipher] and the installation id in plain text. */
@Singleton
class SessionStore
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
        private val cipher: TokenCipher,
    ) {
        private val json = Json { ignoreUnknownKeys = true }

        suspend fun read(): StoredSession? =
            context.sessionDataStore.data
                .first()[SESSION_KEY]
                ?.let(cipher::decrypt)
                ?.let { plain -> runCatching { json.decodeFromString<StoredSession>(plain) }.getOrNull() }

        suspend fun write(session: StoredSession) {
            val encrypted = cipher.encrypt(json.encodeToString(StoredSession.serializer(), session))
            context.sessionDataStore.edit { it[SESSION_KEY] = encrypted }
        }

        suspend fun clear() {
            context.sessionDataStore.edit { it.remove(SESSION_KEY) }
        }

        /** Random id generated once per installation; identifies this device to the server. */
        suspend fun installationId(): String {
            context.sessionDataStore.data
                .first()[INSTALLATION_KEY]
                ?.let { return it }
            val id = UUID.randomUUID().toString()
            context.sessionDataStore.edit { it[INSTALLATION_KEY] = id }
            return id
        }

        private companion object {
            val SESSION_KEY = stringPreferencesKey("session_encrypted")
            val INSTALLATION_KEY = stringPreferencesKey("installation_id")
        }
    }
