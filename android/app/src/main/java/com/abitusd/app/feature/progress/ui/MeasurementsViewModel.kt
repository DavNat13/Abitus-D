package com.abitusd.app.feature.progress.ui

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.core.database.progress.BodyMeasurementEntity
import com.abitusd.app.feature.onboarding.data.AthleteRepository
import com.abitusd.app.feature.progress.data.MeasurementRepository
import com.abitusd.app.feature.progress.domain.BodyTrends
import com.abitusd.app.feature.progress.domain.DayValue
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class MeasurementsState(
    val rows: List<BodyMeasurementEntity> = emptyList(),
    val weights: List<DayValue> = emptyList(),
    val smoothed: List<DayValue> = emptyList(),
    val rateKgWeek: Double? = null,
    val heightM: Double? = null,
    val goalKg: Double? = null,
)

@HiltViewModel
class MeasurementsViewModel
    @Inject
    constructor(
        @param:ApplicationContext context: Context,
        private val repository: MeasurementRepository,
        athlete: AthleteRepository,
    ) : ViewModel() {
        private val prefs = context.getSharedPreferences("abitusd_analysis", Context.MODE_PRIVATE)
        private val goal = MutableStateFlow(prefs.getFloat(GOAL, 0f).toDouble().takeIf { it > 0 })

        val state: StateFlow<MeasurementsState> =
            combine(repository.all, athlete.profile, goal) { rows, profile, g ->
                val weights =
                    rows.mapNotNull { r ->
                        r.weightKg?.let { DayValue(LocalDate.parse(r.measuredOn).toEpochDay(), it) }
                    }
                MeasurementsState(
                    rows,
                    weights,
                    BodyTrends.smoothed(weights),
                    BodyTrends.weeklyRate(weights),
                    profile?.heightM,
                    g,
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), MeasurementsState())

        fun save(row: BodyMeasurementEntity) {
            viewModelScope.launch { repository.save(row) }
        }

        fun delete(id: String) {
            viewModelScope.launch { repository.delete(id) }
        }

        fun setGoal(kg: Double?) {
            prefs.edit { putFloat(GOAL, (kg ?: 0.0).toFloat()) }
            goal.value = kg?.takeIf { it > 0 }
        }

        private companion object {
            const val STOP_MS = 5_000L
            const val GOAL = "goal_weight_kg"
        }
    }
