package com.abitusd.app.core.i18n

import java.util.Locale

/** Language of the app content: the device or app language if supported, Spanish otherwise. */
object AppLocale {
    private val SUPPORTED = setOf("es", "en")

    fun current(): String = Locale.getDefault().language.takeIf { it in SUPPORTED } ?: "es"
}
