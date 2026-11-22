package com.abitusd.app.core.i18n

import android.content.Context
import android.content.res.Configuration
import androidx.core.content.edit
import java.util.Locale

/**
 * Language chosen inside the app. Spanish by default whatever the device language;
 * English only when the user picks it in Profile. Applied by wrapping the base context.
 */
object AppLanguage {
    const val SPANISH = "es"
    const val ENGLISH = "en"
    val SUPPORTED = listOf(SPANISH, ENGLISH)

    private const val PREFS = "abitusd_preferences"
    private const val KEY = "app_language"

    fun saved(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null)?.takeIf { it in SUPPORTED }
            ?: SPANISH

    fun save(
        context: Context,
        language: String,
    ) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit { putString(KEY, language) }
    }

    /** Returns [base] configured with the saved language and makes it the process default. */
    fun wrap(base: Context): Context {
        val locale = Locale.forLanguageTag(saved(base))
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration).apply { setLocale(locale) }
        return base.createConfigurationContext(config)
    }
}
