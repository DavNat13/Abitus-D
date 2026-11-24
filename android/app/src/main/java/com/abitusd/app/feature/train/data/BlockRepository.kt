package com.abitusd.app.feature.train.data

import com.abitusd.app.core.database.planning.BlockPhase
import com.abitusd.app.core.database.planning.PeriodizationDao
import com.abitusd.app.core.database.planning.ProgramBlockEntity
import com.abitusd.app.core.database.planning.Progression
import com.abitusd.app.core.sync.SyncScheduler
import com.abitusd.app.feature.train.domain.BlockSpec
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Blocks of a program (10f Fase 4b). Every write marks rows dirty and asks for a sync. */
@Singleton
class BlockRepository
    @Inject
    constructor(
        private val dao: PeriodizationDao,
        private val syncScheduler: SyncScheduler,
    ) {
        fun blocks(programId: String) = dao.blocks(programId)

        suspend fun blocksOnce(programId: String) = dao.blocksOnce(programId)

        suspend fun substitutions(blockId: String) =
            dao.substitutionsOnce(blockId).associate {
                it.fromExerciseId to
                    it.toExerciseId
            }

        /** Replaces the blocks of [programId]: missing ones become tombstones, positions follow the list. */
        suspend fun replace(
            programId: String,
            blocks: List<ProgramBlockEntity>,
        ) {
            val now = System.currentTimeMillis()
            val keepIds = blocks.map { it.id }.toSet()
            val removed =
                dao.blocksOnce(programId).filter { it.id !in keepIds }.map {
                    it.copy(deletedAt = now, updatedAt = now, dirty = true)
                }
            val kept =
                blocks.mapIndexed { index, block ->
                    block.copy(programId = programId, position = index, updatedAt = now, dirty = true)
                }
            dao.upsertBlocks(removed + kept)
            syncScheduler.requestSync()
        }

        companion object {
            fun newBlock(
                programId: String,
                phase: String = BlockPhase.ACCUMULATION,
                weeks: Int = 1,
                progression: String = Progression.NONE,
                step: Double? = null,
            ) = ProgramBlockEntity(
                id = UUID.randomUUID().toString(),
                programId = programId,
                position = 0,
                phase = phase,
                weeks = weeks,
                progression = progression,
                step = step,
                updatedAt = 0,
            )

            /** Ready-made mesocycle: three accumulation weeks with double progression and a deload week. */
            fun mesocycle(programId: String) =
                listOf(
                    newBlock(programId, BlockPhase.ACCUMULATION, MESO_WEEKS, Progression.DOUBLE, DEFAULT_STEP_KG),
                    newBlock(programId, BlockPhase.DELOAD, 1),
                )

            fun spec(block: ProgramBlockEntity) =
                BlockSpec(block.id, block.position, block.phase, block.weeks, block.progression, block.step)

            private const val MESO_WEEKS = 3
            private const val DEFAULT_STEP_KG = 2.5
        }
    }
