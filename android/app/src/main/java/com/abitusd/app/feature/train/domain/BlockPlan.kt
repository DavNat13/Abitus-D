package com.abitusd.app.feature.train.domain

/** One block as the planner needs it, ordered by [position]. */
data class BlockSpec(
    val id: String,
    val position: Int,
    val phase: String,
    val weeks: Int,
    val progression: String,
    val step: Double?,
)

/** Where a program week falls: its block and the 0-based week inside that block. */
data class BlockWeek(
    val block: BlockSpec,
    val weekInBlock: Int,
    val programWeek: Int,
)

/** Maps program weeks to blocks (10f Fase 4b). Weeks after the last block keep the last block. */
object BlockPlan {
    fun totalWeeks(blocks: List<BlockSpec>): Int = blocks.sumOf { it.weeks }

    fun at(
        blocks: List<BlockSpec>,
        programWeek: Int,
    ): BlockWeek? {
        val ordered = blocks.sortedBy { it.position }
        var start = 0
        var found: BlockWeek? = null
        for (block in ordered) {
            if (found == null && programWeek < start + block.weeks) {
                found = BlockWeek(block, programWeek - start, programWeek)
            }
            start += block.weeks
        }
        val last = ordered.lastOrNull()
        return when {
            programWeek < 0 -> null
            found != null -> found
            last != null -> BlockWeek(last, last.weeks - 1, programWeek)
            else -> null
        }
    }

    /** Program week (0-based) of a day, counted from the program start in epoch days. */
    fun programWeek(
        startEpochDay: Long,
        epochDay: Long,
    ): Int = Math.floorDiv(epochDay - startEpochDay, DAYS_PER_WEEK).toInt()

    /** Start week of every block, for the timeline. */
    fun starts(blocks: List<BlockSpec>): List<Pair<BlockSpec, Int>> {
        var start = 0
        return blocks.sortedBy { it.position }.map { block -> (block to start).also { start += block.weeks } }
    }

    private const val DAYS_PER_WEEK = 7L
}
