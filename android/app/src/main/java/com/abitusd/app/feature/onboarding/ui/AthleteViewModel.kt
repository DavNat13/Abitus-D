package com.abitusd.app.feature.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.database.athlete.AthleteProfileEntity
import com.abitusd.app.feature.onboarding.data.AthleteRepository
import com.abitusd.app.feature.onboarding.domain.valid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AthleteState(
    val profile: AthleteProfileEntity? = null,
    val step: Int = 0,
    val busy: Boolean = false,
    val error: Int? = null,
    val finished: Boolean = false,
)

@HiltViewModel
class AthleteViewModel
    @Inject
    constructor(
        private val repository: AthleteRepository,
    ) : ViewModel() {
        private val mutableState = MutableStateFlow(AthleteState())
        val state = mutableState.asStateFlow()

        init {
            load()
        }

        fun load() {
            viewModelScope.launch {
                attempt { mutableState.value = AthleteState(profile = repository.load()) }
            }
        }

        fun change(profile: AthleteProfileEntity) {
            if (!state.value.busy) mutableState.value = state.value.copy(profile = profile, error = null)
        }

        fun next(
            skip: Boolean = false,
            finish: Boolean = false,
        ) {
            val current = state.value
            val profile = current.profile
            if (profile == null || current.busy) return
            if (!skip && !profile.valid()) {
                mutableState.value = current.copy(error = R.string.athlete_error)
                return
            }
            mutableState.value = current.copy(busy = true, error = null)
            viewModelScope.launch {
                attempt {
                    val row = if (skip) repository.load() else profile
                    val done = finish || current.step == LAST_STEP
                    val saved = row.copy(onboardingDone = row.onboardingDone || done)
                    repository.save(saved)
                    mutableState.value =
                        AthleteState(saved, if (done) current.step else current.step + 1, finished = done)
                }
            }
        }

        private suspend fun attempt(block: suspend () -> Unit) {
            try {
                block()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.value = state.value.copy(busy = false, error = R.string.athlete_save_error)
            }
        }

        companion object {
            const val LAST_STEP = 4
        }
    }
