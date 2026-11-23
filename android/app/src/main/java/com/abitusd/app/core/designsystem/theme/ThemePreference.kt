package com.abitusd.app.core.designsystem.theme

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.edit
import com.abitusd.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Theme chosen in Profile (05b): light by default, dark, or follow the system. */
enum class ThemeMode(
    @param:StringRes val label: Int,
) {
    LIGHT(R.string.theme_light),
    DARK(R.string.theme_dark),
    SYSTEM(R.string.theme_system),
}

/** Remembers the theme on this device. */
@Singleton
class ThemePreference
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val prefs = context.getSharedPreferences("abitusd_preferences", Context.MODE_PRIVATE)
        private val mutable =
            MutableStateFlow(ThemeMode.entries.firstOrNull { it.name == prefs.getString(KEY, null) } ?: ThemeMode.LIGHT)
        val mode: StateFlow<ThemeMode> = mutable.asStateFlow()

        fun set(value: ThemeMode) {
            prefs.edit { putString(KEY, value.name) }
            mutable.value = value
        }

        private companion object {
            const val KEY = "theme_mode"
        }
    }
