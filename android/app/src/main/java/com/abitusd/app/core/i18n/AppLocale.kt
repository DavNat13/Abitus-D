package com.abitusd.app.core.i18n

import java.util.Locale

/** Language of the app content: the language chosen in the app (AppLanguage sets the default locale). */
object AppLocale {
    private val SUPPORTED = setOf("es", "en")

    fun current(): String = Locale.getDefault().language.takeIf { it in SUPPORTED } ?: "es"
}
