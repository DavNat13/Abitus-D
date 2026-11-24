package com.abitusd.app.core.database

import androidx.room3.AutoMigration
import androidx.room3.ColumnTypeConverters
import androidx.room3.Dao
import androidx.room3.Database
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.RoomDatabase
import androidx.room3.Upsert
import com.abitusd.app.core.database.catalog.CatalogWriteDao
import com.abitusd.app.core.database.catalog.DisciplineEntity
import com.abitusd.app.core.database.catalog.DisciplineTextEntity
import com.abitusd.app.core.database.catalog.EquipmentEntity
import com.abitusd.app.core.database.catalog.EquipmentTextEntity
import com.abitusd.app.core.database.catalog.ExerciseEntity
import com.abitusd.app.core.database.catalog.ExerciseEquipmentEntity
import com.abitusd.app.core.database.catalog.ExerciseMuscleEntity
import com.abitusd.app.core.database.catalog.ExerciseRelationEntity
import com.abitusd.app.core.database.catalog.ExerciseTextEntity
import com.abitusd.app.core.database.catalog.LibraryDao
import com.abitusd.app.core.database.catalog.MuscleGroupEntity
import com.abitusd.app.core.database.catalog.MuscleGroupTextEntity
import com.abitusd.app.core.database.catalog.StringListConverter
import com.abitusd.app.core.database.planning.PlanningDao
import com.abitusd.app.core.database.planning.ProgramDayEntity
import com.abitusd.app.core.database.planning.ProgramEntity
import com.abitusd.app.core.database.planning.RoutineEntity
import com.abitusd.app.core.database.planning.RoutineExerciseEntity
import com.abitusd.app.core.database.planning.ScheduledWorkoutEntity
import com.abitusd.app.core.database.session.ActiveSessionEntity
import com.abitusd.app.core.database.session.SessionDao
import com.abitusd.app.core.database.session.SessionExerciseEntity
import com.abitusd.app.core.database.session.SetEntity
import com.abitusd.app.core.database.session.WorkoutSessionEntity

/**
 * Local source of truth (09b-FRONTEND-OFFLINE-SYNC.md). v2: catalog and own exercises;
 * v3: planning. Schemas are exported to app/schemas.
 */
@Database(
    entities = [
        SyncStateEntity::class,
        DisciplineEntity::class,
        DisciplineTextEntity::class,
        MuscleGroupEntity::class,
        MuscleGroupTextEntity::class,
        EquipmentEntity::class,
        EquipmentTextEntity::class,
        ExerciseEntity::class,
        ExerciseTextEntity::class,
        ExerciseMuscleEntity::class,
        ExerciseEquipmentEntity::class,
        ExerciseRelationEntity::class,
        RoutineEntity::class,
        RoutineExerciseEntity::class,
        ProgramEntity::class,
        ProgramDayEntity::class,
        ScheduledWorkoutEntity::class,
        WorkoutSessionEntity::class,
        SessionExerciseEntity::class,
        SetEntity::class,
        ActiveSessionEntity::class,
        com.abitusd.app.core.database.planning.ProgramBlockEntity::class,
        com.abitusd.app.core.database.planning.BlockSubstitutionEntity::class,
        com.abitusd.app.core.database.planning.MuscleTargetEntity::class,
    ],
    version = 5,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
        ), AutoMigration(from = 2, to = 3), AutoMigration(from = 3, to = 4),
    ],
)
@ColumnTypeConverters(StringListConverter::class)
abstract class AbitusDatabase : RoomDatabase() {
    abstract fun syncStateDao(): SyncStateDao

    abstract fun catalogWriteDao(): CatalogWriteDao

    abstract fun libraryDao(): LibraryDao

    abstract fun planningDao(): PlanningDao

    abstract fun sessionDao(): SessionDao

    abstract fun periodizationDao(): com.abitusd.app.core.database.planning.PeriodizationDao

    abstract fun workoutHistoryDao(): com.abitusd.app.core.database.session.WorkoutHistoryDao

    companion object {
        const val NAME = "abitusd.db"
    }
}

/** Key value pairs of the sync engine: pull cursor, catalog version. */
@Entity(tableName = "sync_state")
data class SyncStateEntity(
    @PrimaryKey val key: String,
    val value: String,
)

@Dao
interface SyncStateDao {
    @Query("SELECT value FROM sync_state WHERE `key` = :key")
    suspend fun get(key: String): String?

    @Upsert
    suspend fun put(entity: SyncStateEntity)

    @Query("DELETE FROM sync_state")
    suspend fun clear()
}
