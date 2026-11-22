package com.abitusd.app.feature.library.domain

import com.abitusd.app.core.database.catalog.LibraryDao
import com.abitusd.app.core.i18n.AppLocale
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** Display names of every exercise by id, honoring the English-names preference. */
class ExerciseNames
    @Inject
    constructor(
        private val library: LibraryDao,
        private val englishNames: EnglishNamesStore,
    ) {
        suspend fun all(): Map<String, String> {
            val english = englishNames.disciplines.value.toList()
            val rows =
                library
                    .exercises(
                        AppLocale.current(),
                        "",
                        null,
                        null,
                        null,
                        null,
                        englishDisciplines = english,
                    ).first()
            return rows.associate { it.id to it.name }
        }
    }
