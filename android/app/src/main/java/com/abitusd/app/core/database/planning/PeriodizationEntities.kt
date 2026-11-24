package com.abitusd.app.core.database.planning

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Advanced planning rows (10f Fase 4b), entity and wire row at once like the other planning rows.
 * A block is a run of consecutive weeks of its program, ordered by [position].
 */
@Serializable
@Entity(tableName = "program_blocks", indices = [Index("programId"), Index("dirty")])
data class ProgramBlockEntity(
    @PrimaryKey override val id: String,
    val programId: String,
    val position: Int,
    val name: String = "",
    val goal: String = BlockGoal.GENERAL,
    val phase: String = BlockPhase.ACCUMULATION,
    val weeks: Int,
    val progression: String = Progression.NONE,
    val step: Double? = null,
    override val updatedAt: Long,
    override val deletedAt: Long? = null,
    override val dirty: Boolean = false,
    val serverSeq: Long = 0,
) : SyncRow

/** In [blockId], the planned exercise [fromExerciseId] is trained as [toExerciseId]. */
@Serializable
@Entity(tableName = "block_substitutions", indices = [Index("blockId"), Index("dirty")])
data class BlockSubstitutionEntity(
    @PrimaryKey override val id: String,
    val blockId: String,
    val fromExerciseId: String,
    val toExerciseId: String,
    override val updatedAt: Long,
    override val deletedAt: Long? = null,
    override val dirty: Boolean = false,
    val serverSeq: Long = 0,
) : SyncRow

/** Weekly set target of one muscle group. */
@Serializable
@Entity(tableName = "muscle_targets", indices = [Index("dirty")])
data class MuscleTargetEntity(
    @PrimaryKey override val id: String,
    val muscleGroupId: Int,
    val minSets: Int,
    val maxSets: Int,
    override val updatedAt: Long,
    override val deletedAt: Long? = null,
    override val dirty: Boolean = false,
    val serverSeq: Long = 0,
) : SyncRow

object BlockGoal {
    const val GENERAL = "general"
    val ALL = listOf(GENERAL, "strength", "hypertrophy", "endurance", "skill")
}

object BlockPhase {
    const val ACCUMULATION = "accumulation"
    const val INTENSIFICATION = "intensification"
    const val DELOAD = "deload"
    const val TEST = "test"
    val ALL = listOf(ACCUMULATION, INTENSIFICATION, DELOAD, TEST)
}

object Progression {
    const val NONE = "none"
    const val DOUBLE = "double"
    const val PERCENT = "percent"
    const val RPE = "rpe"
    val ALL = listOf(NONE, DOUBLE, PERCENT, RPE)
}
