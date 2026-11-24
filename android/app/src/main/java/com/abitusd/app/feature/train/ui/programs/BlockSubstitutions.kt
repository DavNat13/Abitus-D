package com.abitusd.app.feature.train.ui.programs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.abitusd.app.R
import com.abitusd.app.core.database.planning.BlockSubstitutionEntity
import com.abitusd.app.core.database.planning.PeriodizationDao
import com.abitusd.app.core.designsystem.component.AppDropdown
import com.abitusd.app.core.designsystem.component.ChoiceOption
import com.abitusd.app.core.designsystem.component.SectionHeader
import com.abitusd.app.core.sync.SyncScheduler
import com.abitusd.app.feature.library.domain.ExerciseNames
import com.abitusd.app.feature.train.data.ProgramRepository
import com.abitusd.app.feature.train.data.RoutineRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/** Exercises of the program, their related options and the block's current choices. */
data class SubstitutionState(
    val planned: List<ChoiceOption<String>> = emptyList(),
    val options: Map<String, List<ChoiceOption<String>>> = emptyMap(),
    val chosen: Map<String, BlockSubstitutionEntity> = emptyMap(),
)

@HiltViewModel(assistedFactory = BlockSubstitutionsViewModel.Factory::class)
class BlockSubstitutionsViewModel
    @AssistedInject
    constructor(
        @Assisted("program") private val programId: String,
        @Assisted("block") private val blockId: String,
        private val programs: ProgramRepository,
        private val routines: RoutineRepository,
        private val dao: PeriodizationDao,
        private val names: ExerciseNames,
        private val syncScheduler: SyncScheduler,
    ) : ViewModel() {
        private val mutable = MutableStateFlow(SubstitutionState())
        val state = mutable.asStateFlow()

        init {
            viewModelScope.launch {
                val all = names.all()
                val routineIds = programs.daysOnce(programId).mapNotNull { it.routineId }.distinct()
                val ids = routineIds.flatMap { routines.exercisesOnce(it) }.map { it.exerciseId }.distinct()
                val related = dao.swapRelations(ids).groupBy({ it.exerciseId }, { it.relatedId })
                mutable.update {
                    SubstitutionState(
                        planned = ids.filter { id -> related[id] != null }.map { ChoiceOption(it, all[it].orEmpty()) },
                        options =
                            related.mapValues { (_, list) ->
                                list.distinct().map { ChoiceOption(it, all[it].orEmpty()) }
                            },
                        chosen = dao.substitutionsOnce(blockId).associateBy { it.fromExerciseId },
                    )
                }
            }
        }

        /** [to] null keeps the planned exercise in this block. */
        fun choose(
            from: String,
            to: String?,
        ) {
            val now = System.currentTimeMillis()
            val old = state.value.chosen[from]
            val row =
                when {
                    to == null -> old?.copy(deletedAt = now)
                    old != null -> old.copy(toExerciseId = to, deletedAt = null)
                    else -> BlockSubstitutionEntity(UUID.randomUUID().toString(), blockId, from, to, now)
                } ?: return
            val saved = row.copy(updatedAt = now, dirty = true)
            mutable.update { s -> s.copy(chosen = if (to == null) s.chosen - from else s.chosen + (from to saved)) }
            viewModelScope.launch {
                dao.upsertSubstitutions(listOf(saved))
                syncScheduler.requestSync()
            }
        }

        @AssistedFactory
        interface Factory {
            fun create(
                @Assisted("program") programId: String,
                @Assisted("block") blockId: String,
            ): BlockSubstitutionsViewModel
        }
    }

/** One dropdown per program exercise that has related options: what to train in this block. */
@Composable
fun BlockSubstitutions(
    programId: String,
    blockId: String,
    viewModel: BlockSubstitutionsViewModel =
        hiltViewModel<BlockSubstitutionsViewModel, BlockSubstitutionsViewModel.Factory>(
            key = "subs-$blockId",
            creationCallback = { it.create(programId, blockId) },
        ),
) {
    val s by viewModel.state.collectAsStateWithLifecycle()
    if (s.planned.isEmpty()) return
    SectionHeader(stringResource(R.string.blocks_substitutions))
    s.planned.forEach { from ->
        AppDropdown(
            from.label,
            s.options[from.value].orEmpty(),
            s.chosen[from.value]?.toExerciseId,
            { viewModel.choose(from.value, it) },
            noneLabel = stringResource(R.string.blocks_keep_exercise),
        )
    }
}
