package com.abitusd.app.feature.auth.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CredentialRulesTest {
    @Test
    fun emailValidation() {
        assertTrue(CredentialRules.isValidEmail(" ana.perez@example.cl "))
        assertFalse(CredentialRules.isValidEmail("ana.perez@"))
        assertFalse(CredentialRules.isValidEmail("ana perez@example.cl"))
        assertFalse(CredentialRules.isValidEmail("sin-arroba.cl"))
    }

    @Test
    fun minimumLengthCountsCharactersNotBytes() {
        assertFalse(CredentialRules.meetsMinimum("Corto-123"))
        assertTrue(CredentialRules.meetsMinimum("Señal-1234"))
    }

    @Test
    fun strengthLevels() {
        assertEquals(PasswordStrength.WEAK, CredentialRules.strength("abc"))
        assertEquals(PasswordStrength.WEAK, CredentialRules.strength("abcdefghijk"))
        assertEquals(PasswordStrength.MEDIUM, CredentialRules.strength("abcdefgh12"))
        assertEquals(PasswordStrength.STRONG, CredentialRules.strength("Rowing-Squat-2026"))
    }
}
