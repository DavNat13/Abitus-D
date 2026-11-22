package com.abitusd.app.feature.library.domain

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Disciplines whose exercise names are shown in English whatever the app language
 * (for example calisthenics: Pull-Up, Muscle-Up). Explanations keep the app language.
 */
@Singleton
class EnglishNamesStore
    @Inject
    constructor(
        @param:ApplicationContext context: Context,
    ) {
        private val prefs = context.getSharedPreferences("abitusd_preferences", Context.MODE_PRIVATE)
        private val mutable = MutableStateFlow(read())
        val disciplines: StateFlow<Set<Int>> = mutable.asStateFlow()

        fun toggle(disciplineId: Int) {
            val next = mutable.value.let { if (disciplineId in it) it - disciplineId else it + disciplineId }
            prefs.edit { putStringSet(KEY, next.map(Int::toString).toSet()) }
            mutable.value = next
        }

        private fun read(): Set<Int> =
            prefs
                .getStringSet(KEY, emptySet())
                .orEmpty()
                .mapNotNull(String::toIntOrNull)
                .toSet()

        private companion object {
            const val KEY = "english_name_disciplines"
        }
    }
