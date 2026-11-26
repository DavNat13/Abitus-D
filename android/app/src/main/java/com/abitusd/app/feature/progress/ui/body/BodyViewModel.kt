package com.abitusd.app.feature.progress.ui.body

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.core.database.AbitusDatabase
import com.abitusd.app.core.i18n.AppLocale
import com.abitusd.app.feature.progress.data.AnalysisSettingsStore
import com.abitusd.app.feature.train.data.VolumeCalculator
import com.abitusd.app.feature.train.domain.PlannedSets
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val WEEKS_IN_MONTH = 4
private const val WEEKS_IN_YEAR = 52

enum class BodyPeriod(
    val weeks: Int,
) {
    WEEK(1),
    MONTH(WEEKS_IN_MONTH),
    YEAR(WEEKS_IN_YEAR),
}

/** Average sets per week of one muscle in the period and in the period before it. */
data class MuscleLoad(
    val name: String,
    val perWeek: Double,
    val previousPerWeek: Double,
)

data class BodyState(
    val period: BodyPeriod = BodyPeriod.WEEK,
    val byKey: Map<String, MuscleLoad> = emptyMap(),
    val minSets: Int = 10,
    val maxSets: Int = 20,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BodyViewModel
    @Inject
    constructor(
        database: AbitusDatabase,
        private val volume: VolumeCalculator,
        settings: AnalysisSettingsStore,
    ) : ViewModel() {
        private val dao = database.progressDao()
        private val period = MutableStateFlow(BodyPeriod.WEEK)

        val state: StateFlow<BodyState> =
            combine(period, settings.settings, dao.trainedExercises(), ::Triple)
                .mapLatest { (p, s, _) ->
                    val now = System.currentTimeMillis()
                    val span = p.weeks * WEEK_MS
                    val current = perMuscle(now - span, now, p.weeks)
                    val previous = perMuscle(now - 2 * span, now - span, p.weeks)
                    val keys = dao.muscleKeys().associate { it.id to it.svgKey }
                    val byKey =
                        current.keys
                            .union(previous.keys)
                            .mapNotNull { id ->
                                val name = current[id]?.first ?: previous[id]?.first.orEmpty()
                                keys[id]?.let {
                                    it to
                                        MuscleLoad(name, current[id]?.second ?: 0.0, previous[id]?.second ?: 0.0)
                                }
                            }.toMap()
                    BodyState(p, byKey, s.minWeeklySets, s.maxWeeklySets)
                }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), BodyState())

        private suspend fun perMuscle(
            from: Long,
            to: Long,
            weeks: Int,
        ): Map<Int, Pair<String, Double>> {
            val sets = dao.setsPerExercise(from, to).map { PlannedSets(it.exerciseId, it.sets) }
            return volume.byMuscle(sets, AppLocale.current()).associate { it.id to (it.name to it.sets / weeks) }
        }

        fun setPeriod(p: BodyPeriod) {
            period.value = p
        }

        private companion object {
            const val WEEK_MS = 7 * 86_400_000L
            const val STOP_MS = 5_000L
        }
    }
