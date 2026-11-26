package com.abitusd.app.feature.progress.data

import android.content.Context
import androidx.core.content.edit
import com.abitusd.app.feature.progress.domain.AnalysisSettings
import com.abitusd.app.feature.progress.domain.OneRmFormula
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Analysis settings on this phone (Progress card now, Settings in Phase 9); views react to changes. */
@Singleton
class AnalysisSettingsStore
    @Inject
    constructor(
        @param:ApplicationContext context: Context,
    ) {
        private val prefs = context.getSharedPreferences("abitusd_analysis", Context.MODE_PRIVATE)
        private val mutable = MutableStateFlow(read())
        val settings: StateFlow<AnalysisSettings> = mutable.asStateFlow()

        private fun read(): AnalysisSettings {
            val d = AnalysisSettings()
            return AnalysisSettings(
                formula =
                    runCatching {
                        OneRmFormula.valueOf(
                            prefs.getString(FORMULA, null) ?: "",
                        )
                    }.getOrDefault(d.formula),
                windowWeeks = prefs.getInt(WINDOW, d.windowWeeks),
                upPct = prefs.getFloat(UP, d.upPct.toFloat()).toDouble(),
                downPct = prefs.getFloat(DOWN, d.downPct.toFloat()).toDouble(),
                minWeeklySets = prefs.getInt(MIN_SETS, d.minWeeklySets),
                maxWeeklySets = prefs.getInt(MAX_SETS, d.maxWeeklySets),
            )
        }

        fun update(change: (AnalysisSettings) -> AnalysisSettings) {
            val s = change(mutable.value)
            prefs.edit {
                putString(FORMULA, s.formula.name)
                putInt(WINDOW, s.windowWeeks)
                putFloat(UP, s.upPct.toFloat())
                putFloat(DOWN, s.downPct.toFloat())
                putInt(MIN_SETS, s.minWeeklySets)
                putInt(MAX_SETS, s.maxWeeklySets)
            }
            mutable.value = s
        }

        private companion object {
            const val FORMULA = "formula"
            const val WINDOW = "window_weeks"
            const val UP = "up_pct"
            const val DOWN = "down_pct"
            const val MIN_SETS = "min_sets"
            const val MAX_SETS = "max_sets"
        }
    }
