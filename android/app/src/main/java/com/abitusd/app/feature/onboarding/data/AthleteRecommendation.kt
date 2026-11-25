package com.abitusd.app.feature.onboarding.data

import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.athlete.AthleteProfileEntity
import com.abitusd.app.feature.train.data.TemplateInstaller
import com.abitusd.app.feature.train.domain.PlanTemplate
import com.abitusd.app.feature.train.domain.TemplateCatalog
import javax.inject.Inject

/** Only existing catalog templates; missing catalog or equipment never becomes an invented plan. */
class AthleteRecommendation
    @Inject
    constructor(
        private val database: AbitusDatabase,
        private val installer: TemplateInstaller,
    ) {
        suspend fun suggest(p: AthleteProfileEntity): PlanTemplate? {
            val days = p.daysPerWeek
            if (days == null || !eligible(p)) return null
            val equipment = p.equipment.split(',').toSet()
            val candidates =
                TemplateCatalog.all
                    .filter {
                        it.schedule
                            .map { day -> day.first }
                            .distinct()
                            .size <= days
                    }.sortedBy { if (it.key == preferred(p)) 0 else 1 }
            var found: PlanTemplate? = null
            for (template in candidates) {
                if (fits(template, equipment)) {
                    found = template
                    break
                }
            }
            return found
        }

        private suspend fun fits(
            template: PlanTemplate,
            equipment: Set<String>,
        ): Boolean {
            val slugs =
                template.routines
                    .flatMap { it.exercises }
                    .map { it.slug }
                    .distinct()
            val ids = database.libraryDao().idsBySlug(slugs)
            if (ids.size != slugs.size) return false
            val needed =
                database
                    .periodizationDao()
                    .equipmentSlugs(ids.map { it.id })
                    .map { it.slug }
                    .toSet()
            return equipment.containsAll(needed)
        }

        suspend fun install(template: PlanTemplate): Boolean = installer.install(template)

        private fun eligible(p: AthleteProfileEntity): Boolean =
            p.healthLimited != true && p.injuries.isBlank() && p.goal in GOALS

        private fun preferred(p: AthleteProfileEntity): String =
            when {
                p.goal == "hypertrophy" && (p.daysPerWeek ?: 0) >= PPL_DAYS -> "ppl"
                (p.sessionDurationS ?: Int.MAX_VALUE) <= EXPRESS_SECONDS -> "express_20"
                p.place == "home" -> "home_no_equipment"
                else -> "full_body_3"
            }

        private companion object {
            val GOALS = setOf("general", "strength", "hypertrophy")
            const val PPL_DAYS = 6
            const val EXPRESS_SECONDS = 1200
        }
    }
