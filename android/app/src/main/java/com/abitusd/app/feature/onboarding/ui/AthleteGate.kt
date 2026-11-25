package com.abitusd.app.feature.onboarding.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.feature.onboarding.data.AthleteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AthleteGateViewModel
    @Inject
    constructor(
        repository: AthleteRepository,
    ) : ViewModel() {
        val completed =
            repository.profile
                .map { it?.onboardingDone == true }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT), null)

        private companion object {
            const val SUBSCRIPTION_TIMEOUT = 5000L
        }
    }

@Composable
fun AthleteGate(
    userId: String,
    viewModel: AthleteGateViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {
    val completed by viewModel.completed.collectAsStateWithLifecycle()
    when (completed) {
        true -> content()
        false -> AthleteScreen(onDone = {}, viewModel = hiltViewModel(key = "athlete-$userId"))
        null -> Unit
    }
}
