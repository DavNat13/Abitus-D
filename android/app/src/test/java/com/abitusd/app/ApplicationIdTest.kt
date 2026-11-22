package com.abitusd.app

import org.junit.Assert.assertEquals
import org.junit.Test

class ApplicationIdTest {
    @Test
    fun applicationIdIsFixed() {
        // Changing the applicationId breaks updates of installed APKs.
        assertEquals("com.abitusd.app", BuildConfig.APPLICATION_ID)
    }
}
