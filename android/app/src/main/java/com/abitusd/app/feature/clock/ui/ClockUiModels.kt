package com.abitusd.app.feature.clock.ui

import com.abitusd.app.core.database.clock.TimerProfileEntity
import com.abitusd.app.core.database.planning.RoutineEntity
import com.abitusd.app.feature.clock.domain.ClockMode
import com.abitusd.app.feature.clock.domain.ClockPresets
import com.abitusd.app.feature.clock.domain.ClockRun
import com.abitusd.app.feature.clock.domain.ClockSpec
import com.abitusd.app.feature.clock.domain.ClockStatus

/** What will start: a built-in preset (by key) or a saved profile (by id), with edited values. */
data class ClockDraft(
    val presetKey: String? = "tabata",
    val profileId: String? = null,
    val name: String = "",
    val mode: ClockMode = ClockMode.TABATA,
    val spec: ClockSpec = ClockPresets.all.first { it.key == "tabata" }.spec,
)

data class ClockUiState(
    val run: ClockRun? = null,
    val status: ClockStatus? = null,
    val draft: ClockDraft = ClockDraft(),
    val profiles: List<TimerProfileEntity> = emptyList(),
    val silent: Boolean = false,
    val keepOn: Boolean = false,
    val routines: List<RoutineEntity> = emptyList(),
)

/** Exercise name handed over by the active session before opening the clock. */
object ClockPending {
    var label: String? = null
}
