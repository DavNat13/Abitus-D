package com.abitusd.app.feature.train.ui.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.MuscleTargetEntity
import com.abitusd.app.core.database.planning.PeriodizationDao
import com.abitusd.app.core.designsystem.component.AppBottomSheet
import com.abitusd.app.core.designsystem.component.AppButton
import com.abitusd.app.core.designsystem.component.AppButtonStyle
import com.abitusd.app.core.designsystem.component.ListItemCard
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.component.VolumeBars
import com.abitusd.app.core.designsystem.icon.AppIcons
import com.abitusd.app.core.sync.SyncScheduler
import com.abitusd.app.feature.train.data.MuscleVolume
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/** Weekly set targets per muscle group (10f Fase 4b), stored offline and synced. */
@HiltViewModel
class MuscleTargetsViewModel
    @Inject
    constructor(
        private val dao: PeriodizationDao,
        private val syncScheduler: SyncScheduler,
    ) : ViewModel() {
        val targets =
            dao
                .targets()
                .map { rows -> rows.associateBy { it.muscleGroupId } }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), emptyMap())

        /** Saves [range] for [muscleId]; null removes the target. */
        fun set(
            muscleId: Int,
            range: IntRange?,
        ) {
            viewModelScope.launch {
                val now = System.currentTimeMillis()
                val old = targets.value[muscleId]
                val row =
                    when {
                        range == null -> old?.copy(deletedAt = now)
                        old != null -> old.copy(minSets = range.first, maxSets = range.last)
                        else -> MuscleTargetEntity(UUID.randomUUID().toString(), muscleId, range.first, range.last, now)
                    } ?: return@launch
                dao.upsertTargets(listOf(row.copy(updatedAt = now, dirty = true)))
                syncScheduler.requestSync()
            }
        }

        private companion object {
            const val STOP_MS = 5_000L
        }
    }

/** Planned sets of the week against the targets, with a sheet to edit them. */
@Composable
fun WeekVolumeCard(
    volume: List<MuscleVolume>,
    viewModel: MuscleTargetsViewModel = hiltViewModel(),
) {
    val targets by viewModel.targets.collectAsStateWithLifecycle()
    var open by rememberSaveable { mutableStateOf(false) }
    var editing by rememberSaveable { mutableStateOf<Int?>(null) }
    SectionCard(stringResource(R.string.calendar_week_volume), icon = AppIcons.Muscles) {
        VolumeBars(
            volume.map { it.name to it.sets },
            targets = volume.map { v -> targets[v.id]?.let { it.minSets..it.maxSets } },
        )
        AppButton(stringResource(R.string.targets_open), {
            open = true
        }, style = AppButtonStyle.Text, icon = AppIcons.Progression)
    }
    if (open) {
        AppBottomSheet(stringResource(R.string.targets_title), { open = false }) {
            volume.forEach { v ->
                val meta =
                    targets[v.id]?.let { stringResource(R.string.targets_range, it.minSets, it.maxSets) }
                        ?: stringResource(R.string.targets_none)
                ListItemCard(v.name, { editing = v.id }, icon = AppIcons.Muscles, meta = meta)
            }
        }
    }
    editing?.let { id ->
        val current = targets[id]?.let { it.minSets..it.maxSets }
        TargetSheet(
            volume.firstOrNull { it.id == id }?.name.orEmpty(),
            current,
            { editing = null },
        ) { viewModel.set(id, it) }
    }
}
