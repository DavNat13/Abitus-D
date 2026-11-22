package com.abitusd.app.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AES-256-GCM with a non-exportable key in the Android Keystore. Used to store session
 * tokens on disk (09-FRONTEND-ANDROID.md). Replaces the deprecated androidx.security.crypto.
 */
@Singleton
class TokenCipher
    @Inject
    constructor() {
        private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

        fun encrypt(plain: String): String {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key())
            val payload = cipher.iv + cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
            return Base64.encodeToString(payload, Base64.NO_WRAP)
        }

        /** Returns null if the data cannot be decrypted (for example, the key was reset). */
        fun decrypt(encoded: String): String? =
            runCatching {
                val payload = Base64.decode(encoded, Base64.NO_WRAP)
                val cipher = Cipher.getInstance(TRANSFORMATION)
                val iv = payload.copyOfRange(0, IV_BYTES)
                cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(TAG_BITS, iv))
                String(cipher.doFinal(payload.copyOfRange(IV_BYTES, payload.size)), Charsets.UTF_8)
            }.getOrNull()

        private fun key(): SecretKey {
            (keyStore.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }
            val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            generator.init(
                KeyGenParameterSpec
                    .Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(KEY_BITS)
                    .build(),
            )
            return generator.generateKey()
        }

        private companion object {
            const val ANDROID_KEYSTORE = "AndroidKeyStore"
            const val ALIAS = "abitusd_session_tokens"
            const val TRANSFORMATION = "AES/GCM/NoPadding"
            const val IV_BYTES = 12
            const val TAG_BITS = 128
            const val KEY_BITS = 256
        }
    }
