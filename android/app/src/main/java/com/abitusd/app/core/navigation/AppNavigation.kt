package com.abitusd.app.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.core.session.SessionManager
import com.abitusd.app.core.session.SessionState
import com.abitusd.app.core.sync.SyncScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel
    @Inject
    constructor(
        sessions: SessionManager,
        syncScheduler: SyncScheduler,
    ) : ViewModel() {
        val session: StateFlow<SessionState> = sessions.state

        init {
            viewModelScope.launch { sessions.restore() }
            // Every sign-in (or app start with a session) syncs catalog and own data.
            viewModelScope.launch {
                sessions.state.filterIsInstance<SessionState.SignedIn>().collect { syncScheduler.requestSync() }
            }
        }
    }

/** The stored session decides the start: access flow or the app (09-FRONTEND-ANDROID.md). */
@Composable
fun AppNavigation(viewModel: RootViewModel = hiltViewModel()) {
    val session by viewModel.session.collectAsStateWithLifecycle()
    when (session) {
        SessionState.Loading -> {
            Box(Modifier.fillMaxSize().background(AppTheme.colors.background))
        }

        SessionState.SignedOut -> {
            AuthNavigation()
        }

        is SessionState.SignedIn -> {
            com.abitusd.app.feature.onboarding.ui
                .AthleteGate((session as SessionState.SignedIn).userId) { MainNavigation() }
        }
    }
}
