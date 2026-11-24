package com.abitusd.app.feature.workout.ui.hub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.PeriodizationDao
import com.abitusd.app.core.designsystem.component.SectionCard
import com.abitusd.app.core.designsystem.component.StatusBanner
import com.abitusd.app.core.designsystem.component.StatusKind
import com.abitusd.app.feature.train.data.BlockRepository
import com.abitusd.app.feature.train.data.ProgramRepository
import com.abitusd.app.feature.train.domain.BlockPlan
import com.abitusd.app.feature.train.domain.BlockSpec
import com.abitusd.app.feature.train.domain.BlockWeek
import com.abitusd.app.feature.train.domain.DeloadAdvisor
import com.abitusd.app.feature.train.ui.programs.BlockTimeline
import com.abitusd.app.feature.train.ui.programs.phaseIcon
import com.abitusd.app.feature.train.ui.programs.phaseLabel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

data class BlockProgress(
    val blocks: List<BlockSpec> = emptyList(),
    val week: BlockWeek? = null,
    val deload: Boolean = false,
)

/** Where today falls in the active program's blocks, and whether a deload is advisable. */
@HiltViewModel
class BlockProgressViewModel
    @Inject
    constructor(
        programs: ProgramRepository,
        blocks: BlockRepository,
        dao: PeriodizationDao,
    ) : ViewModel() {
        @OptIn(ExperimentalCoroutinesApi::class)
        private val active =
            programs
                .programs()
                .map { list ->
                    list.firstOrNull { it.isActive && it.startDate != null }
                }.flatMapLatest { program ->
                    if (program == null) {
                        flowOf(null)
                    } else {
                        blocks.blocks(program.id).map { rows ->
                            val start = LocalDate.parse(program.startDate).toEpochDay()
                            rows.map(BlockRepository::spec) to
                                BlockPlan.programWeek(start, LocalDate.now().toEpochDay())
                        }
                    }
                }

        val state =
            combine(active, dao.recentSessionRpe(DEFAULT_WINDOW)) { plan, rpe ->
                val specs = plan?.first.orEmpty()
                val week = plan?.let { BlockPlan.at(specs, it.second) }
                BlockProgress(specs, week, specs.isNotEmpty() && DeloadAdvisor.suggest(rpe.reversed()))
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), BlockProgress())

        private companion object {
            const val STOP_MS = 5_000L
            const val DEFAULT_WINDOW = 3
        }
    }

/** Compact block timeline for Train: "Week 3 of 5, accumulation" plus the deload advice. */
@Composable
fun BlockProgressCard(viewModel: BlockProgressViewModel = hiltViewModel()) {
    val s by viewModel.state.collectAsStateWithLifecycle()
    val week = s.week ?: return
    val starts = BlockPlan.starts(s.blocks).toMap()
    val total = BlockPlan.totalWeeks(s.blocks)
    val phase = stringResource(phaseLabel(week.block.phase))
    val title =
        stringResource(R.string.blocks_week_of, minOf(week.programWeek + 1, total), total, phase.lowercase())
    SectionCard(title, icon = phaseIcon(week.block.phase)) {
        BlockTimeline(
            s.blocks,
            (starts[week.block] ?: 0) + week.weekInBlock,
            pluralStringResource(R.plurals.blocks_total_weeks, total, total),
        )
        if (s.deload) StatusBanner(stringResource(R.string.blocks_deload_suggested), StatusKind.Warning)
    }
}
