package com.abitusd.app.feature.profile.ui

import android.text.format.DateUtils
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.database.SyncStateDao
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.core.sync.SyncEngine
import com.abitusd.app.core.sync.SyncScheduler
import com.abitusd.app.core.sync.SyncTableAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SyncStatus(
    val pending: Int = 0,
    val lastAt: Long? = null,
)

@HiltViewModel
class SyncStatusViewModel
    @Inject
    constructor(
        private val adapters: List<@JvmSuppressWildcards SyncTableAdapter>,
        private val syncState: SyncStateDao,
        private val scheduler: SyncScheduler,
    ) : ViewModel() {
        val status = MutableStateFlow(SyncStatus())

        suspend fun refresh() {
            val pending = adapters.sumOf { it.pending().size }
            status.value = SyncStatus(pending, syncState.get(SyncEngine.LAST_SYNC_KEY)?.toLongOrNull())
        }

        fun retry() {
            scheduler.requestSync()
            viewModelScope.launch {
                delay(RETRY_REFRESH_MS)
                refresh()
            }
        }

        private companion object {
            const val RETRY_REFRESH_MS = 3_000L
        }
    }

/** Pending changes, last successful sync and a retry button (11f). */
@Composable
fun SyncStatusCard(viewModel: SyncStatusViewModel = hiltViewModel()) {
    val s by viewModel.status.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.refresh() }
    SectionCard(stringResource(R.string.sync_title), icon = if (s.pending > 0) AppIcons.Offline else AppIcons.Sync) {
        val pending =
            if (s.pending == 0) {
                stringResource(R.string.sync_all_sent)
            } else {
                pluralStringResource(R.plurals.sync_pending, s.pending, s.pending)
            }
        Text(pending, style = AppTheme.typography.body, color = AppTheme.colors.textPrimary)
        val last = s.lastAt?.let { DateUtils.getRelativeTimeSpanString(it).toString() }
        Text(
            last?.let { stringResource(R.string.sync_last, it) } ?: stringResource(R.string.sync_never),
            style = AppTheme.typography.body,
            color = AppTheme.colors.textSecondary,
        )
        AppButton(
            stringResource(R.string.sync_retry),
            viewModel::retry,
            style = AppButtonStyle.Tonal,
            icon = AppIcons.Sync,
        )
    }
}
