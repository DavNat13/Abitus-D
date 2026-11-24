package com.abitusd.app.feature.workout.data

import com.abitusd.app.core.database.planning.BlockPhase
import com.abitusd.app.core.database.planning.PeriodizationDao
import com.abitusd.app.core.database.planning.Progression
import com.abitusd.app.core.database.planning.RoutineExerciseEntity
import com.abitusd.app.core.database.session.SetType
import com.abitusd.app.core.database.session.WorkoutHistoryDao
import com.abitusd.app.feature.library.domain.EquipmentProfileStore
import com.abitusd.app.feature.train.data.BlockRepository
import com.abitusd.app.feature.train.data.ProgramRepository
import com.abitusd.app.feature.train.domain.BlockPlan
import com.abitusd.app.feature.train.domain.BlockWeek
import com.abitusd.app.feature.train.domain.ExerciseSwap
import com.abitusd.app.feature.train.domain.ProgressionRule
import com.abitusd.app.feature.train.domain.SwapCandidate
import com.abitusd.app.feature.train.domain.Target
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/** Planned exercises after the block progression and the swaps, plus where the week falls. */
data class SessionPlan(
    val exercises: List<RoutineExerciseEntity>,
    val week: BlockWeek?,
    val swaps: Map<String, String>,
    val totalWeeks: Int = 0,
    val unfit: List<String> = emptyList(),
)

/** Applies the active program's block to a routine before a session starts (10f Fase 4b). */
@Singleton
class SessionPlanner
    @Inject
    constructor(
        private val programs: ProgramRepository,
        private val blocks: BlockRepository,
        private val dao: PeriodizationDao,
        private val history: WorkoutHistoryDao,
        private val equipment: EquipmentProfileStore,
    ) {
        suspend fun plan(
            planned: List<RoutineExerciseEntity>,
            travel: Boolean,
            today: LocalDate = LocalDate.now(),
        ): SessionPlan {
            val (week, total, afterTest) = currentWeek(today)
            val ids = planned.map { it.exerciseId }
            val substitutions = week?.let { blocks.substitutions(it.block.id) }.orEmpty()
            val (swaps, unfit) = swaps(ids, substitutions, travel)
            val last = lastSets(ids)
            val exercises =
                planned.map { e ->
                    val adjusted = adjust(e, week, last[e.exerciseId].orEmpty(), afterTest)
                    swaps[e.exerciseId]?.let { adjusted.copy(exerciseId = it, targetLoadKg = null) } ?: adjusted
                }
            return SessionPlan(exercises, week, swaps, total, unfit)
        }

        private suspend fun currentWeek(today: LocalDate): Triple<BlockWeek?, Int, Boolean> {
            val program =
                programs.programs().first().firstOrNull { it.isActive && it.startDate != null }
                    ?: return Triple(null, 0, false)
            val specs = blocks.blocksOnce(program.id).map(BlockRepository::spec)
            val start = LocalDate.parse(program.startDate).toEpochDay()
            val week = BlockPlan.at(specs, BlockPlan.programWeek(start, today.toEpochDay()))
            val afterTest =
                week != null && specs.any { it.phase == BlockPhase.TEST && it.position < week.block.position }
            return Triple(week, BlockPlan.totalWeeks(specs), afterTest)
        }

        private fun adjust(
            e: RoutineExerciseEntity,
            week: BlockWeek?,
            last: List<Pair<Int, Double>>,
            afterTest: Boolean,
        ): RoutineExerciseEntity {
            if (week == null) return e
            val reps = e.targetRepsMax ?: e.targetRepsMin
            val tested = if (afterTest && reps != null) ProgressionRule.afterTest(last, reps) else null
            val base = Target(e.targetSets, e.targetRepsMin, e.targetRepsMax, tested ?: e.targetLoadKg, e.targetRpe)
            val lastReps = last.map { it.first }
            val t = ProgressionRule.apply(base, week)
            val double = week.block.progression == Progression.DOUBLE && week.block.phase != BlockPhase.DELOAD
            val load =
                if (double) {
                    ProgressionRule.doubleProgression(
                        lastReps,
                        t.repsMax,
                        t.loadKg,
                        week.block.step,
                    )
                } else {
                    t.loadKg
                }
            return e.copy(targetSets = t.sets, targetLoadKg = load, targetRpe = t.rpe)
        }

        private suspend fun swaps(
            ids: List<String>,
            substitutions: Map<String, String>,
            travel: Boolean,
        ): Pair<Map<String, String>, List<String>> {
            val profile = equipment.profile.value
            val available = profile.allowed?.takeIf { travel }
            if (substitutions.isEmpty() && available == null) return emptyMap<String, String>() to emptyList()
            val chosen = ids.map { substitutions[it] ?: it }
            val related = dao.swapRelations(chosen).groupBy({ it.exerciseId }, { it.relatedId })
            val all = (chosen + related.values.flatten()).distinct()
            val slugs = dao.equipmentSlugs(all).groupBy({ it.exerciseId }, { it.slug })
            val catalog = all.associateWith { SwapCandidate(it, slugs[it].orEmpty().toSet()) }
            val swaps = ExerciseSwap.resolve(ids, substitutions, catalog, related, available, profile.excludes)
            val unfit =
                available
                    ?.let { set ->
                        ids.map { swaps[it] ?: it }.filterNot { ExerciseSwap.fits(catalog[it], set, profile.excludes) }
                    }.orEmpty()
            return swaps to unfit
        }

        /** Reps and load of the normal sets of each exercise's most recent session. */
        private suspend fun lastSets(ids: List<String>): Map<String, List<Pair<Int, Double>>> =
            history
                .completedSetsOf(ids, "")
                .filter { it.setType == SetType.NORMAL }
                .groupBy { it.exerciseId }
                .mapValues { (_, sets) ->
                    val latest = sets.maxBy { it.completedAt ?: 0 }.sessionExerciseId
                    sets.filter { it.sessionExerciseId == latest }.mapNotNull { s ->
                        s.reps?.let {
                            it to
                                (s.loadKg ?: 0.0)
                        }
                    }
                }
    }
