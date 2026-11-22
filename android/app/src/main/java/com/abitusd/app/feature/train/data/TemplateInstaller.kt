package com.abitusd.app.feature.train.data

import android.content.Context
import com.abitusd.app.core.database.catalog.LibraryDao
import com.abitusd.app.feature.train.domain.PlanTemplate
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Creates the routines (and program) of a template from the local catalog. */
@Singleton
class TemplateInstaller
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
        private val library: LibraryDao,
        private val routines: RoutineRepository,
        private val programs: ProgramRepository,
    ) {
        /** Returns false when the catalog has not been downloaded yet. */
        suspend fun install(template: PlanTemplate): Boolean {
            val slugs = template.routines.flatMap { r -> r.exercises.map { it.slug } }.distinct()
            val ids = library.idsBySlug(slugs).associate { it.slug to it.id }
            if (ids.size < slugs.size) return false
            val routineIds =
                template.routines.map { tr ->
                    val routine = RoutineRepository.newRoutine(context.getString(tr.name))
                    val exercises =
                        tr.exercises.map { te ->
                            RoutineRepository
                                .newExercise(routine.id, ids.getValue(te.slug), te.restSeconds)
                                .copy(
                                    targetSets = te.sets,
                                    targetRepsMin = te.repsMin,
                                    targetRepsMax = te.repsMax,
                                    groupId = te.group,
                                )
                        }
                    routines.save(routine.copy(folder = context.getString(template.name)), exercises)
                    routine.id
                }
            if (template.weeks > 0) {
                val program = ProgramRepository.newProgram(context.getString(template.name), template.weeks)
                val days =
                    (0 until template.weeks).flatMap { week ->
                        template.schedule.map { (day, routineIndex) ->
                            ProgramRepository.newDay(program.id, week, day, routineIds[routineIndex])
                        }
                    }
                programs.save(program, days)
            }
            return true
        }
    }
