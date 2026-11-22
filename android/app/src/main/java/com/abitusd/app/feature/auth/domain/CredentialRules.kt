package com.abitusd.app.feature.auth.domain

/** Client checks for comfort only; the server validates everything again (golden rule 19). */
object CredentialRules {
    const val MIN_PASSWORD_LENGTH = 10
    private const val MAX_EMAIL_LENGTH = 254
    private const val STRONG_LENGTH = 14
    private const val STRONG_CLASSES = 3
    private val EMAIL = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

    fun isValidEmail(email: String): Boolean {
        val trimmed = email.trim()
        return trimmed.length <= MAX_EMAIL_LENGTH && EMAIL.matches(trimmed)
    }

    fun meetsMinimum(password: String): Boolean = password.codePointCount(0, password.length) >= MIN_PASSWORD_LENGTH

    fun strength(password: String): PasswordStrength {
        if (!meetsMinimum(password)) return PasswordStrength.WEAK
        val classes =
            listOf(
                password.any(Char::isLowerCase),
                password.any(Char::isUpperCase),
                password.any(Char::isDigit),
                password.any { !it.isLetterOrDigit() },
            ).count { it }
        return when {
            password.length >= STRONG_LENGTH && classes >= STRONG_CLASSES -> PasswordStrength.STRONG
            classes >= 2 -> PasswordStrength.MEDIUM
            else -> PasswordStrength.WEAK
        }
    }
}

enum class PasswordStrength { WEAK, MEDIUM, STRONG }
