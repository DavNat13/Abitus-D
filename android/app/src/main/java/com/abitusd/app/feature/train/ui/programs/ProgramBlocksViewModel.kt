package com.abitusd.app.feature.train.ui.programs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abitusd.app.core.database.planning.ProgramBlockEntity
import com.abitusd.app.feature.train.data.BlockRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Blocks of one saved program; edits are written at once (offline-first, synced later). */
@HiltViewModel(assistedFactory = ProgramBlocksViewModel.Factory::class)
class ProgramBlocksViewModel
    @AssistedInject
    constructor(
        @Assisted private val programId: String,
        private val repository: BlockRepository,
    ) : ViewModel() {
        val blocks: StateFlow<List<ProgramBlockEntity>> =
            repository.blocks(programId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_MS), emptyList())

        fun addMesocycle() = write { it + BlockRepository.mesocycle(programId) }

        fun add() = write { it + BlockRepository.newBlock(programId) }

        fun update(block: ProgramBlockEntity) = write { list -> list.map { if (it.id == block.id) block else it } }

        fun remove(id: String) = write { list -> list.filterNot { it.id == id } }

        fun move(
            id: String,
            by: Int,
        ) = write { list ->
            val from = list.indexOfFirst { it.id == id }
            val to = from + by
            if (from < 0 || to !in list.indices) {
                list
            } else {
                list.toMutableList().apply { add(to, removeAt(from)) }
            }
        }

        private fun write(change: (List<ProgramBlockEntity>) -> List<ProgramBlockEntity>) {
            viewModelScope.launch {
                val next = change(blocks.value).take(MAX_BLOCKS)
                repository.replace(programId, next)
            }
        }

        @AssistedFactory
        interface Factory {
            fun create(programId: String): ProgramBlocksViewModel
        }

        private companion object {
            const val STOP_MS = 5_000L
            const val MAX_BLOCKS = 21
        }
    }
