package com.abitusd.app.feature.workout.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.database.session.WorkoutHistoryDao
import com.abitusd.app.core.designsystem.component.EmptyState
import com.abitusd.app.core.designsystem.component.ListItemCard
import com.abitusd.app.core.designsystem.component.ScreenHeader
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.designsystem.motion.staggeredEnter
import com.abitusd.app.core.designsystem.theme.AppSpacing
import com.abitusd.app.core.designsystem.theme.AppTheme
import com.abitusd.app.feature.workout.data.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel
    @Inject
    constructor(
        workouts: WorkoutRepository,
        dao: WorkoutHistoryDao,
    ) : ViewModel() {
        val items =
            combine(workouts.history(), dao.setCounts()) { sessions, counts ->
                val bySession = counts.associate { it.sessionId to it.sets }
                sessions.map { it to (bySession[it.id] ?: 0) }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), emptyList())

        private companion object {
            const val STOP_MS = 5_000L
        }
    }

/** Finished sessions, newest first; each opens its summary in read-only mode. */
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0]
    val format = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", locale)
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        ScreenHeader(stringResource(R.string.workout_history)) {
            IconButton(onClick = onBack) {
                Icon(
                    painterResource(AppIcons.Close),
                    stringResource(R.string.common_back),
                    tint = AppTheme.colors.textPrimary,
                )
            }
        }
        if (items.isEmpty()) EmptyState(AppIcons.History, stringResource(R.string.history_empty))
        LazyColumn(
            contentPadding =
                PaddingValues(
                    start = AppSpacing.screenMargin,
                    end = AppSpacing.screenMargin,
                    bottom = AppSpacing.xl,
                ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
        ) {
            itemsIndexed(items, key = { _, item -> item.first.id }) { index, (session, sets) ->
                val day =
                    Instant
                        .ofEpochMilli(
                            session.startedAt,
                        ).atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(format)
                ListItemCard(
                    title = session.title,
                    onClick = { onOpen(session.id) },
                    modifier = Modifier.staggeredEnter(index),
                    icon = AppIcons.History,
                    meta = stringResource(R.string.history_meta, day.replaceFirstChar { it.titlecase(locale) }, sets),
                )
            }
        }
    }
}
