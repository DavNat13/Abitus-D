package com.abitusd.app.core.database.catalog

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/** Catalog reference tables (08b). Read-only copies of the server catalog. */
@Entity(tableName = "disciplines")
data class DisciplineEntity(
    @PrimaryKey val id: Int,
    val slug: String,
    val sortOrder: Int,
    val iconKey: String,
)

@Entity(tableName = "discipline_texts", primaryKeys = ["disciplineId", "locale"])
data class DisciplineTextEntity(
    val disciplineId: Int,
    val locale: String,
    val name: String,
    val description: String,
)

@Entity(tableName = "muscle_groups")
data class MuscleGroupEntity(
    @PrimaryKey val id: Int,
    val slug: String,
    val bodyRegion: String,
    val view: String,
    val svgKey: String,
    val meshKey: String,
    val sortOrder: Int,
)

@Entity(tableName = "muscle_group_texts", primaryKeys = ["muscleGroupId", "locale"])
data class MuscleGroupTextEntity(
    val muscleGroupId: Int,
    val locale: String,
    val name: String,
)

@Entity(tableName = "equipment")
data class EquipmentEntity(
    @PrimaryKey val id: Int,
    val slug: String,
)

@Entity(tableName = "equipment_texts", primaryKeys = ["equipmentId", "locale"])
data class EquipmentTextEntity(
    val equipmentId: Int,
    val locale: String,
    val name: String,
)
