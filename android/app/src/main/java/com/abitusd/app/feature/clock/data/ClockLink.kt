package com.abitusd.app.feature.clock.data

import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.sync.SyncScheduler
import com.abitusd.app.feature.clock.domain.ClockPhase
import com.abitusd.app.feature.clock.domain.ClockSpec
import com.abitusd.app.feature.clock.domain.PhaseKind
import com.abitusd.app.feature.clock.ui.ClockLabels
import com.abitusd.app.feature.library.domain.ExerciseNames
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Joins the clock to training (11e): a routine becomes a timer sequence with exercise names, and
 * stopwatch laps are added to the notes of the active session.
 */
@Singleton
class ClockLink
    @Inject
    constructor(
        private val database: AbitusDatabase,
        private val names: ExerciseNames,
        private val scheduler: SyncScheduler,
    ) {
        val routines = database.planningDao().routines()

        /** One work phase per set (by time or by reps) labeled with the exercise, then its rest. */
        suspend fun routineSpec(routineId: String): ClockSpec {
            val all = names.all()
            val phases = mutableListOf<ClockPhase>()
            for (row in database.planningDao().routineExercisesOnce(routineId).sortedBy { it.position }) {
                val label = all[row.exerciseId]?.take(MAX_LABEL)
                repeat(row.targetSets ?: 1) {
                    phases +=
                        row.targetDurationS?.let { ClockPhase(PhaseKind.WORK, it, label = label) }
                            ?: ClockPhase(
                                PhaseKind.WORK,
                                1,
                                reps =
                                    row.targetRepsMax ?: row.targetRepsMin ?: DEFAULT_REPS,
                                label = label,
                            )
                    phases += ClockPhase(PhaseKind.REST, row.restSeconds ?: DEFAULT_REST_S)
                }
            }
            return ClockSpec(
                phases.dropLast(1).take(MAX_PHASES).ifEmpty { listOf(ClockPhase(PhaseKind.WORK, DEFAULT_REST_S)) },
            )
        }

        /** Appends the laps to the active session notes; nothing happens without a session. */
        suspend fun saveLaps(
            name: String,
            lapsMs: List<Long>,
        ) {
            val dao = database.sessionDao()
            val session = dao.active()?.let { dao.session(it.sessionId) } ?: return
            val splits =
                lapsMs.mapIndexed {
                    i,
                    ms,
                    ->
                    ClockLabels.time(ms - (lapsMs.getOrNull(i - 1) ?: 0L), tenths = true)
                }
            val line = "$name: ${splits.joinToString(", ")}"
            val notes = listOf(session.notes, line).filter { it.isNotBlank() }.joinToString("\n").take(MAX_NOTES)
            val at = maxOf(System.currentTimeMillis(), session.updatedAt + 1)
            dao.upsertSessions(listOf(session.copy(notes = notes, updatedAt = at, dirty = true)))
            scheduler.requestSync()
        }

        private companion object {
            const val MAX_LABEL = 60
            const val MAX_PHASES = 50
            const val DEFAULT_REPS = 10
            const val DEFAULT_REST_S = 90
            const val MAX_NOTES = 2000
        }
    }
