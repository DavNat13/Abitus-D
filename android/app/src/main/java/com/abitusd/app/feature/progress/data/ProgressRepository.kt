package com.abitusd.app.feature.progress.data

import android.util.Log
import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.database.progress.TrainedExercise
import com.abitusd.app.feature.library.domain.ExerciseNames
import com.abitusd.app.feature.onboarding.data.AthleteRepository
import com.abitusd.app.feature.progress.domain.AnalysisSettings
import com.abitusd.app.feature.progress.domain.ExerciseProgress
import com.abitusd.app.feature.progress.domain.Records
import com.abitusd.app.feature.progress.domain.SessionPoint
import com.abitusd.app.feature.progress.domain.Verdict
import com.abitusd.app.feature.progress.domain.VerdictEngine
import com.abitusd.app.feature.progress.domain.VerdictKind
import com.abitusd.app.feature.progress.domain.WorkSet
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/** Everything Progress shows about one exercise. */
data class ExerciseAnalysis(
    val exerciseId: String,
    val name: String,
    val metricType: String,
    val points: List<SessionPoint>,
    val verdict: Verdict,
    val records: Records,
    val top: List<Pair<WorkSet, Double>>,
    val weeksSinceRecord: Int?,
)

@Singleton
class ProgressRepository
    @Inject
    constructor(
        database: AbitusDatabase,
        private val names: ExerciseNames,
        private val athlete: AthleteRepository,
        private val measurements: MeasurementRepository,
    ) {
        private val dao = database.progressDao()
        val trained = dao.trainedExercises()

        fun totals(
            from: Long,
            to: Long,
        ) = dao.totals(from, to)

        suspend fun nameMap(): Map<String, String> = names.all()

        /** Latest measured weight, or the athlete profile weight when nothing was measured yet. */
        suspend fun bodyweight(): Double? = measurements.latestWeight() ?: athlete.profile.first()?.weightKg

        suspend fun analyze(
            exercise: TrainedExercise,
            settings: AnalysisSettings,
            now: Long,
            nameMap: Map<String, String>,
        ): ExerciseAnalysis {
            val started = System.nanoTime()
            val sets = dao.workSets(exercise.exerciseId)
            val bw = bodyweight()
            val type = exercise.metricType
            val points = ExerciseProgress.sessionPoints(sets, type, bw, settings.formula)
            val result =
                ExerciseAnalysis(
                    exercise.exerciseId,
                    nameMap[exercise.exerciseId].orEmpty(),
                    type,
                    points,
                    VerdictEngine.evaluate(points, now, settings),
                    ExerciseProgress.records(sets, type, bw, settings.formula),
                    ExerciseProgress.topSets(sets, type, bw, settings.formula),
                    ExerciseProgress.weeksSinceRecord(points, now),
                )
            Log.i(TAG, "analyze sets=${sets.size} ms=${(System.nanoTime() - started) / NANOS_PER_MS}")
            return result
        }

        /** Exercises without progress: stable or falling and no record for [STALE_WEEKS] weeks. */
        fun isStagnant(a: ExerciseAnalysis): Boolean =
            a.verdict.kind in setOf(VerdictKind.STABLE, VerdictKind.REGRESS) && (a.weeksSinceRecord ?: 0) >= STALE_WEEKS

        private companion object {
            const val TAG = "AbitusProgress"
            const val NANOS_PER_MS = 1_000_000
            const val STALE_WEEKS = 4
        }
    }
