package com.abitusd.app.feature.clock.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.core.database.clock.TimerProfileEntity
import com.abitusd.app.core.database.planning.RoutineEntity
import com.abitusd.app.feature.clock.data.ClockController
import com.abitusd.app.feature.clock.data.ClockLink
import com.abitusd.app.feature.clock.data.ClockProfiles
import com.abitusd.app.feature.clock.domain.ClockEngine
import com.abitusd.app.feature.clock.domain.ClockMode
import com.abitusd.app.feature.clock.domain.ClockPreset
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

@HiltViewModel
class ClockViewModel
    @Inject
    constructor(
        private val controller: ClockController,
        private val profiles: ClockProfiles,
        private val link: ClockLink,
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
            combine(
                controller.store.run,
                ticker,
                draft,
                combine(profiles.all, link.routines, ::Pair),
                silent,
            ) { run, now, d, p, s ->
                ClockUiState(run, run?.let { ClockEngine.status(it, now) }, d, p.first, s.first, s.second, p.second)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), ClockUiState())

        init {
            controller.store.refresh()
            ClockPending.label?.let { draft.value = draft.value.copy(name = it) }
        }

        fun fromRoutine(routine: RoutineEntity) {
            viewModelScope.launch {
                draft.value =
                    ClockDraft(null, null, routine.name, ClockMode.CUSTOM, link.routineSpec(routine.id))
            }
        }

        fun choosePreset(preset: ClockPreset) {
            val key = preset.key
            draft.value = ClockDraft(key, null, "", preset.mode, preset.spec)
        }

        fun chooseProfile(profile: TimerProfileEntity) {
            val spec = profiles.spec(profile) ?: return
            draft.value = ClockDraft(null, profile.id, profile.name, ClockMode.of(profile.mode), spec)
        }

        fun edit(spec: ClockSpec) {
            draft.value = draft.value.copy(spec = spec)
        }

        /** [leadS] seconds of get-ready countdown before the first phase (11b). */
        fun start(
            name: String,
            leadS: Int = 0,
        ) {
            val d = draft.value
            controller.start(ClockRun(name, d.mode.key, d.spec, System.currentTimeMillis() + leadS * MS))
        }

        /** Applies an engine change (pause, resume, +15 s, skip, lap) and re-arms the alarms. */
        fun act(change: (ClockRun, Long) -> ClockRun) = controller.update(change)

        fun stop() {
            controller.store.run.value
                ?.takeIf { it.lapsMs.isNotEmpty() }
                ?.let { r -> viewModelScope.launch { link.saveLaps(r.name, r.lapsMs) } }
            controller.stop()
        }

        /** Starts the stopwatch unless a clock is already running. */
        fun startStopwatch(name: String) {
            if (controller.store.refresh() != null) return
            choosePreset(ClockPresets.all.first { it.key == "stopwatch" })
            start(name)
        }

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
            if (draft.value.profileId == id) choosePreset(ClockPresets.all.first { it.key == "tabata" })
        }

        private companion object {
            const val TICK_MS = 100L
            const val MS = 1000L
            const val STOP_MS = 5_000L
        }
    }
