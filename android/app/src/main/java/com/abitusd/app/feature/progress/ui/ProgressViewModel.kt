package com.abitusd.app.feature.progress.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.core.database.progress.RangeTotals
import com.abitusd.app.core.database.progress.TrainedExercise
import com.abitusd.app.feature.progress.data.AnalysisSettingsStore
import com.abitusd.app.feature.progress.data.ExerciseAnalysis
import com.abitusd.app.feature.progress.data.ProgressRepository
import com.abitusd.app.feature.progress.domain.AnalysisSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

data class ProgressState(
    val loading: Boolean = true,
    val trained: List<TrainedExercise> = emptyList(),
    val names: Map<String, String> = emptyMap(),
    val selected: ExerciseAnalysis? = null,
    val analyses: List<ExerciseAnalysis> = emptyList(),
    val thisWeek: RangeTotals? = null,
    val lastWeek: RangeTotals? = null,
    val settings: AnalysisSettings = AnalysisSettings(),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProgressViewModel
    @Inject
    constructor(
        private val repository: ProgressRepository,
        private val store: AnalysisSettingsStore,
    ) : ViewModel() {
        private val selectedId = MutableStateFlow<String?>(null)
        private val monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        private fun ms(d: LocalDate) = d.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        private val weeks =
            combine(
                repository.totals(ms(monday), ms(monday.plusWeeks(1))),
                repository.totals(ms(monday.minusWeeks(1)), ms(monday)),
                ::Pair,
            )

        val state: StateFlow<ProgressState> =
            combine(repository.trained, store.settings, selectedId, weeks) { trained, settings, id, w ->
                Input(trained, settings, id, w)
            }.mapLatest { input ->
                val names = repository.nameMap()
                val now = System.currentTimeMillis()
                val analyses =
                    input.trained
                        .take(
                            MAX_ANALYZED,
                        ).map { repository.analyze(it, input.settings, now, names) }
                val selected =
                    (input.trained.firstOrNull { it.exerciseId == input.id } ?: input.trained.firstOrNull())
                        ?.let { t ->
                            analyses.firstOrNull { it.exerciseId == t.exerciseId }
                                ?: repository.analyze(t, input.settings, now, names)
                        }
                ProgressState(
                    false,
                    input.trained,
                    names,
                    selected,
                    analyses,
                    input.weeks.first,
                    input.weeks.second,
                    input.settings,
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), ProgressState())

        fun select(exerciseId: String) {
            selectedId.value = exerciseId
        }

        fun settings(change: (AnalysisSettings) -> AnalysisSettings) = store.update(change)

        fun isStagnant(a: ExerciseAnalysis) = repository.isStagnant(a)

        private data class Input(
            val trained: List<TrainedExercise>,
            val settings: AnalysisSettings,
            val id: String?,
            val weeks: Pair<RangeTotals, RangeTotals>,
        )

        private companion object {
            const val STOP_MS = 5_000L
            const val MAX_ANALYZED = 30
        }
    }
