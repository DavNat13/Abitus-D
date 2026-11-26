package com.abitusd.app.core.database.clock

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.abitusd.app.core.database.planning.SyncRow
import kotlinx.serialization.Serializable

/** Id derived from the exercise, so the same favorite never duplicates across devices. */
@Serializable
@Entity(tableName = "exercise_favorites", indices = [Index("exerciseId"), Index("dirty")])
data class ExerciseFavoriteEntity(
    @PrimaryKey override val id: String,
    val exerciseId: String,
    override val updatedAt: Long,
    override val deletedAt: Long? = null,
    override val dirty: Boolean = false,
) : SyncRow

/** Saved clock configuration; [spec] is the JSON of phases and rounds. */
@Serializable
@Entity(tableName = "timer_profiles", indices = [Index("dirty")])
data class TimerProfileEntity(
    @PrimaryKey override val id: String,
    val name: String,
    val mode: String,
    val spec: String,
    override val updatedAt: Long,
    override val deletedAt: Long? = null,
    override val dirty: Boolean = false,
) : SyncRow
