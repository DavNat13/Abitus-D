package com.abitusd.app.feature.clock.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.core.database.clock.TimerProfileEntity
import com.abitusd.app.feature.clock.data.ClockController
import com.abitusd.app.feature.clock.data.ClockProfiles
import com.abitusd.app.feature.clock.domain.ClockEngine
import com.abitusd.app.feature.clock.domain.ClockMode
import com.abitusd.app.feature.clock.domain.ClockPresets
import com.abitusd.app.feature.clock.domain.ClockRun
import com.abitusd.app.feature.clock.domain.ClockSpec
import com.abitusd.app.feature.clock.domain.ClockStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

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
)

@HiltViewModel
class ClockViewModel
    @Inject
    constructor(
        private val controller: ClockController,
        private val profiles: ClockProfiles,
    ) : ViewModel() {
        private val draft = MutableStateFlow(ClockDraft())
        private val silent = MutableStateFlow(controller.store.silent to controller.store.keepOn)
        private val ticker =
            flow {
                while (true) {
                    emit(System.currentTimeMillis())
                    delay(TICK_MS)
                }
            }

        val state: StateFlow<ClockUiState> =
            combine(controller.store.run, ticker, draft, profiles.all, silent) { run, now, d, p, s ->
                ClockUiState(run, run?.let { ClockEngine.status(it, now) }, d, p, s.first, s.second)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), ClockUiState())

        init {
            controller.store.refresh()
        }

        fun choosePreset(key: String) {
            val preset = ClockPresets.all.first { it.key == key }
            draft.value = ClockDraft(key, null, "", preset.mode, preset.spec)
        }

        fun chooseProfile(profile: TimerProfileEntity) {
            val spec = profiles.spec(profile) ?: return
            draft.value = ClockDraft(null, profile.id, profile.name, ClockMode.of(profile.mode), spec)
        }

        fun edit(spec: ClockSpec) {
            draft.value = draft.value.copy(spec = spec)
        }

        fun start(name: String) {
            val d = draft.value
            controller.start(ClockRun(name, d.mode.key, d.spec, System.currentTimeMillis()))
        }

        /** Applies an engine change (pause, resume, +15 s, skip, lap) and re-arms the alarms. */
        fun act(change: (ClockRun, Long) -> ClockRun) = controller.update(change)

        fun stop() = controller.stop()

        fun setSignals(
            quiet: Boolean,
            keepOn: Boolean,
        ) {
            controller.store.silent = quiet
            controller.store.keepOn = keepOn
            silent.value = quiet to keepOn
        }

        fun saveProfile(name: String) {
            val d = draft.value
            viewModelScope.launch { profiles.save(name, d.mode, d.spec) }
        }

        fun deleteProfile(id: String) {
            viewModelScope.launch { profiles.delete(id) }
            if (draft.value.profileId == id) choosePreset("tabata")
        }

        private companion object {
            const val TICK_MS = 100L
            const val STOP_MS = 5_000L
        }
    }
