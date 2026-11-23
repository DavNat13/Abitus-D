package com.abitusd.app.feature.workout.domain

import com.abitusd.app.core.database.session.SetEntity

/** One reversible set change: the row before and after (10f: undo and redo, also offline). */
data class SetChange(
    val before: SetEntity,
    val after: SetEntity,
)

/** Bounded undo and redo history of set changes during a session. */
class UndoStack(
    private val limit: Int = DEFAULT_LIMIT,
) {
    private val undo = ArrayDeque<SetChange>()
    private val redo = ArrayDeque<SetChange>()

    val canUndo get() = undo.isNotEmpty()
    val canRedo get() = redo.isNotEmpty()

    fun record(change: SetChange) {
        undo.addLast(change)
        if (undo.size > limit) undo.removeFirst()
        redo.clear()
    }

    /** Returns the row to write back, or null when there is nothing to undo. */
    fun undo(): SetEntity? {
        val change = undo.removeLastOrNull() ?: return null
        redo.addLast(change)
        return change.before
    }

    fun redo(): SetEntity? {
        val change = redo.removeLastOrNull() ?: return null
        undo.addLast(change)
        return change.after
    }

    private companion object {
        const val DEFAULT_LIMIT = 50
    }
}
